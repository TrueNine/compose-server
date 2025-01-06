package net.yan100.compose.client.domain

import net.yan100.compose.client.TsSymbol

/**
 * 作用域符号枚举
 */
enum class TsScopeQuota(
  val left: String,
  val right: String
) : TsSymbol {
  ARROW("=>", ""),
  ASSIGN("=", ""),
  ASSIGNMENT(":", ""),
  OBJECT("{", "}"),
  ASSIGN_OBJECT("= {", "}"),
  ARRAY("[", "]"),
  BRACKETS("(", ")"),
  BLANK("", "");
}
