package io.github.truenine.composeserver.data.extract.service.impl

import io.github.truenine.composeserver.data.extract.api.ICnNbsAddressApi
import io.github.truenine.composeserver.data.extract.domain.CnDistrictCode
import io.github.truenine.composeserver.data.extract.service.ILazyAddressService
import io.github.truenine.composeserver.slf4j
import io.github.truenine.composeserver.string
import org.jsoup.Jsoup
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Service

private val log = slf4j<LazyAddressServiceImpl>()

@ConditionalOnMissingBean(ILazyAddressService::class)
@Deprecated("统计局接口已不可用，请使用基于 CSV 的实现，如 LazyAddressCsvServiceImpl")
@Service("DataExtractLazyAddressServiceImpls") // 保持 Bean 名称可能为了某些旧配置兼容
class LazyAddressServiceImpl(private val chstApi: ICnNbsAddressApi) : ILazyAddressService {

  // --- 实现 ILazyAddressService 属性 ---
  override val supportedDefaultYearVersion: String
    get() = "2023" // 国家统计局最后可用的年份（假设）

  override val supportedYearVersions: List<String>
    get() = listOf(supportedDefaultYearVersion)

  override val logger
    get() = log

  // --- 实现核心查找方法 (基于旧逻辑，接口已简化) ---

  override fun fetchChildren(parentCode: string, yearVersion: String): List<ILazyAddressService.CnDistrict> {
    // 检查年份是否匹配，这个旧实现只支持一个版本
    if (yearVersion != supportedDefaultYearVersion) {
      logger?.warn("Unsupported yearVersion {} requested for NBS service, returning empty list.", yearVersion)
      return emptyList()
    }

    // 尝试根据 parentCode 的层级调用旧的查找逻辑
    // 使用接口提供的公共方法创建 CnDistrictCode
    val parentCodeObj = ILazyAddressService.createCnDistrictCode(parentCode) ?: return emptyList()

    return when (parentCodeObj.level) {
      0 -> findAllProvincesInternal(yearVersion) // parent is country
      1 -> findAllCityByCodeInternal(parentCode, yearVersion) // parent is province
      2 -> findAllCountyByCodeInternal(parentCode, yearVersion) // parent is city
      3 -> findAllTownByCodeInternal(parentCode, yearVersion) // parent is county
      4 -> findAllVillageByCodeInternal(parentCode, yearVersion) // parent is town
      else ->
        emptyList<ILazyAddressService.CnDistrict>().also {
          logger?.warn("findChildren called with unsupported parent level: {} for code: {}", parentCodeObj.level, parentCode)
        }
    }
  }

  override fun fetchDistrict(code: string, yearVersion: String): ILazyAddressService.CnDistrict? {
    logger?.debug("Attempting findDistrict for code: {}, starting year: {}", code, yearVersion)
    // 实现版本回退逻辑
    var currentYearVersion: String? = yearVersion
    while (currentYearVersion != null) {
      // 检查当前尝试的版本是否受支持
      if (currentYearVersion != supportedDefaultYearVersion) {
        logger?.trace("Skipping unsupported year {} for NBS service.", currentYearVersion)
        currentYearVersion = lastYearVersionOrNull(currentYearVersion)
        continue
      }

      // 尝试在该版本查找
      // 需要找到父级，然后查找子级列表，再从中筛选
      val codeObj = ILazyAddressService.createCnDistrictCode(code) ?: return null // 无效 code 直接返回
      val parentCode = codeObj.back()?.code?.toString() ?: if (codeObj.level == 1) ILazyAddressService.DEFAULT_COUNTRY_CODE else null

      if (parentCode != null) {
        val children = fetchChildren(parentCode, currentYearVersion) // 调用本类实现的 findChildren
        val found = children.firstOrNull { it.code.code == codeObj.code }
        if (found != null) {
          logger?.debug("Found district {} in year {}", code, currentYearVersion)
          // 确保返回的 district 的 yearVersion 是实际找到的年份
          return found.copy(yearVersion = currentYearVersion)
        }
      } else {
        // 无法确定父级（可能是根代码 "0" 或无效代码）
        logger?.warn("Cannot determine parent for code {} to perform lookup.", code)
        return null // 或者根据情况处理根代码
      }

      // 如果没找到，尝试更早的版本
      logger?.trace("District {} not found in year {}, trying older version.", code, currentYearVersion)
      currentYearVersion = lastYearVersionOrNull(currentYearVersion)
    }

    logger?.warn("District {} not found in any supported version starting from {}.", code, yearVersion)
    return null // 所有版本都试过，未找到
  }

  override fun fetchChildrenRecursive(parentCode: string, maxDepth: Int, yearVersion: String): List<ILazyAddressService.CnDistrict> {
    logger?.warn("findChildrenRecursive is not efficiently implemented in the deprecated NBS service.")
    // 简单实现：只返回第一层子级，如果 maxDepth >= 1
    return if (maxDepth >= 1) {
      fetchChildren(parentCode, yearVersion)
    } else {
      emptyList()
    }
    // 或者抛出异常:
    // throw UnsupportedOperationException("Recursive search is not supported by
    // this deprecated implementation.")
  }

  override fun traverseChildrenRecursive(
    parentCode: string,
    maxDepth: Int,
    yearVersion: String,
    onVisit: (children: List<ILazyAddressService.CnDistrict>, depth: Int, parentDistrict: ILazyAddressService.CnDistrict?) -> Boolean,
  ) {
    fun walk(currentCode: String, depth: Int, parent: ILazyAddressService.CnDistrict?) {
      if (depth > maxDepth) return
      val children = fetchChildren(currentCode, yearVersion)
      if (children.isEmpty()) return
      val shouldContinue = onVisit(children, depth, parent)
      if (shouldContinue) {
        for (child in children) {
          if (!child.leaf) {
            walk(child.code.code, depth + 1, child)
          }
        }
      }
    }
    walk(parentCode, 1, null)
  }

  // --- 内部方法 (旧的实现逻辑，保持 private) ---

  private fun findAllProvincesInternal(yearVersion: String): List<ILazyAddressService.CnDistrict> {
    // 年份检查已在外部 findChildren 完成
    val homeBody = chstApi.homePage().body
    return extractProvinces(homeBody)
  }

  private fun findAllCityByCodeInternal(provinceCode: String, yearVersion: String): List<ILazyAddressService.CnDistrict> {
    // 年份检查已在外层完成
    // 使用接口的公共方法创建 Code 对象
    val model = ILazyAddressService.createCnDistrictCode(provinceCode) ?: return emptyList()
    // 简单的层级检查
    if (model.level != 1) {
      logger?.error("Internal: findAllCityByCodeInternal called with non-province code: {}", provinceCode)
      return emptyList()
    }
    val h = chstApi.getCityPage(model.provinceCode)
    return extractPlainItem("citytr", h.body) ?: listOf()
  }

  private fun findAllCountyByCodeInternal(cityCode: String, yearVersion: String): List<ILazyAddressService.CnDistrict> {
    // 年份检查已在外层完成
    val model = ILazyAddressService.createCnDistrictCode(cityCode) ?: return emptyList()
    if (model.level != 2) {
      logger?.error("Internal: findAllCountyByCodeInternal called with non-city code: {}", cityCode)
      return emptyList()
    }
    return extractPlainItem("countytr", chstApi.getCountyPage(model.provinceCode, model.cityCode).body) ?: listOf()
  }

  private fun findAllTownByCodeInternal(countyCode: String, yearVersion: String): List<ILazyAddressService.CnDistrict> {
    // 年份检查已在外层完成
    val model = ILazyAddressService.createCnDistrictCode(countyCode) ?: return emptyList()
    if (model.level != 3) {
      logger?.error("Internal: findAllTownByCodeInternal called with non-county code: {}", countyCode)
      return emptyList()
    }
    return extractPlainItem("towntr", chstApi.getTownPage(model.provinceCode, model.cityCode, model.countyCode).body) ?: listOf()
  }

  private fun findAllVillageByCodeInternal(townCode: String, yearVersion: String): List<ILazyAddressService.CnDistrict> {
    // 年份检查已在外层完成
    val model = ILazyAddressService.createCnDistrictCode(townCode) ?: return emptyList()
    if (model.level != 4) {
      logger?.error("Internal: findAllVillageByCodeInternal called with non-town code: {}", townCode)
      return emptyList()
    }
    return extractVillages(chstApi.getVillagePage(model.provinceCode, model.cityCode, model.countyCode, model.townCode).body) ?: listOf()
  }

  // --- Jsoup 解析辅助方法 ---

  private fun toCnDistrict(code: String, name: String, leaf: Boolean) =
    ILazyAddressService.CnDistrict(
      leaf = leaf,
      name = name,
      code = CnDistrictCode(code), // 假设 CnDistrictCode 可以处理 code
      yearVersion = supportedDefaultYearVersion, // 固定为此实现支持的版本
    )

  private fun extractProvinces(page: String?): List<ILazyAddressService.CnDistrict> {
    return page?.let {
      Jsoup.parse(it).body().selectXpath("//tr[@class='provincetr']/td/a").mapNotNull { link ->
        val hrefCode = link.attr("href").removePrefix("./").removeSuffix(".html")
        val fullCode = hrefCode.padEnd(12, '0')
        // 使用接口的验证方法
        if (ILazyAddressService.verifyCode(fullCode)) {
          val name = link.text()
          toCnDistrict(fullCode, name, false)
        } else {
          logger?.error("Failed to parse province code from href: {}", link.attr("href"))
          null
        }
      }
    } ?: listOf()
  }

  private fun extractVillages(html: String?) =
    html?.let {
      Jsoup.parse(it).body().selectXpath("//tr[@class='villagetr']").mapNotNull { element ->
        val code = element.child(0).text()
        val name = element.child(2).text()
        // 使用接口的验证方法
        if (code.length == 12 && ILazyAddressService.verifyCode(code)) {
          toCnDistrict(code, name, true)
        } else {
          logger?.error("Failed to parse village code: {} or name: {}", code, name)
          null
        }
      }
    }

  private fun extractPlainItem(className: String, html: String?) =
    html?.let {
      Jsoup.parse(it).body().selectXpath("//tr[@class='$className']").mapNotNull { kv ->
        val codeElement = kv.child(0).selectFirst("a")
        val nameElement = kv.child(1).selectFirst("a")

        if (codeElement != null && nameElement != null) {
          val href = codeElement.attr("href")
          val codePart = href.substringAfterLast('/')?.removeSuffix(".html")
          val expectedLength =
            when (className) {
              "citytr" -> 4
              "countytr" -> 6
              "towntr" -> 9
              else -> 0
            }
          // 市、县、镇代码需要补全
          val fullCode = codePart?.padEnd(12, '0')

          if (fullCode != null && codePart?.length == expectedLength && ILazyAddressService.verifyCode(fullCode)) {
            val name = nameElement.text()
            val leaf =
              when (className) {
                "citytr" -> false
                "countytr",
                "towntr" -> kv.child(1).select("a").isEmpty()

                else -> false
              }
            toCnDistrict(fullCode, name, leaf)
          } else {
            logger?.error("Failed to parse plain item code from href: {} or name for class: {}", href, className)
            null
          }
        } else {
          val codeText = kv.child(0).text()
          val nameText = kv.child(1).text()
          val expectedLength =
            when (className) {
              "citytr" -> 4
              "countytr" -> 6
              "towntr" -> 9
              else -> 0
            }
          val fullCode = codeText.padEnd(12, '0')
          // 使用接口的验证方法
          if (codeText.all { it.isDigit() } && codeText.length == expectedLength && ILazyAddressService.verifyCode(fullCode)) {
            toCnDistrict(fullCode, nameText, true)
          } else {
            logger?.error("Failed to parse plain item text code: {} or name: {} for class: {}", codeText, nameText, className)
            null
          }
        }
      }
    }
}
