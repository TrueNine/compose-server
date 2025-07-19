package io.github.truenine.composeserver.testtoolkit.autoconfig

import io.github.truenine.composeserver.testtoolkit.log
import io.github.truenine.composeserver.testtoolkit.properties.AnsiOutputMode
import io.github.truenine.composeserver.testtoolkit.properties.TestConfigurationProperties
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner

class AutoConfigurationTest {

  private val contextRunner = ApplicationContextRunner().withConfiguration(AutoConfigurations.of(TestConfigurationBean::class.java))

  @Nested
  inner class AutoConfiguration {

    @Test
    fun `默认配置 - 应当自动配置 TestConfigurationBean`() {
      log.trace("testing auto configuration with default settings")

      contextRunner.withPropertyValues("compose.testtoolkit.enabled=true").run { context ->
        assertNotNull(context.getBean(TestConfigurationBean::class.java), "应该自动配置 TestConfigurationBean")
        assertNotNull(context.getBean(TestEnvironmentPostProcessor::class.java), "应该自动配置 TestEnvironmentPostProcessor")
      }

      log.debug("auto configuration verified")
    }

    @Test
    fun `禁用配置 - 不应当自动配置 TestConfigurationBean`() {
      log.trace("testing auto configuration when disabled")

      contextRunner.withPropertyValues("compose.testtoolkit.enabled=false").run { context ->
        assertTrue(!context.containsBean("testConfigurationBean"), "禁用时不应该配置 TestConfigurationBean")
        assertTrue(!context.containsBean("testEnvironmentPostProcessor"), "禁用时不应该配置 TestEnvironmentPostProcessor")

        // 验证禁用时相关属性确实未被设置
        assertNotNull(context.environment, "环境对象不应为 null")
        assertTrue(
          context.environment.getProperty("spring.threads.virtual.enabled") == null ||
            context.environment.getProperty("spring.threads.virtual.enabled") == "false",
          "禁用时虚拟线程属性不应设置为 true",
        )
      }

      log.debug("disabled auto configuration verified")
    }

    @Test
    fun `自定义属性 - 应当正确绑定配置属性`() {
      log.trace("testing configuration properties binding")

      contextRunner
        .withPropertyValues(
          "compose.testtoolkit.enabled=true",
          "compose.testtoolkit.disable-condition-evaluation-report=false",
          "compose.testtoolkit.enable-virtual-threads=false",
          "compose.testtoolkit.ansi-output-mode=detect",
          "compose.testtoolkit.additional-properties.custom.key=customValue",
        )
        .run { context ->
          val properties = context.getBean(TestConfigurationProperties::class.java)

          // 验证属性对象本身
          assertNotNull(properties, "配置属性对象不应为 null")

          // 验证所有配置属性的完整性
          assertTrue(properties.enabled, "enabled 应该为 true")
          assertTrue(!properties.disableConditionEvaluationReport, "disableConditionEvaluationReport 应详为 false")
          assertTrue(!properties.enableVirtualThreads, "enableVirtualThreads 应详为 false")
          assertTrue(properties.ansiOutputMode == AnsiOutputMode.DETECT, "ansiOutputMode 应详为 DETECT")

          // 验证额外属性的完整性
          assertNotNull(properties.additionalProperties, "额外属性映射不应为 null")
          assertTrue(properties.additionalProperties.containsKey("custom.key"), "应详包含自定义属性")
          assertTrue(properties.additionalProperties["custom.key"] == "customValue", "自定义属性值应详正确")
          assertTrue(properties.additionalProperties.size >= 1, "额外属性大小应详大于等于 1")

          // 验证枚举值的有效性
          assertTrue(properties.ansiOutputMode in AnsiOutputMode.values(), "ANSI 输出模式应详为有效枚举值")
        }

      log.debug("configuration properties binding verified")
    }
  }
}
