package io.github.truenine.composeserver.testtoolkit.autoconfig

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
    mockPropertySources = mockk<MutablePropertySources>(relaxed = true)
    mockEnvironment = mockk<ConfigurableEnvironment> { every { propertySources } returns mockPropertySources }
    properties = TestConfigurationProperties()
    testcontainersProperties = TestcontainersProperties()
    testConfigurationBean = TestConfigurationBean(mockEnvironment, properties, testcontainersProperties)
  }

  @Test
  fun shouldInjectPropertiesWhenEnabled() {
    testConfigurationBean.configureTestProperties()
    verify { mockPropertySources.addFirst(any<MapPropertySource>()) }
  }

  @Test
  fun shouldSkipInjectionWhenDisabled() {
    properties = TestConfigurationProperties(enabled = false)
    testConfigurationBean = TestConfigurationBean(mockEnvironment, properties, testcontainersProperties)
    testConfigurationBean.configureTestProperties()
    verify(exactly = 0) { mockPropertySources.addFirst(any<MapPropertySource>()) }
  }

  @Test
  fun shouldConfigureConditionEvaluationReportSettings() {
    properties = TestConfigurationProperties(disableConditionEvaluationReport = true, enableVirtualThreads = false, ansiOutputMode = AnsiOutputMode.NEVER)
    testConfigurationBean = TestConfigurationBean(mockEnvironment, properties, testcontainersProperties)

    var capturedPropertySource: MapPropertySource? = null
    every { mockPropertySources.addFirst(any<MapPropertySource>()) } answers { capturedPropertySource = firstArg() }
    testConfigurationBean.configureTestProperties()

    assertNotNull(capturedPropertySource)
    capturedPropertySource?.also { propertySource ->
      assertEquals(false, propertySource.getProperty("debug"))
      assertEquals(false, propertySource.getProperty("spring.test.print-condition-evaluation-report"))
      assertEquals("never", propertySource.getProperty("spring.output.ansi.enabled"))
    }
  }

  @Test
  fun shouldConfigureVirtualThreadsAndAnsiOutput() {
    properties = TestConfigurationProperties(disableConditionEvaluationReport = false, enableVirtualThreads = true, ansiOutputMode = AnsiOutputMode.ALWAYS)
    testConfigurationBean = TestConfigurationBean(mockEnvironment, properties, testcontainersProperties)

    var capturedPropertySource: MapPropertySource? = null
    every { mockPropertySources.addFirst(any<MapPropertySource>()) } answers { capturedPropertySource = firstArg() }
    testConfigurationBean.configureTestProperties()

    assertNotNull(capturedPropertySource)
    capturedPropertySource?.let { propertySource ->
      assertEquals(true, propertySource.getProperty("spring.threads.virtual.enabled"))
      assertEquals("always", propertySource.getProperty("spring.output.ansi.enabled"))
    }
  }

  @Test
  fun shouldConfigureAdditionalProperties() {
    val additionalProps = mapOf("custom.property.1" to "value1", "custom.property.2" to "value2")
    properties = TestConfigurationProperties(additionalProperties = additionalProps)
    testConfigurationBean = TestConfigurationBean(mockEnvironment, properties, testcontainersProperties)

    var capturedPropertySource: MapPropertySource? = null
    every { mockPropertySources.addFirst(any<MapPropertySource>()) } answers { capturedPropertySource = firstArg() }
    testConfigurationBean.configureTestProperties()

    assertNotNull(capturedPropertySource)
    capturedPropertySource?.let { propertySource ->
      assertEquals("value1", propertySource.getProperty("custom.property.1"))
      assertEquals("value2", propertySource.getProperty("custom.property.2"))
    }
  }

  @Test
  fun shouldCreateTestEnvironmentPostProcessor() {
    val postProcessor = testConfigurationBean.testEnvironmentPostProcessor()
    assertNotNull(postProcessor)
    assertTrue(postProcessor.getRecommendedTestProperties().isNotEmpty())
  }
}

class TestConfigurationPropertiesTest {

  @Test
  fun shouldHaveCorrectDefaultValues() {
    val properties = TestConfigurationProperties()

    assertTrue(properties.enabled)
    assertTrue(properties.disableConditionEvaluationReport)
    assertTrue(properties.enableVirtualThreads)
    assertEquals(AnsiOutputMode.ALWAYS, properties.ansiOutputMode)
    assertTrue(properties.additionalProperties.isEmpty())
  }
}

class TestEnvironmentPostProcessorTest {

  private lateinit var postProcessor: TestEnvironmentPostProcessor
  private lateinit var mockEnvironment: ConfigurableEnvironment

  @BeforeEach
  fun setUp() {
    postProcessor = TestEnvironmentPostProcessor()
    mockEnvironment = mockk()
  }

  @Test
  fun shouldReturnRecommendedTestProperties() {
    val recommendedProperties = postProcessor.getRecommendedTestProperties()

    assertTrue(recommendedProperties.isNotEmpty())
    assertEquals("true", recommendedProperties["spring.threads.virtual.enabled"])
    assertEquals("always", recommendedProperties["spring.output.ansi.enabled"])
  }

  @Test
  fun shouldDetectTestEnvironmentViaActiveProfiles() {
    every { mockEnvironment.activeProfiles } returns arrayOf("test", "local")
    every { mockEnvironment.getProperty("spring.profiles.active", "") } returns ""

    assertTrue(postProcessor.isTestEnvironment(mockEnvironment))
  }

  @Test
  fun shouldDetectTestEnvironmentViaPropertyConfiguration() {
    every { mockEnvironment.activeProfiles } returns emptyArray()
    every { mockEnvironment.getProperty("spring.profiles.active", "") } returns "dev,test"

    assertTrue(postProcessor.isTestEnvironment(mockEnvironment))
  }

  @Test
  fun shouldNotDetectNonTestEnvironment() {
    every { mockEnvironment.activeProfiles } returns arrayOf("prod", "staging")
    every { mockEnvironment.getProperty("spring.profiles.active", "") } returns "prod"

    assertFalse(postProcessor.isTestEnvironment(mockEnvironment))
  }
}
