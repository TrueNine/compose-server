package net.yan100.compose.data.extract.service.impl

import net.yan100.compose.core.holders.ResourceHolder
import net.yan100.compose.core.slf4j
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
  final val csvVersions: MutableMap<String, String> = ConcurrentHashMap(16)

  init {
    csvVersions += "2024" to "area_code_2024.csv"
    log.debug("设定 csv 版本: {}", csvVersions)
  }

  internal val lastYearVersion: String get() = csvVersions.keys.maxOf { it }

  internal fun getCsvResource(yearVersion: String): Resource? {
    return csvVersions[lastYearVersion]?.let { resourceHolder.getConfigResource(it) }
  }

  internal fun getCsvSequence(yearVersion: String): Sequence<ILazyAddressService.CnDistrictResp>? {
    return getCsvResource(yearVersion)?.inputStream?.bufferedReader()?.lineSequence()?.filter { it.isNotBlank() }?.map {
      val a = it.split(",")
      val e = ILazyAddressService.CnDistrictResp()
      e.yearVersion = yearVersion
      e.code = CnDistrictCode(a[0])
      e.name = a[1]
      e.level = a[2].toInt()
      e.leaf = e.level < 5
      e
    }
  }

  override fun findAllProvinces(): List<ILazyAddressService.CnDistrictResp> {
    return getCsvSequence(lastYearVersion)?.filter { c -> c.level == 1 }?.toList() ?: emptyList()
  }

  override fun findAllCityByCode(districtCode: String): List<ILazyAddressService.CnDistrictResp> {
    return getCsvSequence(lastYearVersion)?.filter { c -> c.level == 2 }?.filter { c -> c.code.padCode.startsWith(districtCode) }?.toList() ?: emptyList()
  }

  override fun findAllCountyByCode(districtCode: String): List<ILazyAddressService.CnDistrictResp> {
    return getCsvSequence(lastYearVersion)?.filter { c -> c.level == 3 }?.filter { c -> c.code.padCode.startsWith(districtCode) }?.toList() ?: emptyList()
  }

  override fun findAllTownByCode(districtCode: String): List<ILazyAddressService.CnDistrictResp> {
    return getCsvSequence(lastYearVersion)?.filter { c -> c.level == 4 }?.filter { c -> c.code.padCode.startsWith(districtCode) }?.toList() ?: emptyList()
  }

  override fun findAllVillageByCode(districtCode: String): List<ILazyAddressService.CnDistrictResp> {
    return getCsvSequence(lastYearVersion)?.filter { c -> c.level == 5 }?.filter { c -> c.code.padCode.startsWith(districtCode) }?.toList() ?: emptyList()
  }
}
