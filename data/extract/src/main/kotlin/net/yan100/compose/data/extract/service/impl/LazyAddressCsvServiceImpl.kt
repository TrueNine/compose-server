package net.yan100.compose.data.extract.service.impl

import net.yan100.compose.core.holders.ResourceHolder
import net.yan100.compose.core.slf4j
import net.yan100.compose.core.string
import net.yan100.compose.data.extract.domain.CnDistrictCode
import net.yan100.compose.data.extract.service.ILazyAddressService
import org.springframework.context.annotation.Primary
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

private val log = slf4j<LazyAddressCsvServiceImpl>()

@Primary
@Service
class LazyAddressCsvServiceImpl(
  private val resourceHolder: ResourceHolder
) : ILazyAddressService {
  data class CsvDefine(
    val fileName: String, val codeLine: Int = 0, val nameLine: Int = 1, val levelLine: Int = 2, val parentCodeLine: Int = 3
  )

  final val csvVersions: MutableMap<String, CsvDefine> = ConcurrentHashMap(16)
  override val logger get() = log
  final override val supportedDefaultYearVersion: String get() = "2024"

  init {
    val confinedCsvResources = resourceHolder.matchConfigResources("area_code*.csv")
    val r = confinedCsvResources.map {
      val yearVersion = "\\d{4}".toRegex().find(it.filename!!)?.value
      yearVersion to it.filename!!
    }.filter { it.first != null }
      .forEach {
        csvVersions += it.first!! to CsvDefine(it.second)
      }

    csvVersions += supportedDefaultYearVersion to CsvDefine("area_code_2024.csv")
    log.debug("设定 csv 版本: {}", csvVersions)
  }

  fun removeSupportedYear(year: String) {
    csvVersions -= year
  }

  operator fun plusAssign(definePair: Pair<String, CsvDefine>) {
    addSupportedYear(definePair.first, definePair.second)
  }

  operator fun minusAssign(yearKey: String) {
    removeSupportedYear(yearKey)
  }

  fun addSupportedYear(
    year: String, csvDefine: CsvDefine
  ) {
    csvVersions += year to csvDefine
  }

  override val supportedYearVersions: List<String>
    get() = csvVersions.keys.toList()


  override fun fetchAllByCodeAndLevel(code: string, level: Int, yearVersion: String): List<ILazyAddressService.CnDistrict> {
    log.debug("fetch csv version: {}", yearVersion)
    return getCsvSequence(yearVersion)?.filter { c ->
      if (code == ILazyAddressService.DEFAULT_COUNTRY_CODE) c.level == level
      else c.code.padCode.startsWith(code) && c.level == level
    }?.toList() ?: emptyList()
  }

  internal fun getCsvResource(yearVersion: String): Resource? {
    return run {
      csvVersions[yearVersion] ?: run {
        log.warn("not get supported version csv file: {}", yearVersion)
        csvVersions[yearVersion]
      }
    }?.let { resourceHolder.getConfigResource(it.fileName) }
  }

  internal fun getCsvSequence(yearVersion: String): Sequence<ILazyAddressService.CnDistrict>? {
    return getCsvResource(yearVersion)?.inputStream?.bufferedReader()?.lineSequence()?.filter { it.isNotBlank() }?.map {
      val a = it.split(",")
      ILazyAddressService.CnDistrict(
        code = CnDistrictCode(a[0]), name = a[1], yearVersion = yearVersion, level = a[2].toInt()
      )
    }
  }
}
