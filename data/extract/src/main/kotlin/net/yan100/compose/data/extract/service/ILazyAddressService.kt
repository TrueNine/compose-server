package net.yan100.compose.data.extract.service

import net.yan100.compose.SysLogger
import net.yan100.compose.consts.IRegexes
import net.yan100.compose.data.extract.domain.CnDistrictCode
import net.yan100.compose.nonText
import net.yan100.compose.string

/**
 * 懒加载行政区划代码服务接口。
 * 提供按需、分版本获取中国行政区划代码（统计用区划代码和城乡划分代码）的功能。
 */
interface ILazyAddressService {
  companion object {
    /** 默认表示国家的代码 */
    const val DEFAULT_COUNTRY_CODE = "000000000000"
    private val CHINA_AD_CODE_REGEX = IRegexes.CHINA_AD_CODE.toRegex()

    /**
     * 校验给定的字符串是否符合中国行政区划代码的格式。
     * @param code 待校验的代码字符串。
     * @return 如果符合格式则返回 true，否则返回 false。
     */
    fun verifyCode(code: String): Boolean = code.matches(CHINA_AD_CODE_REGEX)

    /**
     * 将不完整的12位代码补全为12位（末尾补0）。
     * 如果输入无效或不符合代码格式，则原样返回。
     * @param code 待补全的代码字符串。
     * @return 补全后的12位代码字符串或原始字符串。
     */
    fun convertToFillCode(code: String): String = when {
      code.nonText() -> code
      !verifyCode(code) -> code
      else -> code.padEnd(12, '0')
    }

    /**
     * (公开辅助方法) 尝试根据字符串创建 CnDistrictCode 对象。
     * 实现类 **可以** 使用此方法，但推荐在内部处理代码解析。
     * @param code 代码字符串。
     * @return CnDistrictCode 对象或 null（如果代码无效）。
     */
    fun createCnDistrictCode(code: String?): CnDistrictCode? = when {
      code.nonText() -> null
      // 允许最长12位
      code.length > 12 -> null
      // 验证基础格式，允许非12位但符合基本数字和长度的格式
      // 补全后再验证
      !verifyCode(code.padEnd(12, '0')) -> null
      else -> try {
        CnDistrictCode(code).takeUnless { it.empty }
      } catch (_: IllegalArgumentException) {
        null
      }
    }
  }

  /**
   * 表示一个行政区划单位的信息。
   * @property code 地区代码对象。
   * @property name 地区名称。
   * @property yearVersion 数据对应的年份版本。
   * @property level 地区层级 (1:省, 2:市, 3:县, 4:乡镇, 5:村)。
   * @property leaf 是否为叶子节点（通常指村级或无法再下钻的级别）。
   */
  data class CnDistrict(
    val code: CnDistrictCode,
    val name: String,
    val yearVersion: String,
    val level: Int = code.level,
    val leaf: Boolean = level >= 5,
  )

  // --- 服务属性 ---
  /** 提供的日志记录器 (可选) */
  val logger: SysLogger?
    get() = null

  /**
   * 所有支持的年份版本，例如：
   * - `["2023", "2024", "2018"]`
   */
  val supportedYearVersions: List<String>

  /** 默认使用的年份版本 */
  val supportedDefaultYearVersion: String

  /** 支持的最大行政层级，默认为 5 (村级) */
  val supportedMaxLevel: Int
    get() = 5

  /** 获取当前年份版本之前一个年份版本 基于 [supportedYearVersions] */
  fun lastYearVersionOrNull(yearVersion: String): String? {
    if (yearVersion.nonText()) return null
    val currentYearVersion = yearVersion.toIntOrNull() ?: return null
    // 查找小于当前年份且最接近的版本
    return supportedYearVersions.mapNotNull { it.toIntOrNull() }.distinct().sortedDescending().filter { it < currentYearVersion }.maxOrNull()?.toString()
  }

  /** 最新的支持的年份版本 */
  val lastYearVersion: String
    get() = supportedYearVersions.maxOrNull() ?: supportedDefaultYearVersion

  /**
   * 获取指定代码表示的行政区划的 **直接子级** 列表。
   * 例如，给定省代码，返回该省下的所有市；给定国家代码 "0"，返回所有省。
   * 实现类需要处理 `parentCode` 无效或找不到的情况，并负责根据 `yearVersion` 查找数据，**无需** 自动版本回退。
   *
   * @param parentCode 父级行政区划代码 (例如：省代码 "110000000000", 国家代码 "0")。
   * @param yearVersion 需要查找的数据年份版本。
   * @return 直接子级 [CnDistrict] 列表，如果父代码无效、无子级或指定年份无数据则返回空列表。
   */
  fun fetchChildren(
    parentCode: string,
    yearVersion: String,
  ): List<CnDistrict>

  /**
   * 获取所有省份列表（国家 "0" 的直接子级）。
   * 实现类应调用 `findChildren(DEFAULT_COUNTRY_CODE, yearVersion)`。
   *
   * @param yearVersion 数据年份版本。
   * @return 省份 [CnDistrict] 列表。
   */
  fun fetchAllProvinces(
    yearVersion: String = supportedDefaultYearVersion,
  ): List<CnDistrict> = fetchChildren(DEFAULT_COUNTRY_CODE, yearVersion)

  /**
   * 查找指定代码对应的单个行政区划信息。
   * 实现类需要处理 `code` 无效或找不到的情况。
   * 实现类 **需要** 处理版本回退逻辑：如果 `yearVersion` 找不到，则尝试使用 `lastYearVersionOrNull` 获取更早版本进行查找，直到找到或所有版本都尝试过。
   *
   * @param code 需要查找的行政区划代码（可以是任意层级的部分或完整代码）。
   * @param yearVersion **起始** 查找的数据年份版本 (若希望总是从最新开始，传入 `lastYearVersion`)。
   * @return 找到的 [CnDistrict] 信息 (应包含实际找到数据的年份版本)，如果所有支持年份版本中都找不到则返回 null。
   */
  fun fetchDistrict(
    code: string,
    yearVersion: String,
  ): CnDistrict?

  /**
   * 递归查找指定代码下的 **所有子孙** 行政区划列表，直到指定的最大深度。
   * 实现类需要处理 `parentCode` 无效或找不到的情况，并处理 `maxDepth`。
   * 实现类 **需要** 处理版本回退逻辑：对于查找的每一层级，如果在一个版本中找不到子节点，应尝试更早的版本。
   * （注意：版本回退逻辑可能比较复杂，例如，父节点在 V2 找到，子节点在 V1 找到）。
   *
   * @param parentCode 父级行政区划代码。
   * @param maxDepth 相对于 `parentCode` 的最大查找深度（例如 `maxDepth = 1` 表示只查找直接子级，等同于 `findChildren`）。
   * @param yearVersion **起始** 查找的数据年份版本 (每一层递归都应从这个版本开始尝试)。
   * @return 所有符合条件的子孙 [CnDistrict] 列表 (每个 District 应包含实际找到数据的年份版本)。
   */
  fun fetchChildrenRecursive(
    parentCode: string,
    maxDepth: Int,
    yearVersion: String,
  ): List<CnDistrict>

  /**
   * 以遍历的方式递归处理指定代码下的所有子孙行政区划。
   * 该方法通过回调函数让调用者能够控制遍历过程，适合大数据量的数据库操作。
   * 
   * @param parentCode 父级行政区划代码
   * @param maxDepth 相对于 `parentCode` 的最大遍历深度
   * @param yearVersion **起始** 遍历的数据年份版本
   * @param onVisit 访问节点的回调函数，返回 true 继续遍历，返回 false 停止当前分支的遍历
   *                参数说明：
   *                - district: 当前访问的节点信息
   *                - depth: 当前节点相对于 parentCode 的深度（从1开始）
   *                - parentDistrict: 父节点信息（如果是顶级节点则为null）
   */
  fun traverseChildrenRecursive(
    parentCode: string,
    maxDepth: Int,
    yearVersion: String,
    onVisit: (district: CnDistrict, depth: Int, parentDistrict: CnDistrict?) -> Boolean
  )
}
