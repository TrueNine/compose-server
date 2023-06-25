package net.yan100.compose.datacommon.dataextract.service.impl


import net.yan100.compose.datacommon.dataextract.api.CnNbsAddressApi
import net.yan100.compose.datacommon.dataextract.models.CnDistrictCode
import net.yan100.compose.datacommon.dataextract.models.CnDistrictResp
import net.yan100.compose.datacommon.dataextract.service.LazyAddressService
import org.jsoup.Jsoup
import org.springframework.stereotype.Service

@Service
class LazyAddressServiceImpl(
  private val call: CnNbsAddressApi
) : LazyAddressService {
  override fun findAllProvinces(): List<CnDistrictResp> {
    return extractProvinces(call.homePage().body)
  }

  private fun wrapperModel(code: String, name: String, leaf: Boolean) = CnDistrictResp()
    .apply {
      this.leaf = leaf
      codeModel = CnDistrictCode(code)
      level = codeModel?.level
      this.name = name
    }

  private fun getModel(code: String): CnDistrictCode =
    CnDistrictCode(code)

  private fun extractProvinces(page: String?): List<CnDistrictResp> =
    page?.let {
      Jsoup.parse(it).body().selectXpath("//tr[@class='provincetr']/td/a")
        .map { link ->
          val code = link.attr("href").replace(".html", "0000000000")
          val name = link.text()
          wrapperModel(code, name, false)
        }
    } ?: listOf()


  override fun findAllCityByCode(districtCode: String): List<CnDistrictResp> =
    extractPlainItem("citytr", call.getCityPage(getModel(districtCode).provinceCode!!).body) ?: listOf()


  override fun findAllCountyByCode(districtCode: String): List<CnDistrictResp> {
    val model = getModel(districtCode)
    return extractPlainItem(
      "countytr", call.getCountyPage(
        model.provinceCode!!,
        model.cityCode!!
      ).body
    ) ?: listOf()
  }

  override fun findAllTownByCode(districtCode: String): List<CnDistrictResp> {
    val model = getModel(districtCode)
    return extractPlainItem(
      "towntr",
      call.getTownPage(
        model.provinceCode!!,
        model.cityCode!!,
        model.countyCode!!
      ).body
    ) ?: listOf()
  }

  override fun findAllVillageByCode(districtCode: String): List<CnDistrictResp> {
    val model = getModel(districtCode)
    return extractVillages(
      call.getVillagePage(
        model.provinceCode!!,
        model.cityCode!!,
        model.countyCode!!,
        model.townCode!!
      ).body
    ) ?: listOf()
  }

  private fun extractVillages(html: String?) = html?.let {
    Jsoup.parse(it).body().selectXpath("//tr[@class='villagetr']").mapNotNull { element ->
      val code = element.child(0).text()
      val name = element.child(2).text()
      wrapperModel(code, name, true)
    }
  }


  private fun extractPlainItem(
    className: String,
    html: String?
  ) = html?.let {
    Jsoup.parse(it).body().selectXpath("//tr[@class='$className']")
      .mapNotNull { kv ->
        val leaf = kv.child(1).select("a").size <= 0
        val code = kv.child(0).text()
        val name = kv.child(1).text()
        wrapperModel(code, name, leaf)
      }
  }
}
