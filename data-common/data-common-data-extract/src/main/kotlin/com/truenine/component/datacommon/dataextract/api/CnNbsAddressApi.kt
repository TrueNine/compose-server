package com.truenine.component.datacommon.dataextract.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import reactor.core.publisher.Mono

@HttpExchange("http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm")
interface CnNbsAddressApi {
  companion object {
    const val DEFAULT_VERSION = 2022
  }

  @GetExchange(url = "{year}/index.html")
  fun homePage(@PathVariable(required = false) year: Int? = DEFAULT_VERSION): ResponseEntity<String>

  @GetExchange("{version}/{provinceCode}.html")
  fun getCityPage(
    @PathVariable provinceCode: Int,
    @PathVariable version: Int? = DEFAULT_VERSION
  ): ResponseEntity<String>

  @GetExchange("{version}/{provinceCode}/{provinceCode}{cityCode}.html")
  fun getCountyPage(
    @PathVariable provinceCode: Int,
    @PathVariable cityCode: Int,
    @PathVariable version: Int? = DEFAULT_VERSION
  ): ResponseEntity<String>

  @GetExchange("{version}/{provinceCode}/{cityCode}/{provinceCode}{cityCode}{countyCode}.html")
  fun getTownPage(
    @PathVariable provinceCode: Int,
    @PathVariable cityCode: Int,
    @PathVariable countyCode: Int,
    @PathVariable version: Int? = DEFAULT_VERSION
  ): ResponseEntity<String>

  @GetExchange("{version}/{provinceCode}/{cityCode}/{countyCode}/{provinceCode}{cityCode}{countyCode}{villageCode}.html")
  fun getVillagePage(
    @PathVariable provinceCode: Int,
    @PathVariable cityCode: Int,
    @PathVariable countyCode: Int,
    @PathVariable villageCode: Int,
    @PathVariable version: Int? = DEFAULT_VERSION
  ): ResponseEntity<String>
}
