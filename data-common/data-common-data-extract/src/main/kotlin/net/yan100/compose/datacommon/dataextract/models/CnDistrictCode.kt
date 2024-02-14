package net.yan100.compose.datacommon.dataextract.models

/**
 * # 中国行政区编码
 * 12 省
 * 34 市
 * 56 区县
 * 789 乡镇
 * 10,11,12 村庄
 */
class CnDistrictCode(code: String = "") {
    companion object {
        const val zero = "00"
        const val threeZero = "000"
    }

    var code: String
    var provinceCode: String
    var cityCode: String
    var countyCode: String
    var townCode: String
    var villageCode: String
    var empty: Boolean = false


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
        val padCode = code.let {
            when (it.length) {
                1, 3, 5, 7, 8, 10, 11 -> throw IllegalArgumentException("行政区编码格式缺失")
                else -> it.padEnd(12, '0')
            }
        }
        this.empty = padCode.startsWith(threeZero)
        this.code = padCode
        provinceCode = padCode.substring(0, 2)
        cityCode = padCode.substring(2, 4)
        countyCode = padCode.substring(4, 6)
        townCode = padCode.substring(6, 9)
        villageCode = padCode.substring(9, 12)
    }

    fun back(): CnDistrictCode? {
        return when (level) {
            1 -> CnDistrictCode()
            2 -> CnDistrictCode(provinceCode)
            3 -> CnDistrictCode(provinceCode + cityCode)
            4 -> CnDistrictCode(provinceCode + cityCode + countyCode)
            5 -> CnDistrictCode(provinceCode + cityCode + countyCode + townCode)
            else -> null
        }
    }
}
