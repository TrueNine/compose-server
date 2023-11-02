package net.yan100.compose.datacommon.dataextract.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

@HttpExchange("http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm")
interface CnNbsAddressApi {
  companion object {
    const val DEFAULT_VERSION = "2023"
  }

  @GetExchange(url = "{year}/index.html")
  fun homePage(@PathVariable(required = false) year: String? = DEFAULT_VERSION): ResponseEntity<String>

  @GetExchange("{version}/{provinceCode}.html")
  fun getCityPage(
    @PathVariable provinceCode: String,
    @PathVariable version: String? = DEFAULT_VERSION
  ): ResponseEntity<String>

  @GetExchange("{version}/{provinceCode}/{provinceCode}{cityCode}.html")
  fun getCountyPage(
    @PathVariable provinceCode: String,
    @PathVariable cityCode: String,
    @PathVariable version: String? = DEFAULT_VERSION
  ): ResponseEntity<String>

  @GetExchange("{version}/{provinceCode}/{cityCode}/{provinceCode}{cityCode}{countyCode}.html")
  fun getTownPage(
    @PathVariable provinceCode: String,
    @PathVariable cityCode: String,
    @PathVariable countyCode: String,
    @PathVariable version: String? = DEFAULT_VERSION
  ): ResponseEntity<String>

  @GetExchange("{version}/{provinceCode}/{cityCode}/{countyCode}/{provinceCode}{cityCode}{countyCode}{villageCode}.html")
  fun getVillagePage(
    @PathVariable provinceCode: String,
    @PathVariable cityCode: String,
    @PathVariable countyCode: String,
    @PathVariable villageCode: String,
    @PathVariable version: String? = DEFAULT_VERSION
  ): ResponseEntity<String>
}
