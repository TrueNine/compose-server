package net.yan100.compose.core.holders

import net.yan100.compose.core.hasText
import net.yan100.compose.core.properties.DataLoadProperties
import net.yan100.compose.core.systemSeparator
import org.springframework.boot.system.ApplicationHome
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import java.io.File

class ResourceHolder(
  private val home: ApplicationHome,
  private val resourceLoader: ResourceLoader,
  private val p: DataLoadProperties
) {
  private val prodPath: String?
    get() = home.source?.absolutePath
  private val prodDir: String
    get() = home.dir.absolutePath
  private val prodConfigDir: String
    get() = listOf(prodDir, p.configLocation, p.location).filter { it.hasText() }.joinToString(systemSeparator)

  private val internalConfigDir: String
    get() = listOf("classpath:${p.configLocation}", p.location).filter { it.hasText() }.joinToString(systemSeparator)

  private val isProd: Boolean
    get() = home.source == null

  fun getConfigResource(path: String): Resource? {
    val externalCfgPath = listOf(prodConfigDir, path).joinToString(systemSeparator)
    val classpathCfgPath = listOf(internalConfigDir, path).joinToString(systemSeparator)
    val classpathPath = "classpath:$path"

    val externalFile = File(externalCfgPath)

    val classpathCfg = resourceLoader.getResource(classpathCfgPath)
    val classpath = resourceLoader.getResource(classpathPath) as Resource?
    return if (externalFile.exists()) {
      resourceLoader.getResource(externalCfgPath)
    } else if (classpathCfg.exists()) {
      classpathCfg
    } else classpath
  }
}
