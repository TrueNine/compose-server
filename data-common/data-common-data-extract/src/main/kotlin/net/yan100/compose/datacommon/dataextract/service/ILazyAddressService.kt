package net.yan100.compose.datacommon.dataextract.service

import net.yan100.compose.core.exceptions.RemoteCallException
import net.yan100.compose.datacommon.dataextract.models.CnDistrictCode
import net.yan100.compose.datacommon.dataextract.models.CnDistrictResp

@JvmDefaultWithCompatibility
interface ILazyAddressService {
  fun findAllProvinces(): List<CnDistrictResp>
  fun findAllCityByCode(districtCode: String): List<CnDistrictResp>
  fun findAllCountyByCode(districtCode: String): List<CnDistrictResp>
  fun findAllTownByCode(districtCode: String): List<CnDistrictResp>
  fun findAllVillageByCode(districtCode: String): List<CnDistrictResp>

  fun findAllChildrenByCode(code: String, level: Int): List<CnDistrictResp> {
    return if (level in 0..4) {
      try {
        when (level) {
          0 -> findAllProvinces()
          1 -> findAllCityByCode(code)
          2 -> findAllCountyByCode(code)
          3 -> findAllTownByCode(code)
          4 -> findAllVillageByCode(code)
          else -> listOf()
        }
      } catch (e: RemoteCallException) {
        listOf()
      }
    } else throw IllegalArgumentException("地址参数 必须在 1 到 4 之间")
  }

  /**
   * ## 预取地址数据
   * 此函数的作用在于，当拥有某些数据时，则不再需要发送网络请求，减少资源消耗
   * 这个函数应用起来可能有些费解
   * <br/>
   * 首先传入code
   * <br/>
   * preHandle 预处理函数，返回两个值，条件以及结果
   * <br/>
   * 当 preHandle 返回的条件为 true 时，直接返回结果
   * <br/>
   * 否则会调用 postProcessor 函数，入参为预取的地址数据
   */
  fun <T> lazyFindAllChildrenByCode(
    code: String,
    preHandle: () -> Pair<Boolean, List<T>>,
    postProcessor: (List<CnDistrictResp>) -> List<T>
  ): List<T> {
    val preFindList = preHandle()
    return if (preFindList.first) preFindList.second
    else postProcessor(findAllChildrenByCode(code))
  }

  fun findAllChildrenByCode(code: String): List<CnDistrictResp> {
    return findAllChildrenByCode(code, CnDistrictCode(code).level)
  }
}
