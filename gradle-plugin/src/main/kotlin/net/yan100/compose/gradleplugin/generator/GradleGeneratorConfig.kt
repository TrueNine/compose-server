package net.yan100.compose.gradleplugin.generator

import java.util.*
import org.gradle.api.Action

/**
 * # gradle.properties 生成配置
 *
 * @author TrueNine
 * @since 2024-03-08
 */
open class GradleGeneratorConfig {

  var enabled: Boolean = false

  val initGradle = InitGradleConfig()

  fun initGradle(action: Action<InitGradleConfig>) = action.execute(initGradle)

  inner class InitGradleConfig {
    var wrapperUrl: String = "https://services.gradle.org/distributions"
      private set

    var wrapperVersion: String = "8.5"
      private set

    var mavenType: MavenRepoType = MavenRepoType.DEFAULT
      private set

    var otherRepositories: MutableList<String> = mutableListOf()
      private set

    fun wrapperUrl(url: String) {
      wrapperUrl = url
    }

    fun wrapperVersion(version: String) {
      wrapperVersion = version
    }

    fun otherRepositories(vararg repos: String) {
      otherRepositories += repos
    }

    fun mavenType(type: String) {
      mavenType =
        when (type) {
          "ali" -> MavenRepoType.ALIYUN
          "tencent" -> MavenRepoType.TENCENT_CLOUD
          "huawei" -> MavenRepoType.HUAWEI_CLOUD
          else -> MavenRepoType.DEFAULT
        }
    }

    fun mavenType(type: MavenRepoType) {
      mavenType = type
    }

    var enableSpring: Boolean = true
    var enableMybatisPlus: Boolean = true
    var testNetworkSpeed: Boolean = true
  }

  private val propertiesMap =
    mutableMapOf(
      "org.gradle.daemon" to "true",
      "org.gradle.parallel" to "true",
      "org.gradle.caching" to "false",
      "org.gradle.jvmargs" to "-Xmx8192m -Xms4096m",
      "org.gradle.workers.max" to
        Runtime.getRuntime().availableProcessors().toString(),
    )

  fun workers(value: Int) {
    check(value in 0..1024) {
      "The number of counties you set up is too large, be careful that the computer is stuck"
    }
    propertiesMap["org.gradle.workers.max"] = value.toString()
  }

  /** ## 添加其他选项 */
  fun otherOption(optionName: String, value: String) =
    propertiesMap.put(optionName, value)

  fun toPropertiesString(): String {
    return buildString {
      asProperties().forEach {
        append(it.key)
        append("=")
        append(it.value)
        appendLine()
      }
    }
  }

  /** ## 转换为 properties */
  private fun asProperties(): Properties {
    val p = Properties()
    p.putAll(propertiesMap)
    return p
  }

  /** ## 设置 jvm args */
  fun jvmArgs(vararg value: String) {
    propertiesMap["org.gradle.jvmargs"] =
      value.joinToString(separator = " ") { it.trim() }
  }

  /** ## 是否开启 gradle cache */
  fun caching(value: Boolean) {
    propertiesMap["org.gradle.caching"] = value.toString()
  }

  /** ## 开启 gradle parallel */
  fun parallel(value: Boolean) {
    propertiesMap["org.gradle.parallel"] = value.toString()
  }

  /** ## 是否开启 gradle daemon */
  fun daemon(value: Boolean) {
    propertiesMap["org.gradle.daemon"] = value.toString()
  }

  companion object {
    const val GRADLE_PROPERTIES_NAME = "gradle.properties"
  }
}
