package io.github.truenine.composeserver.testtoolkit.autoconfig

import io.github.truenine.composeserver.testtoolkit.log
import io.github.truenine.composeserver.testtoolkit.properties.AnsiOutputMode
import io.github.truenine.composeserver.testtoolkit.properties.TestConfigurationProperties
import io.github.truenine.composeserver.testtoolkit.properties.TestcontainersProperties
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.MutablePropertySources

class TestConfigurationBeanTest {

  private lateinit var testConfigurationBean: TestConfigurationBean
  private lateinit var mockEnvironment: ConfigurableEnvironment
  private lateinit var mockPropertySources: MutablePropertySources
  private lateinit var properties: TestConfigurationProperties
  private lateinit var testcontainersProperties: TestcontainersProperties

  @BeforeEach
  fun setUp() {
    log.trace("initializing test configuration bean test")

    mockPropertySources = mockk<MutablePropertySources>(relaxed = true)
    mockEnvironment = mockk<ConfigurableEnvironment> { every { propertySources } returns mockPropertySources }

    properties = TestConfigurationProperties()
    testcontainersProperties = TestcontainersProperties()
    testConfigurationBean = TestConfigurationBean(mockEnvironment, properties, testcontainersProperties)
  }

  @Nested
  inner class ConfigureTestProperties {

    @Test
    fun `启用状态 - 应当注入测试属性`() {
      log.trace("testing property injection when enabled")

      // 执行配置
      testConfigurationBean.configureTestProperties()

      // 验证属性源被添加
      verify { mockPropertySources.addFirst(any<MapPropertySource>()) }

      // 验证Bean状态
      assertNotNull(testConfigurationBean, "TestConfigurationBean 实例不应为 null")
      assertTrue(properties.enabled, "配置属性应该为启用状态")

      log.debug("property injection verified when enabled")
    }

    @Test
    fun `禁用状态 - 不应当注入测试属性`() {
      log.trace("testing property injection when disabled")

      // 设置为禁用状态
      properties = TestConfigurationProperties(enabled = false)
      testConfigurationBean = TestConfigurationBean(mockEnvironment, properties, testcontainersProperties)

      // 执行配置
      testConfigurationBean.configureTestProperties()

      // 验证属性源未被添加
      verify(exactly = 0) { mockPropertySources.addFirst(any<MapPropertySource>()) }

      // 验证禁用状态未改变其他配置
      assertFalse(properties.enabled, "禁用时配置属性应该为禁用状态")
      assertNotNull(testConfigurationBean, "TestConfigurationBean 实例不应为 null")

      log.debug("property injection verified when disabled")
    }

    @Test
    fun `关闭条件评估报告 - 应当设置相关属性`() {
      log.trace("testing condition evaluation report disable configuration")

      properties = TestConfigurationProperties(disableConditionEvaluationReport = true, enableVirtualThreads = false, ansiOutputMode = AnsiOutputMode.NEVER)
      testConfigurationBean = TestConfigurationBean(mockEnvironment, properties, testcontainersProperties)

      // 捕获添加的属性源
      var capturedPropertySource: MapPropertySource? = null
      every { mockPropertySources.addFirst(any<MapPropertySource>()) } answers { capturedPropertySource = firstArg() }

      testConfigurationBean.configureTestProperties()

      // 验证条件评估报告相关属性
      assertNotNull(capturedPropertySource, "属性源不应为 null")
      capturedPropertySource?.also { propertySource ->
        assertNotNull(propertySource.name, "属性源名称不应为 null")
        assertTrue(propertySource.name.contains("test"), "属性源名称应包含 'test'")
        assertEquals(false, propertySource.getProperty("debug"), "debug 应该被设置为 false")
        assertEquals(false, propertySource.getProperty("spring.test.print-condition-evaluation-report"), "条件评估报告应该被禁用")
        assertEquals(AnsiOutputMode.NEVER.name.lowercase(), propertySource.getProperty("spring.output.ansi.enabled"), "ANSI 输出应该为 never")
        // 当 enableVirtualThreads = false 时，不会设置虚拟线程属性（保持默认）
        assertTrue(propertySource.getProperty("spring.threads.virtual.enabled") == null, "虚拟线程属性不应被设置（保持默认）")
      }

      log.debug("condition evaluation report disable configuration verified")
    }

    @Test
    fun `虚拟线程和颜色输出 - 应当设置相关属性`() {
      log.trace("testing virtual threads and color output configuration")

      properties = TestConfigurationProperties(disableConditionEvaluationReport = false, enableVirtualThreads = true, ansiOutputMode = AnsiOutputMode.ALWAYS)
      testConfigurationBean = TestConfigurationBean(mockEnvironment, properties, testcontainersProperties)

      // 捕获添加的属性源
      var capturedPropertySource: MapPropertySource? = null
      every { mockPropertySources.addFirst(any<MapPropertySource>()) } answers { capturedPropertySource = firstArg() }

      testConfigurationBean.configureTestProperties()

      // 验证虚拟线程和颜色输出相关属性
      assertNotNull(capturedPropertySource, "属性源不应为 null")
      capturedPropertySource?.let { propertySource ->
        assertNotNull(propertySource.source, "属性源的数据不应为 null")
        assertTrue(propertySource.source is Map<*, *>, "属性源数据应该为 Map 类型")
        assertEquals(true, propertySource.getProperty("spring.threads.virtual.enabled"), "虚拟线程应该被启用")
        assertEquals("always", propertySource.getProperty("spring.output.ansi.enabled"), "ANSI 颜色输出应该被设置为 always")
        // 当 disableConditionEvaluationReport = false 时，不会设置 debug 和条件评估报告属性
        assertTrue(propertySource.getProperty("debug") == null, "debug 属性不应被设置（保持默认）")
        assertTrue(propertySource.getProperty("spring.test.print-condition-evaluation-report") == null, "条件评估报告属性不应被设置（保持默认）")
      }

      log.debug("virtual threads and color output configuration verified")
    }

    @Test
    fun `额外属性配置 - 应当正确注入自定义属性`() {
      log.trace("testing additional properties configuration")

      val additionalProps = mapOf("custom.property.1" to "value1", "custom.property.2" to "value2")

      properties =
        TestConfigurationProperties(
          disableConditionEvaluationReport = false,
          enableVirtualThreads = false,
          ansiOutputMode = AnsiOutputMode.DETECT,
          additionalProperties = additionalProps,
        )
      testConfigurationBean = TestConfigurationBean(mockEnvironment, properties, testcontainersProperties)

      // 捕获添加的属性源
      var capturedPropertySource: MapPropertySource? = null
      every { mockPropertySources.addFirst(any<MapPropertySource>()) } answers { capturedPropertySource = firstArg() }

      testConfigurationBean.configureTestProperties()

      // 验证额外属性
      assertNotNull(capturedPropertySource, "属性源不应为 null")
      capturedPropertySource?.let { propertySource ->
        assertEquals("value1", propertySource.getProperty("custom.property.1"), "自定义属性 1 应该被正确设置")
        assertEquals("value2", propertySource.getProperty("custom.property.2"), "自定义属性 2 应该被正确设置")
        assertEquals("detect", propertySource.getProperty("spring.output.ansi.enabled"), "ANSI 输出应该为 detect")
        // 当 enableVirtualThreads = false 时，不会设置虚拟线程属性（保持默认）
        assertTrue(propertySource.getProperty("spring.threads.virtual.enabled") == null, "虚拟线程属性不应被设置（保持默认）")

        // 验证所有额外属性都被正确添加
        additionalProps.forEach { (key, value) -> assertEquals(value, propertySource.getProperty(key), "额外属性 $key 应该被正确设置") }
      }

      log.debug("additional properties configuration verified")
    }
  }

  @Nested
  inner class TestEnvironmentPostProcessor {

    @Test
    fun `应当创建 TestEnvironmentPostProcessor 实例`() {
      log.trace("testing TestEnvironmentPostProcessor bean creation")

      val postProcessor = testConfigurationBean.testEnvironmentPostProcessor()

      // 验证实例不为 null 并且具有预期的功能
      assertNotNull(postProcessor, "应该创建 TestEnvironmentPostProcessor 实例")
      assertTrue(postProcessor.getRecommendedTestProperties().isNotEmpty(), "应该提供推荐的测试属性")

      // 验证Bean的类型和状态
      assertTrue(postProcessor::class.java.simpleName.contains("TestEnvironmentPostProcessor"), "Bean 类型应该正确")
      assertNotNull(postProcessor.javaClass, "PostProcessor 类型不应为 null")

      // 验证推荐属性的质量
      val recommendedProps = postProcessor.getRecommendedTestProperties()
      assertTrue(recommendedProps.size >= 5, "推荐属性数量应该大于等于 5")
      assertTrue(recommendedProps.containsKey("spring.threads.virtual.enabled"), "应包含虚拟线程配置")
      assertTrue(recommendedProps.containsKey("spring.output.ansi.enabled"), "应包含 ANSI 输出配置")

      log.debug("TestEnvironmentPostProcessor bean creation verified")
    }
  }
}

class TestConfigurationPropertiesTest {

  @Nested
  inner class DefaultValues {

    @Test
    fun `应当具有正确的默认值`() {
      log.trace("verifying default configuration properties")

      val properties = TestConfigurationProperties()

      assertTrue(properties.enabled, "默认应该启用测试配置")
      assertTrue(properties.disableConditionEvaluationReport, "默认应该关闭条件评估报告")
      assertTrue(properties.enableVirtualThreads, "默认应该启用虚拟线程")
      assertEquals(AnsiOutputMode.ALWAYS, properties.ansiOutputMode, "默认应该使用 ALWAYS 颜色输出模式")
      assertTrue(properties.additionalProperties.isEmpty(), "默认额外属性应该为空")

      log.debug("default properties verified successfully")
    }
  }
}

class TestEnvironmentPostProcessorTest {

  private lateinit var postProcessor: io.github.truenine.composeserver.testtoolkit.autoconfig.TestEnvironmentPostProcessor
  private lateinit var mockEnvironment: ConfigurableEnvironment

  @BeforeEach
  fun setUp() {
    log.trace("initializing test environment post processor test")
    postProcessor = io.github.truenine.composeserver.testtoolkit.autoconfig.TestEnvironmentPostProcessor()
    mockEnvironment = mockk()
  }

  @Nested
  inner class GetRecommendedTestProperties {

    @Test
    fun `应当返回推荐的测试属性`() {
      log.trace("testing recommended test properties retrieval")

      val recommendedProperties = postProcessor.getRecommendedTestProperties()

      assertTrue(recommendedProperties.isNotEmpty(), "推荐属性不应该为空")
      assertEquals("true", recommendedProperties["spring.threads.virtual.enabled"], "虚拟线程应该被启用")
      assertEquals("always", recommendedProperties["spring.output.ansi.enabled"], "ANSI 颜色输出应该被设置为 always")
      assertEquals("false", recommendedProperties["spring.jpa.show-sql"], "JPA show-sql 应该被设置为 false")
      assertEquals("create-drop", recommendedProperties["spring.jpa.hibernate.ddl-auto"], "Hibernate DDL auto 应该被设置为 create-drop")
      assertEquals("DEBUG", recommendedProperties["logging.level.org.springframework.web"], "Web 日志级别应该被设置为 DEBUG")

      log.debug("recommended test properties retrieval verified, found {} properties", recommendedProperties.size)
    }
  }

  @Nested
  inner class IsTestEnvironment {

    @Test
    fun `通过 active profiles 检测 - 应当识别为测试环境`() {
      log.trace("testing test environment check via active profiles")

      every { mockEnvironment.activeProfiles } returns arrayOf("test", "local")
      every { mockEnvironment.getProperty("spring.profiles.active", "") } returns ""

      val isTestEnv = postProcessor.isTestEnvironment(mockEnvironment)

      assertTrue(isTestEnv, "应该识别为测试环境（通过 active profiles）")

      log.debug("test environment check via active profiles verified")
    }

    @Test
    fun `通过属性配置检测 - 应当识别为测试环境`() {
      log.trace("testing test environment check via property configuration")

      every { mockEnvironment.activeProfiles } returns emptyArray()
      every { mockEnvironment.getProperty("spring.profiles.active", "") } returns "dev,test"

      val isTestEnv = postProcessor.isTestEnvironment(mockEnvironment)

      assertTrue(isTestEnv, "应该识别为测试环境（通过属性配置）")

      log.debug("test environment check via property configuration verified")
    }

    @Test
    fun `非测试环境 - 不应当识别为测试环境`() {
      log.trace("testing non-test environment check")

      every { mockEnvironment.activeProfiles } returns arrayOf("prod", "staging")
      every { mockEnvironment.getProperty("spring.profiles.active", "") } returns "prod"

      val isTestEnv = postProcessor.isTestEnvironment(mockEnvironment)

      assertFalse(isTestEnv, "不应该识别为测试环境")

      log.debug("non-test environment check verified")
    }
  }
}
