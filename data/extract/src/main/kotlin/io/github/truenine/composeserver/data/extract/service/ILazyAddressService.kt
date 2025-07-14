package io.github.truenine.composeserver.data.extract.service

import io.github.truenine.composeserver.SystemLogger
import io.github.truenine.composeserver.consts.IRegexes
import io.github.truenine.composeserver.data.extract.domain.CnDistrictCode
import io.github.truenine.composeserver.nonText
import io.github.truenine.composeserver.string

/**
 * Lazy loading administrative district code service interface.
 *
 * Provides on-demand, versioned access to Chinese administrative district codes (statistical district codes and urban-rural classification codes).
 *
 * Features:
 * - Lazy loading with efficient caching
 * - Multi-version support with automatic fallback
 * - Backward/reverse traversal capabilities
 * - Hierarchical district relationship management
 *
 * @since 1.0.0
 */
interface ILazyAddressService {
  companion object {
    /** Default code representing the country */
    const val DEFAULT_COUNTRY_CODE = "000000000000"
    private val CHINA_AD_CODE_REGEX = IRegexes.CHINA_AD_CODE.toRegex()

    /**
     * Validates whether the given string conforms to Chinese administrative district code format.
     *
     * @param code The code string to validate
     * @return true if the format is valid, false otherwise
     */
    fun verifyCode(code: String): Boolean = code.matches(CHINA_AD_CODE_REGEX)

    /**
     * Converts incomplete codes to full 12-digit format (padding with trailing zeros). Returns original string if input is invalid or doesn't conform to code
     * format.
     *
     * @param code The code string to convert
     * @return Padded 12-digit code string or original string if invalid
     */
    fun convertToFillCode(code: String): String =
      when {
        code.nonText() -> code
        !verifyCode(code) -> code
        else -> code.padEnd(12, '0')
      }

    /**
     * Helper method to attempt creating CnDistrictCode object from string. Implementation classes **may** use this method, but internal code parsing is
     * recommended.
     *
     * @param code The code string
     * @return CnDistrictCode object or null if code is invalid
     */
    fun createCnDistrictCode(code: String?): CnDistrictCode? =
      when {
        code.nonText() -> null
        // Allow maximum 12 digits
        code.length > 12 -> null
        // Validate basic format, allow non-12-digit but conforming to basic numeric and length format
        // Validate after padding
        !verifyCode(code.padEnd(12, '0')) -> null
        else ->
          try {
            CnDistrictCode(code).takeUnless { it.empty }
          } catch (_: IllegalArgumentException) {
            null
          }
      }
  }

  /**
   * Represents information about an administrative district unit.
   *
   * @property code District code object
   * @property name District name
   * @property yearVersion Data year version
   * @property level Administrative level (1:Province, 2:City, 3:County, 4:Town, 5:Village)
   * @property leaf Whether this is a leaf node (usually village level or cannot be drilled down further)
   */
  data class CnDistrict(val code: CnDistrictCode, val name: String, val yearVersion: String, val level: Int = code.level, val leaf: Boolean = level >= 5)

  // --- 服务属性 ---
  /** Optional logger for the service */
  val logger: SystemLogger?
    get() = null

  /**
   * All supported year versions.
   *
   * Example: `["2023", "2024", "2018"]`
   */
  val supportedYearVersions: List<String>

  /** Default year version to use */
  val supportedDefaultYearVersion: String

  /** Maximum supported administrative level, defaults to 5 (village level) */
  val supportedMaxLevel: Int
    get() = 5

  /**
   * Gets the previous year version before the current year version. Based on [supportedYearVersions].
   *
   * @param yearVersion Current year version to find predecessor for
   * @return Previous year version or null if none exists
   */
  fun lastYearVersionOrNull(yearVersion: String): String? {
    if (yearVersion.nonText()) return null
    val currentYearVersion = yearVersion.toIntOrNull() ?: return null
    // 查找小于当前年份且最接近的版本
    return supportedYearVersions.mapNotNull { it.toIntOrNull() }.distinct().sortedDescending().filter { it < currentYearVersion }.maxOrNull()?.toString()
  }

  /** Latest supported year version */
  val lastYearVersion: String
    get() = supportedYearVersions.maxOrNull() ?: supportedDefaultYearVersion

  /**
   * Gets the **direct children** list of the administrative district represented by the specified code.
   *
   * For example, given a province code, returns all cities under that province; given country code "0", returns all provinces.
   *
   * Implementation classes need to handle cases where `parentCode` is invalid or not found, and are responsible for finding data based on `yearVersion`
   * **without** automatic version fallback.
   *
   * @param parentCode Parent administrative district code (e.g., province code "110000000000", country code "0")
   * @param yearVersion Data year version to search
   * @return List of direct children [CnDistrict], empty list if parent code is invalid, has no children, or no data for specified year
   */
  fun fetchChildren(parentCode: string, yearVersion: String): List<CnDistrict>

  /**
   * Gets all provinces list (direct children of country "0"). Implementation classes should call `fetchChildren(DEFAULT_COUNTRY_CODE, yearVersion)`.
   *
   * @param yearVersion Data year version
   * @return List of province [CnDistrict]
   */
  fun fetchAllProvinces(yearVersion: String = supportedDefaultYearVersion): List<CnDistrict> = fetchChildren(DEFAULT_COUNTRY_CODE, yearVersion)

  /**
   * Finds single administrative district information corresponding to the specified code.
   *
   * Implementation classes need to handle cases where `code` is invalid or not found. Implementation classes **must** handle version fallback logic: if
   * `yearVersion` is not found, try using `lastYearVersionOrNull` to get earlier versions for searching until found or all versions tried.
   *
   * @param code Administrative district code to search (can be partial or complete code at any level)
   * @param yearVersion **Starting** data year version to search (pass `lastYearVersion` to always start from latest)
   * @return Found [CnDistrict] information (should include actual year version where data was found), null if not found in any supported year version
   */
  fun fetchDistrict(code: string, yearVersion: String): CnDistrict?

  /**
   * Recursively finds **all descendant** administrative districts under the specified code, up to the specified maximum depth.
   *
   * Implementation classes need to handle cases where `parentCode` is invalid or not found, and handle `maxDepth`. Implementation classes **must** handle
   * version fallback logic: for each level searched, if child nodes are not found in one version, try earlier versions.
   *
   * Note: Version fallback logic can be complex, e.g., parent node found in V2, child nodes found in V1.
   *
   * @param parentCode Parent administrative district code
   * @param maxDepth Maximum search depth relative to `parentCode` (e.g., `maxDepth = 1` means only direct children, equivalent to `fetchChildren`)
   * @param yearVersion **Starting** data year version to search (each recursion level should start trying from this version)
   * @return List of all qualifying descendant [CnDistrict] (each District should include actual year version where data was found)
   */
  fun fetchChildrenRecursive(parentCode: string, maxDepth: Int = supportedMaxLevel, yearVersion: String = lastYearVersion): List<CnDistrict>

  /**
   * Recursively processes all descendant administrative districts under the specified code using traversal.
   *
   * This method allows callers to control the traversal process through callback functions, suitable for large-scale database operations.
   *
   * @param parentCode Parent administrative district code
   * @param maxDepth Maximum traversal depth relative to `parentCode`
   * @param yearVersion **Starting** data year version for traversal
   * @param onVisit Node visit callback function, return true to continue traversal, false to stop current branch traversal. Parameter descriptions:
   *     - children: Direct child node list of currently visited node
   *     - depth: Depth of current node relative to parentCode (starting from 1)
   *     - parentDistrict: Parent node information (null if top-level node)
   */
  fun traverseChildrenRecursive(
    parentCode: string,
    maxDepth: Int = supportedMaxLevel,
    yearVersion: String = lastYearVersion,
    onVisit: (children: List<CnDistrict>, depth: Int, parentDistrict: CnDistrict?) -> Boolean,
  )
}
