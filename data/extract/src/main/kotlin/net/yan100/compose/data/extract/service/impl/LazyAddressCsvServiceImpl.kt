package net.yan100.compose.data.extract.service.impl

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import net.yan100.compose.data.extract.domain.CnDistrictCode
import net.yan100.compose.data.extract.service.ILazyAddressService
import net.yan100.compose.holders.ResourceHolder
import net.yan100.compose.slf4j
import net.yan100.compose.string
import org.springframework.context.annotation.Primary
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

private val log = slf4j<LazyAddressCsvServiceImpl>()

@Primary
@Service
class LazyAddressCsvServiceImpl(private val resourceHolder: ResourceHolder) :
  ILazyAddressService {
  data class CsvDefine(
    val fileName: String,
    val codeLine: Int = 0,
    val nameLine: Int = 1,
    val levelLine: Int = 2,
    val parentCodeLine: Int = 3,
  )

  final val csvVersions: MutableMap<String, CsvDefine> = ConcurrentHashMap(16)

  // 添加缓存来存储已解析的数据
  private val districtCache:
    ConcurrentMap<String, List<ILazyAddressService.CnDistrict>> =
    ConcurrentHashMap()

  override val logger
    get() = log

  final override val supportedDefaultYearVersion: String
    get() = "2024"

  init {
    val confinedCsvResources =
      resourceHolder.matchConfigResources("area_code*.csv")
    val r =
      confinedCsvResources
        .map {
          val yearVersion = "\\d{4}".toRegex().find(it.filename!!)?.value
          yearVersion to it.filename!!
        }
        .filter { it.first != null }
        .forEach { csvVersions += it.first!! to CsvDefine(it.second) }

    csvVersions +=
      supportedDefaultYearVersion to CsvDefine("area_code_2024.csv")
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

  override fun fetchAllByCodeAndLevel(
    code: string,
    level: Int,
    yearVersion: String,
  ): List<ILazyAddressService.CnDistrict> {
    log.debug("fetch csv version: {}", yearVersion)

    // 首先尝试从缓存获取数据
    return districtCache
      .computeIfAbsent(yearVersion) {
        getCsvSequence(yearVersion)?.toList() ?: emptyList()
      }
      .filter { district ->
        if (code == ILazyAddressService.DEFAULT_COUNTRY_CODE) {
          district.level == level
        } else {
          district.code.padCode.startsWith(code) && district.level == level
        }
      }
  }

  internal fun getCsvResource(yearVersion: String): Resource? {
    return csvVersions[yearVersion]?.let {
      resourceHolder.getConfigResource(it.fileName)
    }
  }

  internal fun getCsvSequence(
    yearVersion: String
  ): Sequence<ILazyAddressService.CnDistrict>? {
    return getCsvResource(yearVersion)
      ?.let { resource ->
        resource.inputStream.bufferedReader().useLines { lines ->
          lines
            .filter { it.isNotBlank() }
            .map { line ->
              line.split(',', limit = 4).let { parts ->
                ILazyAddressService.CnDistrict(
                  code = CnDistrictCode(parts[0]),
                  name = parts[1],
                  yearVersion = yearVersion,
                  level = parts[2].toInt(),
                )
              }
            }
            .toList()
        }
      }
      ?.asSequence()
  }
}
