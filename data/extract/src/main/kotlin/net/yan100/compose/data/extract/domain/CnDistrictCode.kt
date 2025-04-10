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
class CnDistrictCode(code: String = "") {
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
  }

  val code: String
  val padCode: String
  val provinceCode: String
  val cityCode: String
  val countyCode: String
  val townCode: String
  val villageCode: String
  val empty: Boolean

  val level: Int
    get() = calculateLevel()

  private val levelSub: Int?
    get() =
      when (level) {
        1 -> PROVINCE_LENGTH
        2 -> PROVINCE_LENGTH + CITY_LENGTH
        3 -> PROVINCE_LENGTH + CITY_LENGTH + COUNTY_LENGTH
        4 -> PROVINCE_LENGTH + CITY_LENGTH + COUNTY_LENGTH + TOWN_LENGTH
        5 -> FULL_LENGTH
        else -> null
      }

  init {
    require(!INVALID_LENGTHS.contains(code.length)) { "行政区编码格式缺失" }

    // 补全编码到12位
    padCode = code.padEnd(FULL_LENGTH, '0')
    empty = padCode.startsWith(THREE_ZERO)

    // 解析各级编码
    var currentIndex = 0
    provinceCode = padCode.substring(currentIndex, PROVINCE_LENGTH)
    currentIndex += PROVINCE_LENGTH

    cityCode = padCode.substring(currentIndex, currentIndex + CITY_LENGTH)
    currentIndex += CITY_LENGTH

    countyCode = padCode.substring(currentIndex, currentIndex + COUNTY_LENGTH)
    currentIndex += COUNTY_LENGTH

    townCode = padCode.substring(currentIndex, currentIndex + TOWN_LENGTH)
    currentIndex += TOWN_LENGTH

    villageCode = padCode.substring(currentIndex, currentIndex + VILLAGE_LENGTH)

    this.code = code.substring(0, levelSub ?: 0)
  }

  private fun calculateLevel(): Int {
    var maxLevel = 5
    if (villageCode == THREE_ZERO) maxLevel--
    if (townCode == THREE_ZERO) maxLevel--
    if (countyCode == ZERO) maxLevel--
    if (cityCode == ZERO) maxLevel--
    if (provinceCode == ZERO) maxLevel--
    return maxLevel
  }

  fun back(): CnDistrictCode? =
    when (level) {
      1 -> CnDistrictCode()
      2 -> CnDistrictCode(provinceCode)
      3 -> CnDistrictCode(provinceCode + cityCode)
      4 -> CnDistrictCode(provinceCode + cityCode + countyCode)
      5 -> CnDistrictCode(provinceCode + cityCode + countyCode + townCode)
      else -> null
    }

  override fun toString(): String = padCode
}
