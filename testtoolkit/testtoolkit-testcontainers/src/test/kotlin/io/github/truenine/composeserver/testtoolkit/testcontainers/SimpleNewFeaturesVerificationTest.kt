package io.github.truenine.composeserver.testtoolkit.testcontainers

import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

/**
 * Simple new features verification tests.
 *
 * Verifies that refactored basic features work correctly, avoiding potential issues from overly complex integration tests.
 *
 * @author TrueNine
 * @since 2025-08-09
 */
@SpringBootTest
@Import(TestConfiguration::class)
@EnableAutoConfiguration(
  excludeName =
    [
      "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
      "org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration",
      "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
    ]
)
class SimpleNewFeaturesVerificationTest : ICacheRedisContainer {

  @Test
  fun verify_extension_function_basic_functionality() =
    redis(resetToInitialState = false) { container ->
      // Verify container instance is not null and is running
      assertNotNull(container, "Container should not be null")
      assertTrue(container.isRunning, "Container should be running")

      // Verify port mapping exists
      val port = container.getMappedPort(6379)
      assertTrue(port > 0, "Port mapping should be valid")

      // Explicitly return Unit
      Unit
    }

  @Test
  fun verify_lazy_variable_accessibility() {
    // Verify that lazy variable can be accessed
    val lazyContainer = ICacheRedisContainer.redisContainerLazy
    assertNotNull(lazyContainer, "Lazy variable should exist")

    // Verify that lazy variable value is a container instance
    val container = lazyContainer.value
    assertNotNull(container, "Lazy-loaded container instance should exist")
    assertTrue(container.isRunning, "Lazy-loaded container should be running")
  }

  @Test
  fun verify_container_aggregation_basic_functionality() =
    containers(ICacheRedisContainer.redisContainerLazy) {
      // Verify that container can be accessed in the aggregation context
      val redisContainer = getRedisContainer()
      assertNotNull(redisContainer, "Should be able to get Redis container from context")
      assertTrue(redisContainer!!.isRunning, "Retrieved container should be running")

      // Verify getAllContainers behavior
      val allContainers = getAllContainers()
      assertTrue(allContainers.isNotEmpty(), "There should be at least one container")

      // Explicitly return Unit
      Unit
    }
}
