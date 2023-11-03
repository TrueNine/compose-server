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


  interface ILookupAllChildrenByCodeFindConditionParam {
    val code: String
    val level: Int
  }

  interface ILookupAllChildrenByCodeSortedSaveParam {
    val parentCode: String
    val deepLevel: Int
    val notInit: Boolean
    val result: List<CnDistrictResp>
  }

  /**
   * ## 预取地址数据
   * @param code 地址代码
   * @param firstFind 首次查找函数
   * @param deepCondition 逐级向上查找，报告条件
   * @param notFound 在逐级向上到最顶层，以及在调用接口没有数据时，调用此函数
   * @param sortedSave 当拥有需要的上级数据时，进行调用
   * @return 自定义返回的列表
   */
  fun <T> lookupAllChildrenByCode(
    code: String,
    firstFind: (param: ILookupAllChildrenByCodeFindConditionParam) -> List<T>?,
    deepCondition: (param: ILookupAllChildrenByCodeFindConditionParam) -> Boolean,
    notFound: () -> Unit = {},
    sortedSave: (param: ILookupAllChildrenByCodeSortedSaveParam) -> List<T>
  ): List<T> {
    val firstFindCode = CnDistrictCode(code)
    val requirementCodes = mutableListOf(firstFindCode)
    val preFind = firstFind(object : ILookupAllChildrenByCodeFindConditionParam {
      override val code = firstFindCode.code
      override val level = firstFindCode.level
    })
    if (!preFind.isNullOrEmpty()) return preFind
    val requestQueue = mutableListOf(firstFindCode)
    var result = listOf<T>()
    while (requirementCodes.isNotEmpty()) {
      val requireRequest = requirementCodes.removeLastOrNull() ?: continue
      val findFnResult = deepCondition(object : ILookupAllChildrenByCodeFindConditionParam {
        override val code = requireRequest.code
        override val level = requireRequest.level
      })
      if (!findFnResult) {
        val back = requireRequest.back()
        if (null != back) {
          requirementCodes += CnDistrictCode(back.code)
          requestQueue += CnDistrictCode(back.code)
        } else notFound()
      }
    }
    requestQueue.reversed().forEach {
      val responses = findAllChildrenByCode(code = it.code)
      result = if (responses.isNotEmpty()) sortedSave(object : ILookupAllChildrenByCodeSortedSaveParam {
        override val parentCode = it.code
        override val deepLevel = it.level
        override val result = responses
        override val notInit = it.empty
      }) else {
        notFound()
        listOf()
      }
    }
    return result
  }


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
    } else listOf()
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
    code: String, preHandle: () -> Pair<Boolean, List<T>>, postProcessor: (List<CnDistrictResp>) -> List<T>
  ): List<T> {
    val preFindList = preHandle()
    return if (preFindList.first) preFindList.second
    else postProcessor(findAllChildrenByCode(code))
  }

  fun findAllChildrenByCode(code: String): List<CnDistrictResp> {
    return findAllChildrenByCode(code, CnDistrictCode(code).level)
  }
}
