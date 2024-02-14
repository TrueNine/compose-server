package net.yan100.compose.rds.gen.util


import freemarker.template.Configuration
import net.yan100.compose.rds.gen.ctx.RenderContext
import java.io.File
import java.io.FileWriter

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
        val genBasePath = "${net.yan100.compose.core.lang.ResourcesLocator.getGenerateDirPath()}/${renderContext.getLang()}/$packagePath"
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
