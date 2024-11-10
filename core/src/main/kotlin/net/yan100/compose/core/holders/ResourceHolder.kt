package net.yan100.compose.core.holders

import net.yan100.compose.core.hasText
import net.yan100.compose.core.properties.DataLoadProperties
import net.yan100.compose.core.slf4j
import net.yan100.compose.core.systemSeparator
import org.springframework.boot.system.ApplicationHome
import org.springframework.core.io.FileSystemResourceLoader
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.io.File

private val log = slf4j<ResourceHolder>()

class ResourceHolder(
  private val home: ApplicationHome, private val resourceLoader: ResourceLoader, private val p: DataLoadProperties
) {
  init {
    log.info("ResourceHolder resource loader user.dir: {}", System.getProperty("user.dir"))
  }

  @Deprecated(message = "暂不使用")
  private val mainResourceDir: File?
    get() = try {
      ResourceHolder::class.java.classLoader.getResource("location.location")?.toURI()?.let { File(it).parentFile }
    } catch (e: Exception) {
      null
    }

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
    return matchConfigResources(path).firstOrNull()
  }

  fun matchConfigResources(pattern: String): List<Resource> {
    if (!pattern.hasText()) return emptyList()
    val configDir = prodConfigDir.replace("\\", "/")
    val classpathDir = internalConfigDir.replace("\\", "/")

    val resolver = PathMatchingResourcePatternResolver(FileSystemResourceLoader())
    val resultMap = mutableMapOf<String, Resource>()

    val rootUrl = resolver.getResource(classpathDir).url
    resolver.getResources("$classpathDir/$pattern").filter { it.exists() }.forEach {
      val name = it.url.toString().removePrefix(rootUrl.toString()).removePrefix("/")
      resultMap[name] = it
    }

    val externalConfigDirAbsolutePath = resolver.getResource("file:$configDir").file.absolutePath
    resolver.getResources("file:$externalConfigDirAbsolutePath/$pattern").filter { it.exists() }.forEach {
      val name = it.file.absolutePath.removePrefix(externalConfigDirAbsolutePath.toString()).replace("\\", "/").removePrefix("/")
      resultMap[name] = it
    }
    return resultMap.values.filter { it.exists() }.toList()
  }
}
