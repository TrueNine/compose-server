package io.github.truenine.composeserver.data.extract.domain

/**
 * Chinese administrative district code representation following the 12-digit national standard.
 *
 * The code structure: [Province(2)][City(2)][County(2)][Town(3)][Village(3)] Example: 110101001001 represents a specific village in Beijing Dongcheng District.
 *
 * This implementation optimizes memory usage and computation performance by:
 * - Caching level calculations to avoid repeated computation
 * - Using efficient string operations with pre-allocated StringBuilder
 * - Validating input format early to fail fast
 *
 * @property code Original input administrative district code
 * @property padCode Padded 12-digit administrative district code
 * @property provinceCode Province code (2 digits)
 * @property cityCode City code (2 digits)
 * @property countyCode County code (2 digits)
 * @property townCode Town code (3 digits)
 * @property villageCode Village code (3 digits)
 * @property empty Whether this represents an empty/null district code
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

    // Pre-computed level boundaries for performance optimization
    private val LEVEL_BOUNDARIES =
      intArrayOf(
        PROVINCE_LENGTH,
        PROVINCE_LENGTH + CITY_LENGTH,
        PROVINCE_LENGTH + CITY_LENGTH + COUNTY_LENGTH,
        PROVINCE_LENGTH + CITY_LENGTH + COUNTY_LENGTH + TOWN_LENGTH,
        FULL_LENGTH,
      )

    /**
     * Factory method to create CnDistrictCode instance with optimized parsing.
     *
     * Performance optimizations:
     * - Early validation to fail fast on invalid input
     * - Single-pass parsing to minimize string operations
     * - Cached level calculation using pre-computed boundaries
     *
     * @param code Input district code (can be partial, will be padded to 12 digits)
     * @return CnDistrictCode instance
     * @throws IllegalArgumentException if code length is invalid
     */
    operator fun invoke(code: String = ""): CnDistrictCode {
      require(!INVALID_LENGTHS.contains(code.length)) { "Invalid district code format: length ${code.length} is not supported" }

      // Optimize padding operation using StringBuilder for better performance
      val padCode =
        if (code.length == FULL_LENGTH) {
          code
        } else {
          buildString(FULL_LENGTH) {
            append(code)
            repeat(FULL_LENGTH - code.length) { append('0') }
          }
        }

      val empty = padCode.startsWith(THREE_ZERO)

      // Single-pass parsing with optimized substring operations
      val provinceCode = padCode.substring(0, PROVINCE_LENGTH)
      val cityCode = padCode.substring(PROVINCE_LENGTH, PROVINCE_LENGTH + CITY_LENGTH)
      val countyCode = padCode.substring(PROVINCE_LENGTH + CITY_LENGTH, PROVINCE_LENGTH + CITY_LENGTH + COUNTY_LENGTH)
      val townCode = padCode.substring(PROVINCE_LENGTH + CITY_LENGTH + COUNTY_LENGTH, PROVINCE_LENGTH + CITY_LENGTH + COUNTY_LENGTH + TOWN_LENGTH)
      val villageCode = padCode.substring(PROVINCE_LENGTH + CITY_LENGTH + COUNTY_LENGTH + TOWN_LENGTH, FULL_LENGTH)

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

    /**
     * Calculates administrative level based on code components.
     *
     * Performance optimization: Uses early termination to avoid unnecessary checks.
     *
     * @param provinceCode Province code component
     * @param cityCode City code component
     * @param countyCode County code component
     * @param townCode Town code component
     * @param villageCode Village code component
     * @return Administrative level (0-5, where 0 is empty and 5 is village level)
     */
    private fun calculateLevel(provinceCode: String, cityCode: String, countyCode: String, townCode: String, villageCode: String): Int {
      // Early termination optimization - check from most specific to least specific
      if (provinceCode == ZERO) return 0
      if (cityCode == ZERO) return 1
      if (countyCode == ZERO) return 2
      if (townCode == THREE_ZERO) return 3
      if (villageCode == THREE_ZERO) return 4
      return 5
    }

    /**
     * Gets the substring length for a given administrative level.
     *
     * @param level Administrative level (1-5)
     * @return Substring length for the level, null if invalid level
     */
    private fun getLevelSub(level: Int): Int? = if (level in 1..5) LEVEL_BOUNDARIES[level - 1] else null
  }

  // Lazy property with caching to avoid repeated calculation
  val level: Int by lazy { calculateLevel(provinceCode, cityCode, countyCode, townCode, villageCode) }

  /**
   * Creates a parent district code by moving up one administrative level.
   *
   * Performance optimization: Uses pre-computed string concatenation patterns to avoid repeated string building operations.
   *
   * @return Parent district code, or null if already at top level
   */
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
