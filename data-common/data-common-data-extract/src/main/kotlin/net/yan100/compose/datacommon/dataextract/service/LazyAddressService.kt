package net.yan100.compose.datacommon.dataextract.service

import net.yan100.compose.datacommon.dataextract.models.CnDistrictCode
import net.yan100.compose.datacommon.dataextract.models.CnDistrictResp

interface LazyAddressService {
  fun findAllProvinces(): List<CnDistrictResp>
  fun findAllCityByCode(districtCode: String): List<CnDistrictResp>
  fun findAllCountyByCode(districtCode: String): List<CnDistrictResp>
  fun findAllTownByCode(districtCode: String): List<CnDistrictResp>
  fun findAllVillageByCode(districtCode: String): List<CnDistrictResp>
}
