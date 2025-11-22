package io.github.truenine.composeserver.testtoolkit.autoconfig

import io.github.truenine.composeserver.testtoolkit.log
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.MutablePropertySources

class TestEnvironmentApplicationListenerTest {

  private lateinit var listener: TestEnvironmentApplicationListener
  private lateinit var mockEvent: ApplicationEnvironmentPreparedEvent
  private lateinit var mockEnvironment: ConfigurableEnvironment
  private lateinit var mockPropertySources: MutablePropertySources

  @BeforeEach
  fun setUp() {
    log.trace("initializing test environment application listener test")

    mockPropertySources = mockk<MutablePropertySources>(relaxed = true)
    mockEnvironment = mockk<ConfigurableEnvironment> { every { propertySources } returns mockPropertySources }
    mockEvent = mockk<ApplicationEnvironmentPreparedEvent> { every { environment } returns mockEnvironment }

    listener = TestEnvironmentApplicationListener()
  }

  @Nested
  inner class OnApplicationEvent {

    @Test
    fun `enabled state should configure early properties`() {
      log.trace("testing early property configuration when enabled")

      // Configure environment properties
      every { mockEnvironment.getProperty("compose.testtoolkit.enabled", Boolean::class.java, true) } returns true
      every { mockEnvironment.getProperty("compose.testtoolkit.disable-condition-evaluation-report", Boolean::class.java, true) } returns true
      every { mockEnvironment.getProperty("compose.testtoolkit.enable-virtual-threads", Boolean::class.java, true) } returns true
      every { mockEnvironment.getProperty("compose.testtoolkit.ansi-output-mode", String::class.java, "always") } returns "always"

      // Capture the added property source
      var capturedPropertySource: MapPropertySource? = null
      every { mockPropertySources.addFirst(any<MapPropertySource>()) } answers { capturedPropertySource = firstArg() }

      listener.onApplicationEvent(mockEvent)

      // Verify that the property source was added
      verify { mockPropertySources.addFirst(any<MapPropertySource>()) }

      // Verify early properties
      assertTrue(capturedPropertySource != null, "Property source should not be null")
      capturedPropertySource?.also { propertySource ->
        // Verify the property source itself
        assertEquals("testToolkitEarlyProperties", propertySource.name, "Property source name should be correct")
        assertNotNull(propertySource.source, "Property source data should not be null")

        // Verify property types and values
        assertTrue(propertySource.getProperty("debug") is Boolean, "debug property should be Boolean")
        assertEquals(false, propertySource.getProperty("debug"), "debug should be set to false")

        assertTrue(
          propertySource.getProperty("spring.test.print-condition-evaluation-report") is Boolean,
          "condition evaluation report property should be Boolean",
        )
        assertEquals(false, propertySource.getProperty("spring.test.print-condition-evaluation-report"), "condition evaluation report should be disabled")

        assertTrue(propertySource.getProperty("spring.main.log-startup-info") is Boolean, "startup info log property should be Boolean")
        assertEquals(false, propertySource.getProperty("spring.main.log-startup-info"), "startup info log should be disabled")

        assertTrue(propertySource.getProperty("spring.main.banner-mode") is String, "banner mode property should be String")
        assertEquals("OFF", propertySource.getProperty("spring.main.banner-mode"), "banner should be disabled")

        assertTrue(propertySource.getProperty("spring.threads.virtual.enabled") is Boolean, "virtual threads property should be Boolean")
        assertEquals(true, propertySource.getProperty("spring.threads.virtual.enabled"), "virtual threads should be enabled")

        assertTrue(propertySource.getProperty("spring.output.ansi.enabled") is String, "ANSI output property should be String")
        assertEquals("always", propertySource.getProperty("spring.output.ansi.enabled"), "ANSI color output should be enabled")

        // Verify that the ANSI output mode value is valid
        assertTrue(propertySource.getProperty("spring.output.ansi.enabled") in listOf("always", "never", "detect"), "ANSI output mode should be a valid value")
      }

      log.debug("early property configuration verified when enabled")
    }

    @Test
    fun `disabled state should not configure early properties`() {
      log.trace("testing early property configuration when disabled")

      // Set environment to disabled
      every { mockEnvironment.getProperty("compose.testtoolkit.enabled", Boolean::class.java, true) } returns false

      listener.onApplicationEvent(mockEvent)

      // Verify that the property source was not added
      verify(exactly = 0) { mockPropertySources.addFirst(any<MapPropertySource>()) }

      // Verify that environment and listener are still valid when disabled
      assertNotNull(mockEnvironment, "Environment should not be null")
      assertNotNull(listener, "Listener should not be null")

      log.debug("early property configuration verified when disabled")
    }

    @Test
    fun `partial configuration should only configure enabled features`() {
      log.trace("testing partial configuration")

      // Configure environment properties
      every { mockEnvironment.getProperty("compose.testtoolkit.enabled", Boolean::class.java, true) } returns true
      every { mockEnvironment.getProperty("compose.testtoolkit.disable-condition-evaluation-report", Boolean::class.java, true) } returns false
      every { mockEnvironment.getProperty("compose.testtoolkit.enable-virtual-threads", Boolean::class.java, true) } returns true
      every { mockEnvironment.getProperty("compose.testtoolkit.ansi-output-mode", String::class.java, "always") } returns "detect"

      // Capture the added property source
      var capturedPropertySource: MapPropertySource? = null
      every { mockPropertySources.addFirst(any<MapPropertySource>()) } answers { capturedPropertySource = firstArg() }

      listener.onApplicationEvent(mockEvent)

      // Verify that the property source was added
      verify { mockPropertySources.addFirst(any<MapPropertySource>()) }

      // Verify that only virtual threads and color output are configured
      assertTrue(capturedPropertySource != null, "Property source should not be null")
      capturedPropertySource?.also { propertySource ->
        // Verify enabled properties
        assertTrue(propertySource.getProperty("spring.threads.virtual.enabled") is Boolean, "virtual threads property should be Boolean")
        assertEquals(true, propertySource.getProperty("spring.threads.virtual.enabled"), "virtual threads should be enabled")

        assertTrue(propertySource.getProperty("spring.output.ansi.enabled") is String, "ANSI output property should be String")
        assertEquals("detect", propertySource.getProperty("spring.output.ansi.enabled"), "ANSI color output should be set to detect")

        // Verify properties that should not be set
        assertTrue(propertySource.getProperty("debug") == null, "debug should not be set")
        assertTrue(propertySource.getProperty("spring.main.banner-mode") == null, "banner should not be set")
        assertTrue(propertySource.getProperty("spring.test.print-condition-evaluation-report") == null, "condition evaluation report should not be set")
        assertTrue(propertySource.getProperty("spring.main.log-startup-info") == null, "startup info log should not be set")

        // Verify that the ANSI output mode value is valid
        assertTrue(propertySource.getProperty("spring.output.ansi.enabled") in listOf("always", "never", "detect"), "ANSI output mode should be a valid value")
      }

      log.debug("partial configuration verified")
    }
  }
}
