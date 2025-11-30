package io.github.truenine.composeserver.testtoolkit.testcontainers

import io.github.truenine.composeserver.testtoolkit.SpringBootConfigurationPropertiesPrefixes
import java.time.Duration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

/**
 * MinIO test container interface.
 *
 * Provides a standard configuration for MinIO test containers used in object storage integration tests. By implementing this interface, test classes can obtain
 * a preconfigured MinIO test instance and use extension functions for convenient testing.
 *
 * Important: container reuse and data cleanup
 *
 * By default, to improve test performance, all containers are reusable. This means objects and buckets in MinIO may remain between tests.
 *
 * Data cleanup responsibility:
 * - You must clean up MinIO data in tests (for example using `@BeforeEach` or `@AfterEach`).
 * - Recommended cleanup:
 *     - Delete all objects in test buckets using the MinIO client.
 *     - Delete test buckets after use.
 *     - Use unique bucket names (for example `test-bucket-${'$'}{java.util.UUID.randomUUID()}`) to avoid conflicts.
 * - It is not recommended to disable container reuse because it significantly slows down tests.
 *
 * Features:
 * - Automatically configures a MinIO test container.
 * - Container is started automatically when Spring properties are injected.
 * - Container reuse improves performance.
 * - Provides standard MinIO connection configuration.
 * - Supports Spring Test dynamic property injection.
 * - Uses random ports to avoid port conflicts.
 * - Supports S3-compatible APIs.
 *
 * Usage: see tests implementing this interface directly or use the `minio` extension function for a more concise style.
 *
 * @see org.testcontainers.junit.jupiter.Testcontainers
 * @see org.testcontainers.containers.GenericContainer
 * @author TrueNine
 * @since 2025-04-24
 */
@Testcontainers
interface IOssMinioContainer : ITestContainerBase {
  companion object {
    /**
     * MinIO test container instance.
     *
     * Preconfigured MinIO container with settings customizable via configuration:
     * - Access key: configurable, default `minioadmin`.
     * - Secret key: configurable, default `minioadmin`.
     * - API port: randomly mapped.
     * - Console port: randomly mapped.
     * - Image: configurable, default `minio/minio:RELEASE.2025-04-22T22-12-26Z`.
     * - Container reuse is enabled by default so multiple tests share the same instance.
     * - Container is started automatically when Spring properties are injected or when it is first accessed.
     *
     * Important: because of container reuse, objects and buckets in MinIO will remain between tests, so make sure to perform proper cleanup.
     */
    @Volatile private var _container: GenericContainer<*>? = null

    @JvmStatic
    val container: GenericContainer<*>
      get() {
        if (_container == null) {
          synchronized(IOssMinioContainer::class.java) {
            if (_container == null) {
              _container = createAndStartContainer()
            }
          }
        }
        return _container!!
      }

    /**
     * Creates and starts the MinIO container.
     *
     * @return started MinIO container instance
     */
    private fun createAndStartContainer(): GenericContainer<*> {
      val config = TestcontainersConfigurationHolder.getTestcontainersProperties()
      return GenericContainer(DockerImageName.parse(config.minio.image)).apply {
        withReuse(config.reuseAllContainers || config.minio.reuse)
        withEnv("MINIO_ROOT_USER", config.minio.accessKey)
        withEnv("MINIO_ROOT_PASSWORD", config.minio.secretKey)
        withEnv("MINIO_CONSOLE_ADDRESS", ":9001")
        withCommand("server", "/data")
        withExposedPorts(9000, 9001)
        // Wait for the MinIO API port to become available, not just log output
        setWaitStrategy(Wait.forHttp("/minio/health/live").forPort(9000).withStartupTimeout(Duration.ofSeconds(30)))
        start()
      }
    }

    /**
     * Lazily initialized MinIO container instance.
     *
     * Used by containers() aggregation functions to return an initialized container instance.
     *
     * @return lazy MinIO container instance
     */
    @JvmStatic val minioContainerLazy: Lazy<GenericContainer<*>> by lazy { lazy { container } }

    /**
     * Dynamic property configuration for Spring test environments.
     *
     * Automatically injects MinIO connection properties into the Spring test environment:
     * - endpoint URL
     * - access key
     * - secret key
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
        synchronized(IOssMinioContainer::class.java) {
          if (_container == null) {
            _container = createAndStartContainer()
          }
        }
      }

      val config = TestcontainersConfigurationHolder.getTestcontainersProperties()
      val host = container.host
      val port = container.getMappedPort(9000)

      registry.add(SpringBootConfigurationPropertiesPrefixes.OSS_MINIO_ENDPOINT) { host }
      registry.add(SpringBootConfigurationPropertiesPrefixes.OSS_MINIO_EXPOSED_BASE_URL) { "http://$host:$port" }
      registry.add(SpringBootConfigurationPropertiesPrefixes.OSS_MINIO_PORT) { port }
      registry.add(SpringBootConfigurationPropertiesPrefixes.OSS_MINIO_ACCESS_KEY) { config.minio.accessKey }
      registry.add(SpringBootConfigurationPropertiesPrefixes.OSS_MINIO_SECRET_KEY) { config.minio.secretKey }
      registry.add(SpringBootConfigurationPropertiesPrefixes.OSS_MINIO_ENABLE_SSL) { false }
      registry.add(SpringBootConfigurationPropertiesPrefixes.OSS_ENABLE_SSL) { false }
    }
  }

  /**
   * MinIO container extension function.
   *
   * Provides a convenient way to test with a MinIO container and supports automatic data reset. The container has already been started when Spring properties
   * are injected.
   *
   * @param resetToInitialState whether to reset to the initial state (clear all test-related buckets and objects), default is true
   * @param block test block that receives the current MinIO container instance
   * @return result of the test block
   */
  fun <T> minio(resetToInitialState: Boolean = true, block: (GenericContainer<*>) -> T): T {

    if (resetToInitialState) {
      // Reset MinIO to initial state by clearing all test-related buckets
      try {
        // Use the MinIO Java client for cleanup, which is more reliable than mc
        val config = TestcontainersConfigurationHolder.getTestcontainersProperties()
        val minioClient =
          io.minio.MinioClient.builder()
            .endpoint("http://${container.host}:${container.getMappedPort(9000)}")
            .credentials(config.minio.accessKey, config.minio.secretKey)
            .build()

        // List all buckets
        val buckets = minioClient.listBuckets()
        val testBuckets =
          buckets.filter { bucket ->
            // Only delete test-related buckets (starting with test- or combined-)
            bucket.name().startsWith("test-") || bucket.name().startsWith("combined-")
          }

        // Delete test buckets and all objects inside them
        testBuckets.forEach { bucket ->
          try {
            val bucketName = bucket.name()

            // Delete all objects in the bucket
            val objects = minioClient.listObjects(io.minio.ListObjectsArgs.builder().bucket(bucketName).recursive(true).build())

            val objectsToDelete = objects.map { result -> io.minio.messages.DeleteObject(result.get().objectName()) }.toList()

            if (objectsToDelete.isNotEmpty()) {
              minioClient.removeObjects(io.minio.RemoveObjectsArgs.builder().bucket(bucketName).objects(objectsToDelete).build())
            }

            // Delete the bucket itself
            minioClient.removeBucket(io.minio.RemoveBucketArgs.builder().bucket(bucketName).build())
          } catch (e: Exception) {
            org.slf4j.LoggerFactory.getLogger(IOssMinioContainer::class.java)
              .debug("Exception occurred while deleting test bucket {} (it may not exist): {}", bucket.name(), e.message)
          }
        }
      } catch (e: Exception) {
        // If cleanup fails, log a warning but continue executing tests
        org.slf4j.LoggerFactory.getLogger(IOssMinioContainer::class.java).warn("Failed to reset MinIO container to initial state: {}", e.message)
      }
    }

    return block(container)
  }
}
