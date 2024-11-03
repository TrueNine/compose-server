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
  override val logger get() = log
  final val csvVersions: MutableMap<String, String> = ConcurrentHashMap(16)
  final override val supportedDefaultYearVersion: String get() = "2024"

  init {
    csvVersions += supportedDefaultYearVersion to "area_code_2024.csv"
    log.debug("设定 csv 版本: {}", csvVersions)
  }

  override val supportedYearVersions: List<String>
    get() = csvVersions.keys.toList()


  override fun fetchAllByCodeAndLevel(code: string, level: Int, yearVersion: String): List<ILazyAddressService.CnDistrict> {
    return getCsvSequence(yearVersion)?.filter { c ->
      if (code == ILazyAddressService.DEFAULT_COUNTRY_CODE) c.level == level
      else c.code.padCode.startsWith(code) && c.level == level
    }?.toList() ?: emptyList()
  }


  internal fun getCsvResource(yearVersion: String): Resource? {
    return csvVersions[lastYearVersion]?.let { resourceHolder.getConfigResource(it) }
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
