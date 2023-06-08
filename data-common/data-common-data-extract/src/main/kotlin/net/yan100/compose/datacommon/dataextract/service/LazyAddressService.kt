package net.yan100.compose.datacommon.dataextract.service

import net.yan100.compose.datacommon.dataextract.models.CnDistrictModel

interface LazyAddressService {
  fun findAllProvinces(): List<CnDistrictModel>
  fun findAllCityByCode(districtCode: String): List<CnDistrictModel>
  fun findAllCountyByCode(districtCode: String): List<CnDistrictModel>
  fun findAllTownByCode(districtCode: String): List<CnDistrictModel>
  fun findAllVillageByCode(districtCode: String): List<CnDistrictModel>
}
