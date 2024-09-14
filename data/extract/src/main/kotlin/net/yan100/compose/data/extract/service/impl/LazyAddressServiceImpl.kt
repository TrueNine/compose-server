/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.data.extract.service.impl

import net.yan100.compose.core.slf4j
import net.yan100.compose.data.extract.api.ICnNbsAddressApi
import net.yan100.compose.data.extract.domain.CnDistrictCode
import net.yan100.compose.data.extract.service.ILazyAddressService
import org.jsoup.Jsoup
import org.springframework.stereotype.Service

@Service("DataExtractLazyAddressServiceImpls")
class LazyAddressServiceImpl(private val call: ICnNbsAddressApi) : ILazyAddressService {
  companion object {
    private val log = slf4j(LazyAddressServiceImpl::class)
  }

  override fun findAllProvinces(): List<ILazyAddressService.CnDistrictResp> {
    val homeBody = call.homePage().body
    log.debug("homeBody = {}", homeBody)
    val result = extractProvinces(call.homePage().body)
    log.debug("result = {}", result)
    return result
  }

  private fun wrapperModel(code: String, name: String, leaf: Boolean) =
    ILazyAddressService.CnDistrictResp().apply {
      this.leaf = leaf
      this.name = name
      this.code = CnDistrictCode(code)
      this.yearVersion = ICnNbsAddressApi.DEFAULT_VERSION
      level = this.code.level
    }

  private fun getModel(code: String): CnDistrictCode {
    return CnDistrictCode(code)
  }

  private fun extractProvinces(page: String?): List<ILazyAddressService.CnDistrictResp> {
    return page?.let {
      Jsoup.parse(it).body().selectXpath("//tr[@class='provincetr']/td/a").map { link ->
        val code = link.attr("href").replace(".html", "0000000000")
        val name = link.text()
        wrapperModel(code, name, false)
      }
    } ?: listOf()
  }

  override fun findAllCityByCode(districtCode: String): List<ILazyAddressService.CnDistrictResp> {
    val h = call.getCityPage(getModel(districtCode).provinceCode)
    log.debug("h.headers = {}", h.headers)
    return extractPlainItem("citytr", h.body) ?: listOf()
  }

  override fun findAllCountyByCode(districtCode: String): List<ILazyAddressService.CnDistrictResp> {
    val model = getModel(districtCode)
    return extractPlainItem("countytr", call.getCountyPage(model.provinceCode, model.cityCode).body) ?: listOf()
  }

  override fun findAllTownByCode(districtCode: String): List<ILazyAddressService.CnDistrictResp> {
    val model = getModel(districtCode)
    return extractPlainItem("towntr", call.getTownPage(model.provinceCode, model.cityCode, model.countyCode).body) ?: listOf()
  }

  override fun findAllVillageByCode(districtCode: String): List<ILazyAddressService.CnDistrictResp> {
    val model = getModel(districtCode)
    return extractVillages(call.getVillagePage(model.provinceCode, model.cityCode, model.countyCode, model.townCode).body) ?: listOf()
  }

  private fun extractVillages(html: String?) =
    html?.let {
      Jsoup.parse(it).body().selectXpath("//tr[@class='villagetr']").mapNotNull { element ->
        val code = element.child(0).text()
        val name = element.child(2).text()
        wrapperModel(code, name, true)
      }
    }

  private fun extractPlainItem(className: String, html: String?) =
    html?.let {
      Jsoup.parse(it).body().selectXpath("//tr[@class='$className']").mapNotNull { kv ->
        val leaf = kv.child(1).select("a").size <= 0
        val code = kv.child(0).text()
        val name = kv.child(1).text()
        wrapperModel(code, name, leaf)
      }
    }
}
