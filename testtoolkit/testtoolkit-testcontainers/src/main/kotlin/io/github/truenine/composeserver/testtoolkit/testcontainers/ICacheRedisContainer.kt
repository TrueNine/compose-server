package io.github.truenine.composeserver.testtoolkit.testcontainers

import java.time.Duration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

/**
 * Redis test container interface.
 *
 * Provides a standard configuration for Redis test containers used in cache integration tests. By implementing this interface, test classes can obtain a
 * preconfigured Redis test instance and use extension functions for convenient testing.
 *
 * Important: container reuse and data cleanup
 *
 * By default, to improve test performance, all containers are reusable. This means data may remain between tests.
 *
 * Data cleanup responsibility:
 * - You must clean up Redis data in tests (for example using `@BeforeEach` or `@AfterEach`).
 * - Recommended cleanup: use `FLUSHALL` or `FLUSHDB` to clear all keys.
 * - It is not recommended to disable reuse because it significantly slows down tests.
 *
 * Features:
 * - Automatically configures a Redis test container.
 * - Container is started automatically when Spring properties are injected.
 * - Container reuse improves performance.
 * - Provides standard Redis connection configuration.
 * - Supports Spring Test dynamic property injection.
 * - Uses random ports to avoid port conflicts.
 *
 * Usage (legacy style): see tests implementing this interface directly.
 *
 * Usage (recommended extension style): use the `redis` extension function with `resetToInitialState` to automatically clear data before tests.
 *
 * @see org.testcontainers.junit.jupiter.Testcontainers
 * @see org.testcontainers.containers.GenericContainer
 * @author TrueNine
 * @since 2025-04-24
 */
@Testcontainers
interface ICacheRedisContainer : ITestContainerBase {
  companion object {
    /**
     * Redis test container instance.
     *
     * Preconfigured Redis container with the following defaults:
     * - Port: 6379 (randomly mapped)
     * - Version: configurable, default 7.4.2-alpine3.21
     * - No password authentication
     * - Container reuse is enabled by default so multiple tests share the same instance
     * - Container is started automatically when Spring properties are injected
     *
     * Important: because of container reuse, data will remain between tests, so make sure to perform proper cleanup in tests.
     */
    @Volatile private var _container: GenericContainer<*>? = null

    @JvmStatic
    val container: GenericContainer<*>
      get() =
        _container
          ?: throw IllegalStateException(
            "Redis container not initialized. Make sure your test class is annotated with @SpringBootTest and implements ICacheRedisContainer."
          )

    /**
     * Creates and starts the Redis container.
     *
     * @return started Redis container instance
     */
    private fun createAndStartContainer(): GenericContainer<*> {
      val config = TestcontainersConfigurationHolder.getTestcontainersProperties()
      return GenericContainer(DockerImageName.parse(config.redis.image)).apply {
        withReuse(config.reuseAllContainers || config.redis.reuse)
        withExposedPorts(6379)
        setWaitStrategy(Wait.forLogMessage(".*Ready to accept connections.*\\n", 1).withStartupTimeout(Duration.ofSeconds(10)))
        start()
      }
    }

    /**
     * Lazily initialized Redis container instance.
     *
     * Used by containers() aggregation functions to return an initialized container instance.
     *
     * @return lazy Redis container instance
     */
    @JvmStatic val redisContainerLazy: Lazy<GenericContainer<*>> by lazy { lazy { container } }

    /**
     * Dynamic property configuration for Spring test environments.
     *
     * Automatically injects Redis connection properties into the Spring test environment:
     * - host
     * - port
     *
     * The container will be created and started when this method is called, ensuring that property values are available.
     *
     * @param registry Spring dynamic property registry
     */
    @JvmStatic
    @DynamicPropertySource
    fun properties(registry: DynamicPropertyRegistry) {
      // Thread-safe container initialization
      if (_container == null) {
        synchronized(ICacheRedisContainer::class.java) {
          if (_container == null) {
            _container = createAndStartContainer()
          }
        }
      }

      val host = container.host
      val port = container.getMappedPort(6379)

      registry.add("spring.data.redis.host") { host }
      registry.add("spring.data.redis.port") { port }
    }
  }

  /**
   * Redis container extension function.
   *
   * Provides a convenient way to test with a Redis container and supports automatic data reset. The container has already been started when Spring properties
   * are injected.
   *
   * @param resetToInitialState whether to reset to the initial state (clear all data), default is true
   * @param block test block that receives the current Redis container instance
   * @return result of the test block
   */
  fun <T> redis(resetToInitialState: Boolean = true, block: (GenericContainer<*>) -> T): T {
    if (resetToInitialState) {
      // Reset Redis to initial state by clearing all data
      try {
        // Use the container to execute the Redis FLUSHALL command
        container.execInContainer("redis-cli", "FLUSHALL")
      } catch (e: Exception) {
        // If cleanup fails, log a warning but continue executing tests
        org.slf4j.LoggerFactory.getLogger(ICacheRedisContainer::class.java).warn("Failed to reset Redis container to initial state: {}", e.message)
      }
    }

    return block(container)
  }
}
