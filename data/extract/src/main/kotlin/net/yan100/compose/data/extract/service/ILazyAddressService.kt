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
package net.yan100.compose.data.extract.service


import net.yan100.compose.core.SysLogger
import net.yan100.compose.core.consts.IRegexes
import net.yan100.compose.core.exceptions.RemoteCallException
import net.yan100.compose.core.nonText
import net.yan100.compose.core.string
import net.yan100.compose.data.extract.domain.CnDistrictCode

private fun <T> createRequestQueue(
  firstFindCode: CnDistrictCode,
  deepCondition: (param: ILazyAddressService.ILookupFindParam) -> Boolean,
  notFound: (emptyCode: Boolean) -> T? = { null },
): MutableList<CnDistrictCode> {
  val requestQueue = mutableListOf(firstFindCode)
  var lastSize = 0
  while (requestQueue.size > lastSize) {
    val requireRequest = requestQueue.last()
    val r = object : ILazyAddressService.ILookupFindParam {
      override val code = requireRequest.code
      override val level = requireRequest.level
    }
    val findFnResult = deepCondition(r)
    if (!findFnResult) {
      val back = requireRequest.back()
      if (null != back) requestQueue += back else notFound(false)
    }
    lastSize += 1
  }
  return requestQueue
}

interface ILazyAddressService {
  companion object {
    const val DEFAULT_COUNTRY_CODE = "0"

    fun verifyCode(code: String): Boolean {
      return code.matches(IRegexes.CHINA_AD_CODE.toRegex())
    }

    fun convertToFillCode(code: String): String {
      return if (code.nonText()) code
      else {
        if (!verifyCode(code)) code
        else code.padEnd(12, '0')
      }
    }
  }

  interface ILookupFindParam {
    val code: string
    val level: Int
  }

  interface ILookupSortedSaveParam {
    val parentCode: String
    val deepLevel: Int
    val notInit: Boolean
    val result: List<CnDistrict>
  }

  data class CnDistrict(
    val code: CnDistrictCode, val name: String, val yearVersion: String, val level: Int = code.level, val leaf: Boolean = level < 5
  )

  /**
   * 提供的日志记录器
   */
  val logger: SysLogger? get() = null

  /**
   * 所有支持的年份版本
   * 以下为例
   * - `2023`
   * - `2024`
   * - `2018`
   */
  val supportedYearVersions: List<String>
  val supportedDefaultYearVersion: String

  /**
   * 最新的支持的年份版本
   */
  val lastYearVersion: String get() = supportedYearVersions.maxOf { it }

  fun fetchAllByCodeAndLevel(code: string, level: Int, yearVersion: String = lastYearVersion): List<CnDistrict>

  fun findAllProvinces(yearVersion: String = lastYearVersion): List<CnDistrict> {
    return fetchAllByCodeAndLevel(DEFAULT_COUNTRY_CODE, 1)
  }

  fun findAllCityByCode(districtCode: String, yearVersion: String = lastYearVersion): List<CnDistrict> {
    return fetchAllByCodeAndLevel(districtCode, 2)
  }

  fun findAllCountyByCode(districtCode: String, yearVersion: String = lastYearVersion): List<CnDistrict> {
    return fetchAllByCodeAndLevel(districtCode, 3)
  }

  fun findAllTownByCode(districtCode: String, yearVersion: String = lastYearVersion): List<CnDistrict> {
    return fetchAllByCodeAndLevel(districtCode, 4)
  }

  fun findAllVillageByCode(districtCode: String, yearVersion: String = lastYearVersion): List<CnDistrict> {
    return fetchAllByCodeAndLevel(districtCode, 5)
  }


  fun findByCode(code: string, yearVersion: String = lastYearVersion): CnDistrict? {
    return CnDistrictCode(code).back()?.let { a -> findAllChildrenByCode(a.code).find { it.code.code == a.code } }
  }

  fun <T> lookupByCode(
    code: string,
    firstFind: (param: ILookupFindParam) -> T?,
    deepCondition: (param: ILookupFindParam) -> Boolean,
    notFound: (forehead: Boolean) -> T? = { null },
    yearVersion: String = lastYearVersion,
    sortedSave: (param: ILookupSortedSaveParam) -> T?,
  ): T? {
    return CnDistrictCode(code).back()?.let { firstFindCode ->
      if (firstFindCode.empty) return notFound(false)
      val preFind = firstFind(object : ILookupFindParam {
        override val code = firstFindCode.code
        override val level = firstFindCode.level
      })
      if (null != preFind) return preFind
      val requestQueue = createRequestQueue(firstFindCode, deepCondition, notFound)
      var result: T? = null
      requestQueue.reversed().forEach {
        val responses = findAllChildrenByCode(code = it.code, yearVersion)
        val b = object : ILookupSortedSaveParam {
          override val parentCode = it.code
          override val deepLevel = it.level
          override val result = responses
          override val notInit = it.empty
        }
        result = if (responses.isNotEmpty()) sortedSave(b)
        else {
          supportedYearVersions.sorted().reversed().dropLast(1).firstNotNullOfOrNull { y ->
            lookupByCode(
              code, firstFind, deepCondition, notFound, y, sortedSave
            )
          }
        } ?: run {
          logger?.warn("lookupByCode: $code, $result")
          notFound(true)
        }
      }
      result
    }
  }

  /**
   * ## 预取地址数据
   *
   * @param code 地址代码
   * @param firstFind 首次查找函数
   * @param deepCondition 逐级向上查找，报告条件
   * @param notFound 当未找到符合条件或空时，调用
   * @param sortedSave 当拥有需要的上级数据时，进行调用
   * @param yearVersion 年份版本（默认使用最新版本）
   * @return 自定义返回的列表
   */
  fun <T> lookupAllChildrenByCode(
    code: string,
    firstFind: (param: ILookupFindParam) -> List<T>?,
    deepCondition: (param: ILookupFindParam) -> Boolean,
    notFound: (forehead: Boolean) -> List<T>? = { null },
    yearVersion: String = lastYearVersion,
    sortedSave: (param: ILookupSortedSaveParam) -> List<T>,

    ): List<T> {
    val firstFindCode = CnDistrictCode(code)
    if (firstFindCode.empty) return notFound(false) ?: listOf() // code 为空时，返回调用方的数据
    val preFind = firstFind(object : ILookupFindParam {
      override val code = firstFindCode.code
      override val level = firstFindCode.level
    })
    if (!preFind.isNullOrEmpty()) return preFind
    var result = listOf<T>()
    val requestQueue = createRequestQueue(firstFindCode, deepCondition, notFound)

    requestQueue.reversed().forEach {
      val responses = findAllChildrenByCode(code = it.code)
      result = if (responses.isNotEmpty()) {
        sortedSave(object : ILookupSortedSaveParam {
          override val parentCode = it.code
          override val deepLevel = it.level
          override val result = responses
          override val notInit = it.empty
        })
      } else {
        supportedYearVersions.sorted().reversed().dropLast(1).firstNotNullOfOrNull { y ->
          lookupAllChildrenByCode(
            code, firstFind, deepCondition, notFound, y, sortedSave
          )
        } ?: run {
          logger?.warn("lookupAllChildrenByCode: $code, $result")
          notFound(true) ?: emptyList()
        }
      }
    }
    return result
  }

  fun findAllChildrenByCode(code: string, level: Int, yearVersion: String = lastYearVersion): List<CnDistrict> {
    return if (level in 0..4) {
      try {
        when (level) {
          0 -> findAllProvinces(yearVersion)
          1 -> findAllCityByCode(code, yearVersion)
          2 -> findAllCountyByCode(code, yearVersion)
          3 -> findAllTownByCode(code, yearVersion)
          4 -> findAllVillageByCode(code, yearVersion)
          else -> listOf()
        }
      } catch (e: RemoteCallException) {
        logger?.warn("获取地址出错", e)
        emptyList()
      }
    } else emptyList()
  }

  /**
   * ## 预取地址数据
   * 此函数的作用在于，当拥有某些数据时，则不再需要发送网络请求，减少资源消耗 这个函数应用起来可能有些费解 <br/> 首先传入code <br/> preHandle 预处理函数，返回两个值，条件以及结果 <br/> 当 preHandle 返回的条件为 true 时，直接返回结果 <br/> 否则会调用
   * postProcessor 函数，入参为预取的地址数据
   */
  fun <T> lazyFindAllChildrenByCode(code: String, preHandle: () -> Pair<Boolean, List<T>>, postProcessor: (List<CnDistrict>) -> List<T>): List<T> {
    val preFindList = preHandle()
    return if (preFindList.first) {
      preFindList.second
    } else {
      postProcessor(findAllChildrenByCode(code))
    }
  }

  fun findAllChildrenByCode(code: string, yearVersion: String = lastYearVersion): List<CnDistrict> {
    return findAllChildrenByCode(code, CnDistrictCode(code).level, yearVersion)
  }
}
