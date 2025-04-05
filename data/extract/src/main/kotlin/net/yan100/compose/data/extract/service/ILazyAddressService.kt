package net.yan100.compose.data.extract.service

import net.yan100.compose.core.SysLogger
import net.yan100.compose.core.consts.IRegexes
import net.yan100.compose.core.exceptions.RemoteCallException
import net.yan100.compose.core.nonText
import net.yan100.compose.core.string
import net.yan100.compose.data.extract.domain.CnDistrictCode
import java.util.concurrent.ConcurrentHashMap

private fun createRequestQueue(
  firstFindCode: CnDistrictCode,
  deepCondition: (param: ILazyAddressService.LookupFindDto) -> Boolean,
): List<CnDistrictCode> = buildList {
  add(firstFindCode)
  var lastSize = 0
  while (size > lastSize) {
    val requireRequest = last()
    val lookupDto = ILazyAddressService.LookupFindDto(
      code = requireRequest.code,
      level = requireRequest.level,
    )
    if (!deepCondition(lookupDto)) {
      requireRequest.back()?.let { add(it) }
    }
    lastSize++
  }
}.reversed()

interface ILazyAddressService {
  companion object {
    const val DEFAULT_COUNTRY_CODE = "0"
    private val CHINA_AD_CODE_REGEX = IRegexes.CHINA_AD_CODE.toRegex()
    private val districtCache = ConcurrentHashMap<String, CnDistrict>()
    private val childrenCache = ConcurrentHashMap<String, List<CnDistrict>>()

    fun verifyCode(code: String): Boolean = code.matches(CHINA_AD_CODE_REGEX)

    fun createCnDistrict(code: String?): CnDistrictCode? = when {
      code.nonText() -> null
      code.length > 12 -> null
      else -> CnDistrictCode(code).takeUnless { it.empty }
    }

    fun convertToFillCode(code: String): String = when {
      code.nonText() -> code
      !verifyCode(code) -> code
      else -> code.padEnd(12, '0')
    }

    private fun getCacheKey(code: String, yearVersion: String) = "${code}_$yearVersion"
  }

  data class LookupFindDto(val code: string, val level: Int)

  data class LookupSortedSaveVo(
    val parentCode: String,
    val deepLevel: Int,
    val notInit: Boolean,
    val yearVersion: String,
    val result: List<CnDistrict>,
  )

  data class CnDistrict(
    val code: CnDistrictCode,
    val name: String,
    val yearVersion: String,
    val level: Int = code.level,
    val leaf: Boolean = level >= 5,
  )

  /** 提供的日志记录器 */
  val logger: SysLogger?
    get() = null

  /**
   * 所有支持的年份版本 以下为例
   * - `2023`
   * - `2024`
   * - `2018`
   */
  val supportedYearVersions: List<String>
  val supportedDefaultYearVersion: String
  val supportedMaxLevel: Int
    get() = 5

  /** 获取当前年份版本之前一个年份版本 基于 [supportedYearVersions] */
  fun lastYearVersionOrNull(yearVersion: String): String? {
    if (yearVersion.nonText()) return null
    val currentYearVersion = yearVersion.toIntOrNull() ?: return null
    return supportedYearVersions
      .sorted()
      .reversed()
      .firstOrNull { it.toInt() < currentYearVersion }
  }

  /** 最新的支持的年份版本 */
  val lastYearVersion: String
    get() = supportedYearVersions.maxOf { it }

  fun fetchAllByCodeAndLevel(
    code: string,
    level: Int,
    yearVersion: String = lastYearVersion,
  ): List<CnDistrict>

  fun findAllProvinces(yearVersion: String = lastYearVersion): List<CnDistrict> =
    fetchAllByCodeAndLevel(DEFAULT_COUNTRY_CODE, 1, yearVersion)

  fun findAllCityByCode(
    districtCode: String,
    yearVersion: String = lastYearVersion,
  ): List<CnDistrict> = fetchAllByCodeAndLevel(districtCode, 2, yearVersion)

  fun findAllCountyByCode(
    districtCode: String,
    yearVersion: String = lastYearVersion,
  ): List<CnDistrict> = fetchAllByCodeAndLevel(districtCode, 3, yearVersion)

  fun findAllTownByCode(
    districtCode: String,
    yearVersion: String = lastYearVersion,
  ): List<CnDistrict> = fetchAllByCodeAndLevel(districtCode, 4, yearVersion)

  fun findAllVillageByCode(
    districtCode: String,
    yearVersion: String = lastYearVersion,
  ): List<CnDistrict> = fetchAllByCodeAndLevel(districtCode, 5, yearVersion)

  fun findByCode(
    code: string,
    yearVersion: String = lastYearVersion,
  ): CnDistrict? {
    val cacheKey = getCacheKey(code, yearVersion)
    return districtCache.getOrPut(cacheKey) {
      CnDistrictCode(code).back()?.let { backCode ->
        findAllChildrenByCode(backCode.code).find { it.code.code == backCode.code }
      } ?: return null
    }
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
    notFound: (forehead: Boolean) -> Unit? = {},
    yearVersion: String = lastYearVersion,
    sortedSave: (param: LookupSortedSaveVo) -> T? = { null },
  ): T? {
    val codeObj = createCnDistrict(code) ?: return null
    var currentYearVersion = yearVersion
    var result: T? = null
    var isEnd = false

    while (!isEnd) {
      codeObj.back()?.let { firstFindCode ->
        // 尝试首次查找
        firstFind(LookupFindDto(code = firstFindCode.code, level = firstFindCode.level))?.let {
          return it
        }

        // 创建请求队列
        val requestQueue = createRequestQueue(firstFindCode, deepCondition)

        // 处理请求队列
        for (request in requestQueue) {
          val responses = findAllChildrenByCode(code = request.code, currentYearVersion)
          if (responses.isNotEmpty()) {
            result = sortedSave(
              LookupSortedSaveVo(
                parentCode = request.code,
                yearVersion = currentYearVersion,
                deepLevel = request.level,
                result = responses,
                notInit = request.empty
              )
            )
            if (result != null) {
              return result
            }
          }
        }

        // 如果当前版本未找到，尝试上一个版本
        val nextVersion = lastYearVersionOrNull(currentYearVersion)
        if (nextVersion == null) {
          isEnd = true
          notFound(true)
          logger?.warn("lookupByCode all not found: $code, lastYearVersion: {}", currentYearVersion)
        } else {
          currentYearVersion = nextVersion
          logger?.debug("code recursion in next version: {}", nextVersion)
        }
      } ?: run {
        isEnd = true
        notFound(true)
      }
    }

    return result
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
    firstFind: (param: LookupFindDto) -> List<T>? = { null },
    deepCondition: (param: LookupFindDto) -> Boolean = { false },
    notFound: (forehead: Boolean) -> Unit? = {},
    yearVersion: String = lastYearVersion,
    sortedSave: (param: LookupSortedSaveVo) -> List<T> = { emptyList() },
  ): List<T> {
    val toCode = createCnDistrict(code) ?: return emptyList()
    var currentYearVersion = yearVersion
    var result = listOf<T>()
    
    // 尝试从缓存获取结果
    val cacheKey = getCacheKey(code, currentYearVersion)
    @Suppress("UNCHECKED_CAST")
    childrenCache[cacheKey]?.let { cached ->
        return sortedSave(
            LookupSortedSaveVo(
                parentCode = code,
                yearVersion = currentYearVersion,
                deepLevel = toCode.level,
                result = cached,
                notInit = toCode.empty
            )
        )
    }

    // 尝试首次查找（仅在最新版本时）
    if (currentYearVersion == lastYearVersion) {
        firstFind(LookupFindDto(code = toCode.code, level = toCode.level))?.let {
            return it
        }
    }

    // 创建请求队列
    val requestQueue = createRequestQueue(toCode, deepCondition)
    
    while (true) {
        var found = false
        for (request in requestQueue) {
            val responses = findAllChildrenByCode(code = request.code, currentYearVersion)
            logger?.debug("implemented responses: {}", responses)

            if (responses.isNotEmpty()) {
                result = sortedSave(
                    LookupSortedSaveVo(
                        parentCode = request.code,
                        yearVersion = currentYearVersion,
                        deepLevel = request.level,
                        result = responses,
                        notInit = request.empty
                    )
                )
                found = true
                break
            }
        }

        if (found) break

        val nextVersion = lastYearVersionOrNull(currentYearVersion)
        if (nextVersion == null) {
            notFound(true)
            logger?.warn(
                "lookupAllChildrenByCode all not found: $code, lastYearVersion: {}",
                currentYearVersion
            )
            return emptyList()
        }
        
        currentYearVersion = nextVersion
        logger?.debug("recursion in next version: {}", nextVersion)
    }

    // 缓存结果
    if (result.isNotEmpty()) {
        @Suppress("UNCHECKED_CAST")
        childrenCache[cacheKey] = result as List<CnDistrict>
    }

    return result
  }

  fun findAllChildrenByCode(
    code: string,
    level: Int,
    yearVersion: String = lastYearVersion,
  ): List<CnDistrict> = when {
    level !in 0 .. 4 -> emptyList()
    else -> try {
      when (level) {
        0 -> findAllProvinces(yearVersion)
        1 -> findAllCityByCode(code, yearVersion)
        2 -> findAllCountyByCode(code, yearVersion)
        3 -> findAllTownByCode(code, yearVersion)
        4 -> findAllVillageByCode(code, yearVersion)
        else -> emptyList()
      }
    } catch (e: RemoteCallException) {
      logger?.warn("获取地址出错", e)
      emptyList()
    }
  }

  /**
   * ## 预取地址数据
   * 此函数的作用在于，当拥有某些数据时，则不再需要发送网络请求，减少资源消耗 这个函数应用起来可能有些费解 <br/> 首先传入code <br/>
   * preHandle 预处理函数，返回两个值，条件以及结果 <br/> 当 preHandle 返回的条件为 true 时，直接返回结果 <br/>
   * 否则会调用 postProcessor 函数，入参为预取的地址数据
   */
  fun <T> lazyFindAllChildrenByCode(
    code: String,
    preHandle: () -> Pair<Boolean, List<T>>,
    postProcessor: (List<CnDistrict>) -> List<T>,
  ): List<T?> {
    val (shouldUsePreResult, preResult) = preHandle()
    return if (shouldUsePreResult) {
      preResult
    } else {
      postProcessor(findAllChildrenByCode(code))
    }
  }

  fun findAllChildrenByCode(
    code: string,
    yearVersion: String = lastYearVersion,
  ): List<CnDistrict> {
    val cacheKey = getCacheKey(code, yearVersion)
    return childrenCache.getOrPut(cacheKey) {
      findAllChildrenByCode(code, CnDistrictCode(code).level, yearVersion)
    }
  }
}
