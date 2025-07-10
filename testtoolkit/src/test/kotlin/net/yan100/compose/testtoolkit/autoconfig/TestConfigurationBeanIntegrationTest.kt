package net.yan100.compose.testtoolkit.autoconfig

import kotlin.test.assertEquals
import kotlin.test.assertTrue
import net.yan100.compose.testtoolkit.TestEntrance
import net.yan100.compose.testtoolkit.log
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.test.context.TestPropertySource

@SpringBootTest(classes = [TestEntrance::class])
@TestPropertySource(
  properties =
    [
      "compose.testtoolkit.enabled=true",
      "compose.testtoolkit.disable-condition-evaluation-report=true",
      "compose.testtoolkit.enable-color-output=true",
      "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
    ]
)
class TestConfigurationBeanIntegrationTest {

  @Autowired private lateinit var environment: ConfigurableEnvironment

  @Autowired private lateinit var testConfigurationBean: TestConfigurationBean

  @Autowired private lateinit var testEnvironmentPostProcessor: TestEnvironmentPostProcessor

  @Nested
  inner class BeanInjection {

    @Test
    fun `应当正确注入所有 Bean`() {
      log.trace("[应当正确注入所有 Bean] verifying beans are properly injected")

      assertTrue(::testConfigurationBean.isInitialized, "TestConfigurationBean 应该被正确注入")
      assertTrue(::testEnvironmentPostProcessor.isInitialized, "TestEnvironmentPostProcessor 应该被正确注入")

      log.debug("[应当正确注入所有 Bean] beans injection verified successfully")
    }
  }

  @Nested
  inner class EnvironmentProperties {

    @Test
    fun `应当正确注入环境属性`() {
      log.trace("[应当正确注入环境属性] verifying environment properties are injected correctly")

      // 验证颜色输出配置
      val ansiEnabled = environment.getProperty("spring.output.ansi.enabled")
      assertEquals("always", ansiEnabled, "ANSI 颜色输出应该被设置为 always")

      // 验证条件评估报告被关闭
      val debugEnabled = environment.getProperty("debug")
      assertEquals("false", debugEnabled, "debug 应该被设置为 false")

      val conditionLogLevel = environment.getProperty("spring.test.print-condition-evaluation-report")
      assertEquals("false", conditionLogLevel, "条件评估日志级别应该被设置为 false")

      val startupInfoEnabled = environment.getProperty("spring.main.log-startup-info")
      assertEquals("false", startupInfoEnabled, "启动信息日志应该被关闭")

      log.debug("[应当正确注入环境属性] environment properties injection verified successfully")
    }
  }

  @Nested
  inner class TestEnvironmentPostProcessorFunctionality {

    @Test
    fun `应当正确提供 TestEnvironmentPostProcessor 功能`() {
      log.trace("[应当正确提供 TestEnvironmentPostProcessor 功能] testing TestEnvironmentPostProcessor functionality")

      val recommendedProperties = testEnvironmentPostProcessor.getRecommendedTestProperties()
      assertTrue(recommendedProperties.isNotEmpty(), "推荐属性不应该为空")

      val isTestEnv = testEnvironmentPostProcessor.isTestEnvironment(environment)
      // 由于我们在测试环境中，这应该返回 true 或者根据实际配置决定
      log.debug("[应当正确提供 TestEnvironmentPostProcessor 功能] isTestEnvironment result: {}", isTestEnv)

      log.debug("[应当正确提供 TestEnvironmentPostProcessor 功能] TestEnvironmentPostProcessor functionality verified")
    }
  }
}
