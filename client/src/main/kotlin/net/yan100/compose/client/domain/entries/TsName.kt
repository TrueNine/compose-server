package net.yan100.compose.client.domain.entries

sealed class TsName {
  /**
   * 匿名
   */
  data object Anonymous : TsName()

  /**
   * 被转换的名称
   */
  data class As(
    val name: String,
    val asName: String
  ) : TsName()

  /**
   * 普通命名
   */
  data class Name(
    val name: String
  ) : TsName() {
    override fun toString(): String = name
  }

  /**
   * 泛型名称
   */
  data class Generic(
    val name: String
  ) : TsName() {
    override fun toString(): String = name
  }

  /**
   * 一般用作文件路径
   */
  data class PathName(
    val name: String,
    val path: String = ""
  ) : TsName() {
    override fun toString(): String = if (path.isBlank()) name else "$path/$name"
  }
}
