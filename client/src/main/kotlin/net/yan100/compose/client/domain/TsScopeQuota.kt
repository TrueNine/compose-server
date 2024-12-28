package net.yan100.compose.client.domain

/**
 * 作用域符号枚举
 */
enum class TsScopeQuota(
  val left: String,
  val right: String
) {
  OBJECT("{", "}"),
  ARRAY("[", "]"),
  BRACKETS("(", ")"),
  BLANK("", "");
}
