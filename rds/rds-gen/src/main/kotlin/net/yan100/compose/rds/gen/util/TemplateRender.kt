/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.rds.gen.util

import freemarker.template.Configuration
import java.io.File
import java.io.FileWriter
import net.yan100.compose.rds.gen.ctx.RenderContext

object TemplateRender {
  private val config = Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS)

  init {
    config.setDirectoryForTemplateLoading(
      File(javaClass.classLoader.getResource("template")!!.toURI())
    )
  }

  fun render(
    packagePath: String,
    generatedFileName: String,
    templateFileNamePrefix: String,
    renderContext: RenderContext,
    tableContext: Any
  ) {
    val genBasePath =
      "${net.yan100.compose.core.lang.ResourcesLocator.getGenerateDirPath()}/${renderContext.getLang()}/$packagePath"
    val destGenerateFile = File(genBasePath, generatedFileName)

    if (destGenerateFile.exists()) {
      destGenerateFile.delete()
    }

    destGenerateFile.parentFile.mkdirs()
    destGenerateFile.createNewFile()

    val writer = FileWriter(destGenerateFile)

    val map = mutableMapOf<String, Any>()
    map["ctx"] = renderContext
    map["tab"] = tableContext
    val template = config.getTemplate("${templateFileNamePrefix}.ftl")
    template.process(map, writer)
  }
}
