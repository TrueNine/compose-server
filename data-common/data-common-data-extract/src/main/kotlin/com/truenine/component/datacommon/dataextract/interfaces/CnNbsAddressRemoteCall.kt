package com.truenine.component.datacommon.dataextract.interfaces

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import reactor.core.publisher.Mono

@HttpExchange
interface CnNbsAddressRemoteCall {

  @GetExchange(url = "http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/{year}/index.html")
  fun homePage(@PathVariable year: Int? = 2022): Mono<ResponseEntity<String>>

  @GetExchange("http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/2022/{provinceCode}.html")
  fun getCityPage(
    @PathVariable provinceCode: Int
  ): ResponseEntity<String>

  @GetExchange("http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/2022/{provinceCode}/{provinceCode}{cityCode}.html")
  fun getCountyPage(
    @PathVariable provinceCode: Int,
    @PathVariable cityCode: Int
  ): ResponseEntity<String>

  @GetExchange("http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/2022/{provinceCode}/{cityCode}/{provinceCode}{cityCode}{countyCode}.html")
  fun getTownPage(
    @PathVariable provinceCode: Int,
    @PathVariable cityCode: Int,
    @PathVariable countyCode: Int
  ): ResponseEntity<String>

  @GetExchange("http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/2022/{provinceCode}/{cityCode}/{countyCode}/{provinceCode}{cityCode}{countyCode}{villageCode}.html")
  fun getVillagePage(
    @PathVariable provinceCode: Int,
    @PathVariable cityCode: Int,
    @PathVariable countyCode: Int,
    @PathVariable villageCode: Int
  ): ResponseEntity<String>
}
