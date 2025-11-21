package io.github.truenine.composeserver.testtoolkit.autoconfig

import io.github.truenine.composeserver.testtoolkit.SystemTestLogger
import org.slf4j.LoggerFactory
import org.springframework.boot.Banner
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.env.MapPropertySource

/**
 * Test environment application listener.
 *
 * Configures test-related properties during the application environment
 * preparation phase.
 */
class TestEnvironmentApplicationListener : ApplicationListener<ApplicationEnvironmentPreparedEvent> {

  private val log: SystemTestLogger = LoggerFactory.getLogger(TestEnvironmentApplicationListener::class.java)

  override fun onApplicationEvent(event: ApplicationEnvironmentPreparedEvent) {
    val environment = event.environment

    // Check whether the test toolkit is enabled
    val enabled = environment.getProperty("compose.testtoolkit.enabled", Boolean::class.java, true)
    if (!enabled) {
      log.debug("test toolkit disabled")
      return
    }

    log.trace("configuring early test properties")

    val testProperties = mutableMapOf<String, Any>()

    // Read configuration properties
    val disableConditionEvaluationReport = environment.getProperty("compose.testtoolkit.disable-condition-evaluation-report", Boolean::class.java, true)
    val enableVirtualThreads = environment.getProperty("compose.testtoolkit.enable-virtual-threads", Boolean::class.java, true)
    val ansiOutputMode = environment.getProperty("compose.testtoolkit.ansi-output-mode", String::class.java, "always")

    // Disable condition evaluation report and banner
    if (disableConditionEvaluationReport) {
      testProperties["debug"] = false
      testProperties["spring.test.print-condition-evaluation-report"] = false
      testProperties["spring.main.log-startup-info"] = false
      testProperties["spring.main.banner-mode"] = Banner.Mode.OFF.name
      log.trace("disabled condition evaluation report and banner")
    }

    // Enable virtual threads
    if (enableVirtualThreads) {
      testProperties["spring.threads.virtual.enabled"] = true
      log.trace("enabled virtual threads")
    }

    // Configure ANSI color output
    testProperties["spring.output.ansi.enabled"] = ansiOutputMode
    log.trace("set ansi output: {}", ansiOutputMode)

    // Add properties into the environment
    if (testProperties.isNotEmpty()) {
      val propertySource = MapPropertySource("testToolkitEarlyProperties", testProperties)
      environment.propertySources.addFirst(propertySource)
      log.debug("added {} early properties", testProperties.size)
    }
  }
}
