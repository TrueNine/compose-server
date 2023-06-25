package net.yan100.compose.datacommon.dataextract.models


class CnDistrictCode(code: String?) {
  companion object {
    const val zero = "00"
    const val threeZero = "000"
  }

  var code: String? = null
  var provinceCode: String? = null
  var cityCode: String? = null
  var countyCode: String? = null
  var townCode: String? = null
  var villageCode: String? = null

  val level: Int
    get() {
      var maxLevel = 5
      if (villageCode == threeZero) maxLevel -= 1
      if (townCode == threeZero) maxLevel -= 1
      if (countyCode == zero) maxLevel -= 1
      if (cityCode == zero) maxLevel -= 1
      if (provinceCode == zero) maxLevel -= 1
      return maxLevel
    }

  init {
    checkNotNull(code) { "传入的 code 不能为空" }
    val internalCode = code.toLongOrNull()
    if (internalCode in 100000000000L..1000000000000L) {
      this.code = code
      provinceCode = code.substring(0, 2)
      cityCode = code.substring(2, 4)
      countyCode = code.substring(4, 6)
      townCode = code.substring(6, 9)
      villageCode = code.substring(9, 12)
    } else {
      throw IllegalArgumentException("行政区编码不满足 12 数值 要求：code = $code")
    }
  }
}
