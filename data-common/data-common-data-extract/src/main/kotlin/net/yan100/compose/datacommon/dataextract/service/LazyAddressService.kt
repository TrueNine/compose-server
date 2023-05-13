package net.yan100.compose.datacommon.dataextract.service

import net.yan100.compose.datacommon.dataextract.models.CnDistrictModel

interface LazyAddressService {
  fun findAllProvinces(): List<CnDistrictModel>?
  fun findAllCityByCode(districtCode: Long): List<CnDistrictModel>?
  fun findAllCountyByCode(districtCode: Long): List<CnDistrictModel>?
  fun findAllTownByCode(districtCode: Long): List<CnDistrictModel>?
  fun findAllVillageByCode(districtCode: Long): List<CnDistrictModel>?
}
