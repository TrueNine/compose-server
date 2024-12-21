package net.yan100.compose.ksp.tspoet.freemarker

import freemarker.cache.TemplateLoader
import java.io.Closeable
import java.io.InputStream
import java.io.Reader
import java.util.*

class ClassResourceTemplateLoader(private val baseFolder: String = "") : TemplateLoader {
  private val loader = ClassResourceTemplateLoader::class.java.classLoader
  override fun findTemplateSource(name: String): Any {
    val default = Locale.getDefault()
    val n = name.replace("_${default.language}_${default.country}", "")
    val folder = baseFolder.ifBlank { "" }.let {
      if (it.isBlank()) "" else "$it/"
    }
    val path = "${folder}${n}.ftl"
    return loader.getResourceAsStream(path)!!
  }

  override fun getLastModified(templateSource: Any?): Long {
    return 0
  }

  override fun getReader(templateSource: Any?, encoding: String?): Reader {
    if (null == templateSource) error("未找到资源")
    val resource = templateSource as InputStream
    return resource.reader(Charsets.UTF_8)
  }

  override fun closeTemplateSource(templateSource: Any?) {
    val c = (templateSource as Closeable)
    c.close()
  }
}
