package net.yan100.compose.client.templates

import net.yan100.compose.client.domain.TypescriptEnum


object EnumTemplate {
  fun renderEnum(e: TypescriptEnum): String {
    return buildString {
      if (e.isExport) append("export ")
      append("enum")
      appendLine(" ${e.name} {")
      val constants = e.constants.map { (k, v) ->
        """  $k = ${if (e.isString || v is String) "\"$v\"" else v}"""
      }.joinToString(",\n")
      appendLine(constants)
      append("}")
      appendLine()
    }
  }
}
