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
  deepCondition: (param: ILazyAddressService.LookupFindDto) -> Boolean,
  notFound: (forehead: Boolean) -> Unit? = { },
): List<CnDistrictCode> {
  val requestQueue = mutableListOf(firstFindCode)
  var lastSize = 0
  while (requestQueue.size > lastSize) {
    val requireRequest = requestQueue.last()
    val r = ILazyAddressService.LookupFindDto(
      code = requireRequest.code, level = requireRequest.level
    )
    val findFnResult = deepCondition(r)
    if (!findFnResult) {
      val back = requireRequest.back()
      if (null != back) requestQueue += back
    }
    lastSize += 1
  }
  return requestQueue.reversed()
}

interface ILazyAddressService {
  companion object {
    const val DEFAULT_COUNTRY_CODE = "0"

    fun verifyCode(code: String): Boolean {
      return code.matches(IRegexes.CHINA_AD_CODE.toRegex())
    }

    fun createCnDistrict(code: String?): CnDistrictCode? {
      if (code.nonText()) return null
      if (code.length > 12) return null
      val codeObj = CnDistrictCode(code)
      if (codeObj.empty) return null
      return codeObj
    }

    fun convertToFillCode(code: String): String {
      return if (code.nonText()) code
      else {
        if (!verifyCode(code)) code
        else code.padEnd(12, '0')
      }
    }
  }

  data class LookupFindDto(
    val code: string, val level: Int
  )

  data class LookupSortedSaveVo(
    val parentCode: String, val deepLevel: Int, val notInit: Boolean, val yearVersion: String, val result: List<CnDistrict>
  )

  data class CnDistrict(
    val code: CnDistrictCode, val name: String, val yearVersion: String, val level: Int = code.level, val leaf: Boolean = level >= 5
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
  val supportedMaxLevel: Int get() = 5

  /**
   * 获取当前年份版本之前一个年份版本
   * 基于 [supportedYearVersions]
   */
  fun lastYearVersionOrNull(yearVersion: String): String? {
    if (yearVersion.nonText()) return null
    val sortedVersions = supportedYearVersions.sorted().reversed()
    val currentYearVersion = yearVersion.toIntOrNull() ?: return null
    return sortedVersions.firstNotNullOfOrNull {
      if (it.toInt() < currentYearVersion) it
      else null
    }
  }

  /**
   * 最新的支持的年份版本
   */
  val lastYearVersion: String get() = supportedYearVersions.maxOf { it }

  fun fetchAllByCodeAndLevel(code: string, level: Int, yearVersion: String = lastYearVersion): List<CnDistrict>

  fun findAllProvinces(yearVersion: String = lastYearVersion): List<CnDistrict> {
    return fetchAllByCodeAndLevel(DEFAULT_COUNTRY_CODE, 1, yearVersion)
  }

  fun findAllCityByCode(districtCode: String, yearVersion: String = lastYearVersion): List<CnDistrict> {
    return fetchAllByCodeAndLevel(districtCode, 2, yearVersion)
  }

  fun findAllCountyByCode(districtCode: String, yearVersion: String = lastYearVersion): List<CnDistrict> {
    return fetchAllByCodeAndLevel(districtCode, 3, yearVersion)
  }

  fun findAllTownByCode(districtCode: String, yearVersion: String = lastYearVersion): List<CnDistrict> {
    return fetchAllByCodeAndLevel(districtCode, 4, yearVersion)
  }

  fun findAllVillageByCode(districtCode: String, yearVersion: String = lastYearVersion): List<CnDistrict> {
    return fetchAllByCodeAndLevel(districtCode, 5, yearVersion)
  }

  fun findByCode(code: string, yearVersion: String = lastYearVersion): CnDistrict? {
    return CnDistrictCode(code).back()?.let { a -> findAllChildrenByCode(a.code).find { it.code.code == a.code } }
  }

  /**
   * ## 预取地址数据（根据代码查找单个地址）
   *
   * @param code 地址代码
   * @param firstFind 首次查找函数
   * @param deepCondition 逐级向上查找，报告条件
   * @param notFound 当未找到符合条件或空时，调用
   * @param sortedSave 当拥有需要的上级数据时，进行调用
   * @param yearVersion 年份版本（默认使用最新版本）
   * @return 自定义返回结果
   */
  fun <T> lookupByCode(
    code: string,
    firstFind: (param: LookupFindDto) -> T? = { null },
    deepCondition: (param: LookupFindDto) -> Boolean = { false },
    notFound: (forehead: Boolean) -> Unit? = { },
    yearVersion: String = lastYearVersion,
    sortedSave: (param: LookupSortedSaveVo) -> T? = { null },
  ): T? {
    fun deepFind(
      code: string,
      firstFind: (param: LookupFindDto) -> T?,
      deepCondition: (param: LookupFindDto) -> Boolean,
      notFound: (forehead: Boolean) -> Unit? = { },
      yearVersion: String = lastYearVersion,
      end: Boolean = false,
      sortedSave: (param: LookupSortedSaveVo) -> T?,
    ): Pair<T?, Boolean> {
      val codeObj = createCnDistrict(code) ?: return null to true
      return codeObj.back()?.let { firstFindCode ->
        var result = firstFind(
          LookupFindDto(
            code = firstFindCode.code, level = firstFindCode.level
          )
        )
        if (null != result) return result to true
        val requestQueue = createRequestQueue<List<CnDistrictCode>>(firstFindCode, deepCondition, notFound)

        fun notFound(): Pair<T?, Boolean> {
          return if (end) {
            notFound(true)
            logger?.warn("lookupByCode all not found: $code, lastYearVersion: {}", yearVersion)
            return null to true
          } else {
            val next = lastYearVersionOrNull(yearVersion)
            logger?.debug("code recursion in next version: {}", next)
            deepFind(codeObj.code, firstFind, deepCondition, notFound, next ?: yearVersion, next.isNullOrEmpty(), sortedSave)
          }
        }

        for (it in requestQueue) {
          val responses = findAllChildrenByCode(code = it.code, yearVersion)
          val saveVo = LookupSortedSaveVo(
            parentCode = it.code, yearVersion = yearVersion, deepLevel = it.level, result = responses, notInit = it.empty
          )

          result = if (responses.isNotEmpty()) sortedSave(saveVo)
          else notFound().first
        }
        if (null == result) notFound()
        else result to false
      } ?: run {
        notFound(true)
        null to true
      }
    }
    return deepFind(code, firstFind, deepCondition, notFound, yearVersion, false, sortedSave).first
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
    firstFind: (param: LookupFindDto) -> List<T>? = { _ -> null },
    deepCondition: (param: LookupFindDto) -> Boolean = { false },
    notFound: (forehead: Boolean) -> Unit? = { },
    yearVersion: String = lastYearVersion,
    sortedSave: (param: LookupSortedSaveVo) -> List<T> = { emptyList() },
  ): List<T> {
    fun deepFind(
      code: string,
      firstFind: (param: LookupFindDto) -> List<T>?,
      deepCondition: (param: LookupFindDto) -> Boolean,
      notFound: (forehead: Boolean) -> Unit? = { },
      yearVersion: String = lastYearVersion,
      end: Boolean = false,
      sortedSave: (param: LookupSortedSaveVo) -> List<T>,
    ): Pair<List<T>, Boolean> {
      val toCode = createCnDistrict(code) ?: return emptyList<T>() to true
      logger?.debug("begin find code: {} yearVersion: {}", code, yearVersion)
      val preFind = if (yearVersion == lastYearVersion) firstFind(
        LookupFindDto(
          code = toCode.code, level = toCode.level
        )
      ) else {
        logger?.debug("skip first find")
        null
      }
      if (!preFind.isNullOrEmpty()) return preFind to true
      val requestQueue = createRequestQueue<List<CnDistrictCode>>(toCode, deepCondition, notFound)
      var result = listOf<T>()
      for (it in requestQueue) {
        val responses = findAllChildrenByCode(code = it.code, yearVersion)
        logger?.debug("implemented responses: {}", responses)
        result = if (responses.isNotEmpty()) {
          sortedSave(
            LookupSortedSaveVo(
              parentCode = it.code, yearVersion = yearVersion, deepLevel = it.level, result = responses, notInit = it.empty
            )
          )
        } else if (end) {
          notFound(true)
          logger?.warn("lookupAllChildrenByCode all not found: $code, lastYearVersion: {}", yearVersion)
          return emptyList<T>() to true
        } else {
          logger?.warn(" not found code: {} version: {}", it, yearVersion)
          val sortedVersions = supportedYearVersions.sorted().reversed()
          val currentYearVersion = yearVersion.toInt()
          val next = sortedVersions.firstNotNullOfOrNull {
            if (it.toInt() < currentYearVersion) it
            else null
          }
          logger?.debug("recursion in next version: {}", next)
          val r = deepFind(code, firstFind, deepCondition, notFound, next ?: yearVersion, next.isNullOrEmpty(), sortedSave)
          if (r.second) return r
          else r.first
        }
      }
      return result to true
    }
    return deepFind(code, firstFind, deepCondition, notFound, yearVersion, false, sortedSave).first
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
  fun <T> lazyFindAllChildrenByCode(code: String, preHandle: () -> Pair<Boolean, List<T>>, postProcessor: (List<CnDistrict>) -> List<T>): List<T?> {
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
