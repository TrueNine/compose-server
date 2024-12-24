package net.yan100.compose.client.domain.entries

sealed class TypescriptName {
  /**
   * 匿名
   */
  data object Anonymous : TypescriptName()

  /**
   * 被转换的名称
   */
  data class As(
    val name: String,
    val asName: String
  ) : TypescriptName()

  /**
   * 普通命名
   */
  data class Name(
    val name: String
  ) : TypescriptName()
}
