package net.yan100.compose.testtoolkit.autoconfig

import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import net.yan100.compose.testtoolkit.log
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
          assertTrue(properties.enabled, "enabled 应该为 true")
          assertTrue(!properties.disableConditionEvaluationReport, "disableConditionEvaluationReport 应该为 false")
          assertTrue(!properties.enableVirtualThreads, "enableVirtualThreads 应该为 false")
          assertTrue(properties.ansiOutputMode == AnsiOutputMode.DETECT, "ansiOutputMode 应该为 DETECT")
          assertTrue(properties.additionalProperties.containsKey("custom.key"), "应该包含自定义属性")
        }

      log.debug("configuration properties binding verified")
    }
  }
}
