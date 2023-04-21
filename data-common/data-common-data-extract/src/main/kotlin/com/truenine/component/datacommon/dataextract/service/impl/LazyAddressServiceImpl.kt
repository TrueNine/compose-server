package com.truenine.component.datacommon.dataextract.service.impl

import com.truenine.component.datacommon.dataextract.api.CnNbsAddressApi
import com.truenine.component.datacommon.dataextract.models.CnDistrictCodeModel
import com.truenine.component.datacommon.dataextract.models.CnDistrictModel
import com.truenine.component.datacommon.dataextract.service.LazyAddressService
import org.jsoup.Jsoup
import org.springframework.stereotype.Service

@Service
class LazyAddressServiceImpl(
  private val call: CnNbsAddressApi
) : LazyAddressService {
  override fun findAllProvinces(): List<CnDistrictModel>? {
    return extractProvinces(call.homePage().body)
  }

  private fun wrapperModel(code: Long, name: String) = CnDistrictModel().apply {
    codeModel = CnDistrictCodeModel(code)
    level = codeModel.level
    this.name = name
  }

  private fun getModel(code: Long): CnDistrictCodeModel = CnDistrictCodeModel(code)

  private fun extractProvinces(page: String?): List<CnDistrictModel>? =
    page?.let {
      Jsoup.parse(it).body().selectXpath("//tr[@class='provincetr']/td/a")
        .map { link ->
          val code = link.attr("href").replace(".html", "0000000000").toLong()
          val name = link.text()
          wrapperModel(code, name)
        }
    }


  override fun findAllCityByCode(districtCode: Long): List<CnDistrictModel>? =
    extractPlainItem("citytr", call.getCityPage(getModel(districtCode).provinceCode).body)


  override fun findAllCountyByCode(districtCode: Long): List<CnDistrictModel>? {
    val model = getModel(districtCode)
    return extractPlainItem(
      "countytr", call.getCountyPage(
        model.provinceCode,
        model.cityCode
      ).body
    )
  }

  override fun findAllTownByCode(districtCode: Long): List<CnDistrictModel>? {
    val model = getModel(districtCode)
    return extractPlainItem(
      "towntr",
      call.getTownPage(
        model.provinceCode,
        model.cityCode,
        model.countyCode
      ).body
    )
  }

  override fun findAllVillageByCode(districtCode: Long): List<CnDistrictModel>? {
    val model = getModel(districtCode)
    return extractVillages(
      call.getVillagePage(
        model.provinceCode,
        model.cityCode,
        model.countyCode,
        model.townCode
      ).body
    )
  }

  private fun extractVillages(html: String?) = html?.let {
    Jsoup.parse(it).body().selectXpath("//tr[@class='villagetr']").mapNotNull {
      val code = it.child(0).text().toLong()
      val name = it.child(2).text()
      wrapperModel(code, name)
    }
  }


  private fun extractPlainItem(
    className: String,
    html: String?
  ) = html?.let {
    Jsoup.parse(it).body().selectXpath("//tr[@class='$className']")
      .mapNotNull { kv ->
        val code = kv.child(0).text().toLong()
        val name = kv.child(1).text()
        wrapperModel(code, name)
      }
  }
}
