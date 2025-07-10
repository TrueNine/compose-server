package net.yan100.compose.data.extract.domain

/**
 * # 中国行政区编码
 * 12 省 34 市 56 区县 789 乡镇 10,11,12 村庄
 *
 * @property code 原始输入的行政区编码
 * @property padCode 补全后的12位行政区编码
 * @property provinceCode 省级编码(2位)
 * @property cityCode 市级编码(2位)
 * @property countyCode 区县编码(2位)
 * @property townCode 乡镇编码(3位)
 * @property villageCode 村庄编码(3位)
 * @property empty 是否为空编码
 */
data class CnDistrictCode(
  val code: String,
  val padCode: String,
  val provinceCode: String,
  val cityCode: String,
  val countyCode: String,
  val townCode: String,
  val villageCode: String,
  val empty: Boolean,
) {
  companion object {
    private const val ZERO = "00"
    private const val THREE_ZERO = "000"
    private const val FULL_LENGTH = 12
    private const val PROVINCE_LENGTH = 2
    private const val CITY_LENGTH = 2
    private const val COUNTY_LENGTH = 2
    private const val TOWN_LENGTH = 3
    private const val VILLAGE_LENGTH = 3

    private val INVALID_LENGTHS = setOf(1, 3, 5, 7, 8, 10, 11)

    // 工厂方法，替代原来的主构造函数逻辑
    operator fun invoke(code: String = ""): CnDistrictCode {
      require(!INVALID_LENGTHS.contains(code.length)) { "行政区编码格式缺失" }

      // 补全编码到12位
      val padCode = code.padEnd(FULL_LENGTH, '0')
      val empty = padCode.startsWith(THREE_ZERO)

      // 解析各级编码
      var currentIndex = 0
      val provinceCode = padCode.substring(currentIndex, PROVINCE_LENGTH)
      currentIndex += PROVINCE_LENGTH

      val cityCode = padCode.substring(currentIndex, currentIndex + CITY_LENGTH)
      currentIndex += CITY_LENGTH

      val countyCode = padCode.substring(currentIndex, currentIndex + COUNTY_LENGTH)
      currentIndex += COUNTY_LENGTH

      val townCode = padCode.substring(currentIndex, currentIndex + TOWN_LENGTH)
      currentIndex += TOWN_LENGTH

      val villageCode = padCode.substring(currentIndex, currentIndex + VILLAGE_LENGTH)

      val level = calculateLevel(provinceCode, cityCode, countyCode, townCode, villageCode)
      val actualCode = code.substring(0, getLevelSub(level) ?: 0)

      return CnDistrictCode(
        code = actualCode,
        padCode = padCode,
        provinceCode = provinceCode,
        cityCode = cityCode,
        countyCode = countyCode,
        townCode = townCode,
        villageCode = villageCode,
        empty = empty,
      )
    }

    private fun calculateLevel(provinceCode: String, cityCode: String, countyCode: String, townCode: String, villageCode: String): Int {
      var maxLevel = 5
      if (villageCode == THREE_ZERO) maxLevel--
      if (townCode == THREE_ZERO) maxLevel--
      if (countyCode == ZERO) maxLevel--
      if (cityCode == ZERO) maxLevel--
      if (provinceCode == ZERO) maxLevel--
      return maxLevel
    }

    private fun getLevelSub(level: Int): Int? =
      when (level) {
        1 -> PROVINCE_LENGTH
        2 -> PROVINCE_LENGTH + CITY_LENGTH
        3 -> PROVINCE_LENGTH + CITY_LENGTH + COUNTY_LENGTH
        4 -> PROVINCE_LENGTH + CITY_LENGTH + COUNTY_LENGTH + TOWN_LENGTH
        5 -> FULL_LENGTH
        else -> null
      }
  }

  val level: Int
    get() = calculateLevel(provinceCode, cityCode, countyCode, townCode, villageCode)

  fun back(): CnDistrictCode? =
    when (level) {
      1 -> invoke()
      2 -> invoke(provinceCode)
      3 -> invoke(provinceCode + cityCode)
      4 -> invoke(provinceCode + cityCode + countyCode)
      5 -> invoke(provinceCode + cityCode + countyCode + townCode)
      else -> null
    }

  override fun toString(): String = padCode
}
