package io.github.truenine.composeserver.testtoolkit.testcontainers

import org.testcontainers.containers.*

/**
 * Base interface for test containers.
 *
 * Common base for all test-container interfaces, providing container aggregation capabilities. Test classes must implement this interface or its subinterfaces
 * to use the `containers()` aggregation function.
 *
 * Features:
 * - Aggregates multiple containers for a single test.
 * - Provides unified container lifecycle handling.
 * - Supports coordinated testing across multiple containers.
 * - Containers are started automatically when Spring properties are injected.
 *
 * Example usage:
 * ```kotlin
 * @SpringBootTest
 * class MyTest : ICacheRedisContainer, IDatabasePostgresqlContainer {
 *   @Test
 *   fun `multi-container test`() = containers(
 *     redisContainerLazy,
 *     postgresqlContainerLazy,
 *   ) {
 *     // Multi-container testing within this context
 *     val redis = getRedisContainer()
 *     val postgres = getPostgresContainer()
 *   }
 * }
 * ```
 *
 * @author TrueNine
 * @since 2025-08-09
 */
interface ITestContainerBase {
  /**
   * Aggregates multiple containers for an integration test.
   *
   * Allows using multiple test containers together. Within the block, all provided containers can be accessed through the context. Containers have already been
   * started when Spring properties are injected, so no extra startup logic is required.
   *
   * @param containerLazies lazy container instances to aggregate
   * @param block test block executed within an `IContainersContext`
   * @return result of the test block
   */
  fun <T> containers(vararg containerLazies: Lazy<out GenericContainer<*>>, block: IContainersContext.() -> T): T {
    val containers =
      containerLazies.map { lazy ->
        // Containers are already started in @DynamicPropertySource, just access them.
        lazy.value
      }
    val context = ContainersContextImpl(containers)
    return context.block()
  }
}

/**
 * Aggregated container context interface.
 *
 * Context provided inside the `containers()` function, used to access and operate on multiple test containers.
 *
 * @author TrueNine
 * @since 2025-08-09
 */
interface IContainersContext {
  /**
   * Gets the Redis container.
   *
   * @return Redis container instance, or null if not present
   */
  fun getRedisContainer(): GenericContainer<*>?

  /**
   * Gets the PostgreSQL container.
   *
   * @return PostgreSQL container instance, or null if not present
   */
  fun getPostgresContainer(): PostgreSQLContainer<*>?

  /**
   * Gets the MySQL container.
   *
   * @return MySQL container instance, or null if not present
   */
  fun getMysqlContainer(): MySQLContainer<*>?

  /**
   * Gets the MinIO container.
   *
   * @return MinIO container instance, or null if not present
   */
  fun getMinioContainer(): GenericContainer<*>?

  /**
   * Gets all containers.
   *
   * @return list of all containers
   */
  fun getAllContainers(): List<GenericContainer<*>>
}

/**
 * Default implementation of the aggregated container context.
 *
 * @param containers list of containers passed into the context
 */
private class ContainersContextImpl(private val containers: List<GenericContainer<*>>) : IContainersContext {

  override fun getRedisContainer(): GenericContainer<*>? {
    return containers.find { container ->
      // Identify Redis container by image name
      container.dockerImageName.contains("redis", ignoreCase = true)
    }
  }

  override fun getPostgresContainer(): PostgreSQLContainer<*>? {
    return containers.find { it is PostgreSQLContainer<*> } as PostgreSQLContainer<*>?
  }

  override fun getMysqlContainer(): MySQLContainer<*>? {
    return containers.find { it is MySQLContainer<*> } as MySQLContainer<*>?
  }

  override fun getMinioContainer(): GenericContainer<*>? {
    return containers.find { container ->
      // Identify MinIO container by image name
      container.dockerImageName.contains("minio", ignoreCase = true)
    }
  }

  override fun getAllContainers(): List<GenericContainer<*>> {
    return containers.toList()
  }
}
