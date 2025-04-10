package net.yan100.compose.data.extract.service.impl

import net.yan100.compose.data.extract.api.ICnNbsAddressApi
import net.yan100.compose.data.extract.domain.CnDistrictCode
import net.yan100.compose.data.extract.service.ILazyAddressService
import net.yan100.compose.slf4j
import net.yan100.compose.string
import org.jsoup.Jsoup
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Service

private val log = slf4j<LazyAddressServiceImpl>()

@ConditionalOnMissingBean(ILazyAddressService::class)
@Deprecated("统计局已暂时不能使用")
@Service("DataExtractLazyAddressServiceImpls")
class LazyAddressServiceImpl(private val chstApi: ICnNbsAddressApi) :
  ILazyAddressService {
  override val supportedDefaultYearVersion: String
    get() = "2023"

  override val supportedYearVersions: List<String>
    get() = listOf(supportedDefaultYearVersion)

  override val logger
    get() = log

  override fun fetchAllByCodeAndLevel(
    code: string,
    level: Int,
    yearVersion: String,
  ): List<ILazyAddressService.CnDistrict> {
    return when (level) {
      1 -> findAllProvinces()
      2 -> findAllCityByCode(code)
      3 -> findAllCountyByCode(code)
      4 -> findAllTownByCode(code)
      5 -> findAllVillageByCode(code)
      else -> TODO("Not yet implemented")
    }
  }

  override fun findAllProvinces(
    yearVersion: String,
  ): List<ILazyAddressService.CnDistrict> {
    val homeBody = chstApi.homePage().body
    log.debug("homeBody = {}", homeBody)
    val result = extractProvinces(chstApi.homePage().body)
    log.debug("result = {}", result)
    return result
  }

  private fun toCnDistrict(code: String, name: String, leaf: Boolean) =
    ILazyAddressService.CnDistrict(
      leaf = leaf,
      name = name,
      code = CnDistrictCode(code),
      yearVersion = ICnNbsAddressApi.DEFAULT_VERSION,
    )

  private fun getModel(code: String): CnDistrictCode {
    return CnDistrictCode(code)
  }

  private fun extractProvinces(
    page: String?,
  ): List<ILazyAddressService.CnDistrict> {
    return page?.let {
      Jsoup.parse(it)
        .body()
        .selectXpath("//tr[@class='provincetr']/td/a")
        .map { link ->
          val code = link.attr("href").replace(".html", "0000000000")
          val name = link.text()
          toCnDistrict(code, name, false)
        }
    } ?: listOf()
  }

  override fun findAllCityByCode(
    districtCode: String,
    yearVersion: String,
  ): List<ILazyAddressService.CnDistrict> {
    val h = chstApi.getCityPage(getModel(districtCode).provinceCode)
    log.debug("h.headers = {}", h.headers)
    return extractPlainItem("citytr", h.body) ?: listOf()
  }

  override fun findAllCountyByCode(
    districtCode: String,
    yearVersion: String,
  ): List<ILazyAddressService.CnDistrict> {
    val model = getModel(districtCode)
    return extractPlainItem(
      "countytr",
      chstApi.getCountyPage(model.provinceCode, model.cityCode).body,
    ) ?: listOf()
  }

  override fun findAllTownByCode(
    districtCode: String,
    yearVersion: String,
  ): List<ILazyAddressService.CnDistrict> {
    val model = getModel(districtCode)
    return extractPlainItem(
      "towntr",
      chstApi
        .getTownPage(model.provinceCode, model.cityCode, model.countyCode)
        .body,
    ) ?: listOf()
  }

  override fun findAllVillageByCode(
    districtCode: String,
    yearVersion: String,
  ): List<ILazyAddressService.CnDistrict> {
    val model = getModel(districtCode)
    return extractVillages(
      chstApi
        .getVillagePage(
          model.provinceCode,
          model.cityCode,
          model.countyCode,
          model.townCode,
        )
        .body
    ) ?: listOf()
  }

  private fun extractVillages(html: String?) =
    html?.let {
      Jsoup.parse(it)
        .body()
        .selectXpath("//tr[@class='villagetr']")
        .mapNotNull { element ->
          val code = element.child(0).text()
          val name = element.child(2).text()
          toCnDistrict(code, name, true)
        }
    }

  private fun extractPlainItem(className: String, html: String?) =
    html?.let {
      Jsoup.parse(it)
        .body()
        .selectXpath("//tr[@class='$className']")
        .mapNotNull { kv ->
          val leaf = kv.child(1).select("a").size <= 0
          val code = kv.child(0).text()
          val name = kv.child(1).text()
          toCnDistrict(code, name, leaf)
        }
    }
}
