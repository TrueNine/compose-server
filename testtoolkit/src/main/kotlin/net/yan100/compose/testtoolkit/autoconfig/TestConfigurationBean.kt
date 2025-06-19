package net.yan100.compose.testtoolkit.autoconfig

import jakarta.annotation.PostConstruct
import net.yan100.compose.testtoolkit.SysLogger
import org.slf4j.LoggerFactory
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource

/**
 * # 测试工具包配置 Bean
 *
 * 提供测试期间的默认配置：
 * - 默认为 Spring 上下文注入属性
 * - 关闭条件评估报告
 * - 总是开启颜色输出
 */
@AutoConfiguration
@EnableConfigurationProperties(TestConfigurationProperties::class)
@ConditionalOnProperty(name = ["compose.testtoolkit.enabled"], havingValue = "true", matchIfMissing = true)
open class TestConfigurationBean(
  private val environment: ConfigurableEnvironment,
  private val properties: TestConfigurationProperties
) {

  private val log: SysLogger = LoggerFactory.getLogger(TestConfigurationBean::class.java)

  @PostConstruct
  fun configureTestProperties() {
    if (!properties.enabled) {
      log.debug("test toolkit disabled")
      return
    }

    log.trace("configuring test properties")

    val testProperties = mutableMapOf<String, Any>()

    // 关闭条件评估报告
    if (properties.disableConditionEvaluationReport) {
      testProperties["debug"] = false
      testProperties["spring.test.print-condition-evaluation-report"] = false
      testProperties["spring.main.log-startup-info"] = false
      testProperties["spring.main.banner-mode"] = Banner.Mode.OFF
      log.trace("disabled condition evaluation report")
    }

    // 启用虚拟线程
    if (properties.enableVirtualThreads) {
      testProperties["spring.threads.virtual.enabled"] = true
      log.trace("enabled virtual threads")
    }

    // 配置颜色输出
    testProperties["spring.output.ansi.enabled"] = properties.ansiOutputMode.value
    log.trace("set ansi output mode: {}", properties.ansiOutputMode.value)

    // 添加额外的测试属性
    testProperties.putAll(properties.additionalProperties)

    // 将属性添加到环境中
    if (testProperties.isNotEmpty()) {
      val propertySource = MapPropertySource("testToolkitProperties", testProperties)
      environment.propertySources.addFirst(propertySource)
      log.debug("added {} test properties", testProperties.size)
    }
  }

  @Bean
  @ConditionalOnMissingBean
  open fun testEnvironmentPostProcessor(): TestEnvironmentPostProcessor {
    log.trace("creating test environment post processor")
    return TestEnvironmentPostProcessor()
  }


}

/**
 * # 测试工具包配置属性
 */
@ConfigurationProperties(prefix = "compose.testtoolkit")
data class TestConfigurationProperties(
  /**
   * 是否启用测试配置
   */
  val enabled: Boolean = true,

  /**
   * 是否关闭条件评估报告
   */
  val disableConditionEvaluationReport: Boolean = true,

  /**
   * 是否启用虚拟线程
   */
  val enableVirtualThreads: Boolean = true,

  /**
   * ANSI 颜色输出模式
   */
  val ansiOutputMode: AnsiOutputMode = AnsiOutputMode.ALWAYS,

  /**
   * 额外的测试属性
   */
  val additionalProperties: Map<String, String> = emptyMap()
)

/**
 * # 测试环境后处理器
 *
 * 用于在测试环境中进行额外的配置处理
 */
class TestEnvironmentPostProcessor {

  /**
   * 获取推荐的测试属性
   */
  fun getRecommendedTestProperties(): Map<String, String> {
    return mapOf(
      "spring.threads.virtual.enabled" to "true",
      "spring.output.ansi.enabled" to "always",
      "spring.jpa.show-sql" to "false",
      "spring.jpa.hibernate.ddl-auto" to "create-drop",
      "spring.datasource.initialization-mode" to "never",
      "spring.test.database.replace" to "none",
      "logging.level.org.springframework.web" to "DEBUG",
      "logging.level.org.springframework.security" to "DEBUG"
    )
  }

  /**
   * 检查是否为测试环境
   */
  fun isTestEnvironment(environment: ConfigurableEnvironment): Boolean {
    val activeProfiles = environment.activeProfiles
    return activeProfiles.any { it.contains("test") } ||
      environment.getProperty("spring.profiles.active", "").contains("test")
  }
} 
