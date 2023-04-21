package com.truenine.component.datacommon.dataextract.service

import com.truenine.component.datacommon.dataextract.models.CnDistrictModel

interface LazyAddressService {
  fun findAllProvinces(): List<CnDistrictModel>?
  fun findAllCityByCode(districtCode: Long): List<CnDistrictModel>?
  fun findAllCountyByCode(districtCode: Long): List<CnDistrictModel>?
  fun findAllTownByCode(districtCode: Long): List<CnDistrictModel>?
  fun findAllVillageByCode(districtCode: Long): List<CnDistrictModel>?
}
