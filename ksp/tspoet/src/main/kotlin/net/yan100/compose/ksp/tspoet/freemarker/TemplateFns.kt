package net.yan100.compose.ksp.tspoet.freemarker

import freemarker.template.Template
import java.io.StringWriter

fun Template.renderString(map: Map<String, Any>): String {
  return StringWriter().use {
    process(map, it)
    it.toString()
  }
}
