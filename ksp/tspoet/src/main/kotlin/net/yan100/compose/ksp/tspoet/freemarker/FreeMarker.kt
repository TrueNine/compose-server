package net.yan100.compose.ksp.tspoet.freemarker

import freemarker.template.Configuration
import freemarker.template.Template

object FreeMarker {
  private var fm: Configuration = Configuration(Configuration.VERSION_2_3_33)

  init {
    fm.defaultEncoding = "UTF-8"
    fm.templateLoader = ClassResourceTemplateLoader("")
  }

  operator fun get(templateName: String): Template? {
    return fm.getTemplate(templateName)
  }
}
