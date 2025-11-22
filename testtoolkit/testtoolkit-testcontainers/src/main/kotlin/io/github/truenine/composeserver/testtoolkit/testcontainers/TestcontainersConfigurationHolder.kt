package io.github.truenine.composeserver.testtoolkit.testcontainers

import io.github.truenine.composeserver.testtoolkit.log
import io.github.truenine.composeserver.testtoolkit.properties.TestcontainersProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

/**
 * Testcontainers configuration holder.
 *
 * Utility for accessing Spring configuration statically from companion objects. Because container interfaces use companion objects, they cannot receive Spring
 * beans directly, so this holder exposes the configuration.
 *
 * Container reuse configuration:
 * - Controls reuse behavior for all Testcontainers-based containers.
 * - `reuseAllContainers` can be used as a global switch.
 * - Each container type has its own reuse configuration as well.
 *
 * Important: container reuse causes data to remain between tests, so make sure to perform proper cleanup.
 *
 * @author TrueNine
 * @since 2025-07-19
 */
@Component
class TestcontainersConfigurationHolder : ApplicationContextAware {

  companion object {
    private var applicationContext: ApplicationContext? = null

    /**
     * Returns the Testcontainers configuration properties.
     *
     * @return TestcontainersProperties or a default configuration if none is available
     */
    fun getTestcontainersProperties(): TestcontainersProperties {
      return try {
        applicationContext?.getBean(TestcontainersProperties::class.java) ?: TestcontainersProperties()
      } catch (e: Exception) {
        // Fall back to default configuration if properties cannot be resolved
        TestcontainersProperties()
      }
    }
  }

  override fun setApplicationContext(applicationContext: ApplicationContext) {
    log.trace("exposing testcontainers properties as bean")
    Companion.applicationContext = applicationContext
  }
}
