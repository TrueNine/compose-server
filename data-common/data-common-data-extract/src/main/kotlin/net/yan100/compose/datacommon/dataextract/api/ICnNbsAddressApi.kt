package net.yan100.compose.datacommon.dataextract.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

/**
 *
 * # 中华人民共和国国家统计局 地址数据接口
 *
 * > 地址数据 最早统计到 2009 年
 *
 * @version 2023
 */
@HttpExchange("https://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm")
interface ICnNbsAddressApi {
  companion object {
    const val DEFAULT_VERSION = "2023"
  }

  @GetExchange(url = "{year}/index.html")
  fun homePage(@PathVariable(required = false) year: String? = DEFAULT_VERSION): ResponseEntity<String>

  @GetExchange("{year}/{provinceCode}.html")
  fun getCityPage(
    @PathVariable provinceCode: String,
    @PathVariable year: String? = DEFAULT_VERSION
  ): ResponseEntity<String>

  @GetExchange("{year}/{provinceCode}/{provinceCode}{cityCode}.html")
  fun getCountyPage(
    @PathVariable provinceCode: String,
    @PathVariable cityCode: String,
    @PathVariable year: String? = DEFAULT_VERSION
  ): ResponseEntity<String>

  @GetExchange("{year}/{provinceCode}/{cityCode}/{provinceCode}{cityCode}{countyCode}.html")
  fun getTownPage(
    @PathVariable provinceCode: String,
    @PathVariable cityCode: String,
    @PathVariable countyCode: String,
    @PathVariable year: String? = DEFAULT_VERSION
  ): ResponseEntity<String>

  @GetExchange("{year}/{provinceCode}/{cityCode}/{countyCode}/{provinceCode}{cityCode}{countyCode}{villageCode}.html")
  fun getVillagePage(
    @PathVariable provinceCode: String,
    @PathVariable cityCode: String,
    @PathVariable countyCode: String,
    @PathVariable villageCode: String,
    @PathVariable year: String? = DEFAULT_VERSION
  ): ResponseEntity<String>
}
