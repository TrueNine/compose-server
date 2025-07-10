package io.github.truenine.composeserver.data.extract.service.impl

import io.github.truenine.composeserver.data.extract.domain.CnDistrictCode
import io.github.truenine.composeserver.data.extract.service.ILazyAddressService
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import net.yan100.compose.holders.ResourceHolder
import net.yan100.compose.slf4j
import net.yan100.compose.string
import org.springframework.context.annotation.Primary
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

private val log = slf4j<LazyAddressCsvServiceImpl>()

@Primary
@Service
class LazyAddressCsvServiceImpl(private val resourceHolder: ResourceHolder) : ILazyAddressService {
  data class CsvDefine(val fileName: String, val codeLine: Int = 0, val nameLine: Int = 1, val levelLine: Int = 2, val parentCodeLine: Int = 3)

  final val csvVersions: MutableMap<String, CsvDefine> = ConcurrentHashMap(16)

  // 添加缓存来存储已解析的数据
  private val districtCache: ConcurrentMap<String, List<ILazyAddressService.CnDistrict>> = ConcurrentHashMap()

  override val logger
    get() = log

  final override val supportedDefaultYearVersion: String
    get() = "2024"

  init {
    val confinedCsvResources = resourceHolder.matchConfigResources("area_code*.csv")
    val r =
      confinedCsvResources
        .mapNotNull {
          val yearVersion = "\\d{4}".toRegex().find(it.filename!!)?.value
          yearVersion!! to it.filename!!
        }
        .forEach { csvVersions.put(it.first, CsvDefine(it.second)) }

    // 确保默认年份版本存在
    if (!csvVersions.containsKey(supportedDefaultYearVersion)) {
      csvVersions += supportedDefaultYearVersion to CsvDefine("area_code_${supportedDefaultYearVersion}.csv")
    }
    log.debug("设定 csv 版本: {}", csvVersions)
  }

  fun removeSupportedYear(year: String) {
    csvVersions -= year
    districtCache.remove(year)
  }

  operator fun plusAssign(definePair: Pair<String, CsvDefine>) {
    addSupportedYear(definePair.first, definePair.second)
  }

  operator fun minusAssign(yearKey: String) {
    removeSupportedYear(yearKey)
  }

  fun addSupportedYear(year: String, csvDefine: CsvDefine) {
    csvVersions += year to csvDefine
    districtCache.remove(year) // 清除相关缓存
  }

  override val supportedYearVersions: List<String>
    get() = csvVersions.keys.toList()

  // 实现 ILazyAddressService 的抽象方法
  override fun fetchChildren(parentCode: string, yearVersion: String): List<ILazyAddressService.CnDistrict> {
    log.debug("Finding children for parent code: {} in year: {}", parentCode, yearVersion)

    // 如果年份版本不存在，返回空列表
    if (!csvVersions.containsKey(yearVersion)) {
      return emptyList()
    }

    // 获取指定年份的所有数据
    val allDistricts = districtCache.computeIfAbsent(yearVersion) { getCsvSequence(yearVersion)?.toList() ?: emptyList() }

    // 如果是查询国家的子集，直接返回所有省级数据
    if (parentCode == ILazyAddressService.DEFAULT_COUNTRY_CODE) {
      return allDistricts.filter { it.level == 1 }
    }

    // 创建父级代码对象
    val parentCodeObj = ILazyAddressService.createCnDistrictCode(parentCode) ?: return emptyList()
    val targetLevel = parentCodeObj.level + 1

    // 过滤出子级数据
    return allDistricts.filter { district -> district.level == targetLevel && district.code.code.startsWith(parentCodeObj.code) }
  }

  override fun fetchDistrict(code: string, yearVersion: String): ILazyAddressService.CnDistrict? {
    log.debug("Finding district for code: {} in year: {}", code, yearVersion)

    // 如果年份版本不存在，返回null
    if (!csvVersions.containsKey(yearVersion)) {
      return null
    }

    // 创建代码对象
    val codeObj = ILazyAddressService.createCnDistrictCode(code) ?: return null

    // 获取指定年份的所有数据
    val allDistricts = districtCache.computeIfAbsent(yearVersion) { getCsvSequence(yearVersion)?.toList() ?: emptyList() }

    // 查找匹配的区划
    return allDistricts.firstOrNull { it.code.code == codeObj.code }
  }

  override fun fetchChildrenRecursive(parentCode: string, maxDepth: Int, yearVersion: String): List<ILazyAddressService.CnDistrict> {
    log.debug("Finding recursive children for code: {} with maxDepth: {} in year: {}", parentCode, maxDepth, yearVersion)

    // 如果年份版本不存在，返回空列表
    if (!csvVersions.containsKey(yearVersion)) {
      return emptyList()
    }

    if (maxDepth <= 0) return emptyList()

    val result = mutableListOf<ILazyAddressService.CnDistrict>()
    val queue = ArrayDeque<Pair<String, Int>>() // (code, remainingDepth)
    queue.add(parentCode to maxDepth)

    while (queue.isNotEmpty()) {
      val (currentCode, remainingDepth) = queue.removeFirst()
      if (remainingDepth <= 0) continue

      // 获取当前代码的直接子级
      val children = fetchChildren(currentCode, yearVersion)
      result.addAll(children)

      // 将子级加入队列继续处理
      children.forEach { child -> queue.add(child.code.code to (remainingDepth - 1)) }
    }

    return result
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

  internal fun getCsvResource(yearVersion: String): Resource? {
    return csvVersions[yearVersion]?.let { resourceHolder.getConfigResource(it.fileName) }
  }

  internal fun getCsvSequence(yearVersion: String): List<ILazyAddressService.CnDistrict>? {
    return getCsvResource(yearVersion)?.let { resource ->
      resource.inputStream.bufferedReader().useLines { lines ->
        lines
          .filter { it.isNotBlank() }
          .map { line ->
            line.split(',', limit = 4).let { parts ->
              ILazyAddressService.CnDistrict(code = CnDistrictCode(parts[0]), name = parts[1], yearVersion = yearVersion, level = parts[2].toInt())
            }
          }
          .toList()
      }
    }
  }
}
