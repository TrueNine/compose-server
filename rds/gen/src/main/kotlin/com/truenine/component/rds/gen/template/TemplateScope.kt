package com.truenine.component.rds.gen.template

import freemarker.template.Configuration
import io.tn.core.lang.ResourcesLocator
import com.truenine.component.rds.gen.ctx.DefCtx
import java.io.File
import java.io.FileWriter

object TemplateScope {
  private val config =
    Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS)

  init {
    config.setDirectoryForTemplateLoading(
      File(
        ResourcesLocator.classpathUrl("template").toURI()
      )
    )
  }

  fun scoped(
      pkgPath: String,
      genFileName: String,
      ftlName: String,
      ctx: DefCtx,
      tab: Any
  ) {
    val genBasePath =
      "${ResourcesLocator.getGenerateDirPath()}/${ctx.getLang()}/$pkgPath"
    val dest = File(genBasePath, genFileName)

    if (dest.exists()) {
      dest.delete()
    }

    dest.parentFile.mkdirs()
    dest.createNewFile()
    val writer = FileWriter(dest)

    val map = mutableMapOf<String, Any>()
    map["ctx"] = ctx
    map["tab"] = tab
    val template = config.getTemplate("${ftlName}.ftl")
    template.process(map, writer)
  }
}
