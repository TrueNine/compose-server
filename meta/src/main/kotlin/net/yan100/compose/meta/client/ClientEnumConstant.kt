package net.yan100.compose.meta.client

/**
 * 枚举常量
 */
data class ClientEnumConstant(
  /**
   * 枚举名称
   */
  val name: String,
  /**
   * 枚举序号
   */
  val ordinal: Int? = null,
  /**
   * 枚举常量值类型
   */
  val enumKind: String? = null,
  /**
   * 枚举常量值
   */
  val enumValue: String? = null
)
