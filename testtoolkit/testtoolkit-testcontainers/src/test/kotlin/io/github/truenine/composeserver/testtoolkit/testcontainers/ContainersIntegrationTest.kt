package io.github.truenine.composeserver.testtoolkit.testcontainers

import io.github.truenine.composeserver.testtoolkit.log
import io.minio.*
import jakarta.annotation.Resource
import kotlin.test.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.jdbc.core.JdbcTemplate

/**
 * Containers integration tests.
 *
 * Verifies combined usage of the three test containers (PostgreSQL, Redis, MinIO). Ensures all containers can start correctly, are configured properly, and
 * support basic operations.
 *
 * @author TrueNine
 * @since 2025-04-24
 */
@SpringBootTest
@Import(TestConfiguration::class)
class ContainersIntegrationTest : IDatabasePostgresqlContainer, ICacheRedisContainer, IOssMinioContainer {

  @Resource private lateinit var jdbcTemplate: JdbcTemplate

  @Resource private lateinit var redisTemplate: StringRedisTemplate

  private val minioClient: MinioClient by lazy {
    minio { MinioClient.builder().endpoint("http://localhost:${it.getMappedPort(9000)}").credentials("minioadmin", "minioadmin").build() }
  }

  @BeforeEach
  fun setup() {
    // Initialize PostgreSQL test table and clean existing data
    // Drop table if it exists to ensure a clean state
    jdbcTemplate.execute("drop table if exists test_table")

    // Re-create table
    jdbcTemplate.execute(
      """
      create table test_table (
          id serial primary key,
          name varchar(255) not null
      )
      """
        .trimIndent()
    )

    log.info("PostgreSQL test_table created successfully")

    // Clean up MinIO test buckets
    cleanupMinioBuckets()
  }

  /**
   * Clean up MinIO test buckets
   *
   * This method removes test buckets that start with "test-" or "combined-" prefixes. It first removes all objects in the bucket, then removes the bucket
   * itself. This is a best-effort cleanup - if buckets cannot be removed, the test continues.
   */
  private fun cleanupMinioBuckets() {
    runCatching {
        val buckets = minioClient.listBuckets()
        buckets
          .filter { bucket -> bucket.name().startsWith("test-") || bucket.name().startsWith("combined-") }
          .forEach { bucket -> cleanupSingleBucket(bucket.name()) }
      }
      .onFailure { exception -> log.warn("Failed to list or clean up MinIO buckets: ${exception.message}") }
  }

  /** Clean up a single MinIO bucket by removing all objects first, then the bucket */
  private fun cleanupSingleBucket(bucketName: String) {
    runCatching {
        if (minioClient.bucketExists(io.minio.BucketExistsArgs.builder().bucket(bucketName).build())) {
          log.debug("Cleaning up bucket: $bucketName")

          // Remove all objects in the bucket
          val objects = minioClient.listObjects(io.minio.ListObjectsArgs.builder().bucket(bucketName).build())
          objects.forEach { objectResult ->
            runCatching {
                val objectName = objectResult.get().objectName()
                log.debug("Removing object: $objectName from bucket: $bucketName")
                minioClient.removeObject(io.minio.RemoveObjectArgs.builder().bucket(bucketName).`object`(objectName).build())
              }
              .onFailure { exception -> log.warn("Failed to remove object from bucket $bucketName: ${exception.message}") }
          }

          // Remove the bucket
          minioClient.removeBucket(io.minio.RemoveBucketArgs.builder().bucket(bucketName).build())
          log.debug("Successfully removed bucket: $bucketName")
        }
      }
      .onFailure { exception -> log.warn("Failed to clean up bucket $bucketName: ${exception.message}") }
  }

  @Test
  fun `verify PostgreSQL container works correctly`() {
    jdbcTemplate.update("insert into test_table (name) values (?)", "test_name")
    val result = jdbcTemplate.queryForObject("select name from test_table where name = ?", String::class.java, "test_name")
    assertEquals("test_name", result)
  }

  @Test
  fun `verify Redis container works correctly`() {
    val key = "test:key"
    val value = "test_value"
    redisTemplate.opsForValue().set(key, value)
    val result = redisTemplate.opsForValue().get(key)
    assertEquals(value, result)
  }

  @Test
  fun `verify MinIO container works correctly`() {
    val bucketName = "test-bucket"
    if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
    }
    val exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())
    assertTrue(exists)
  }

  @Test
  fun `verify all containers work correctly together`() {
    jdbcTemplate.update("insert into test_table (name) values (?)", "combined_test")
    val redisKey = "combined:test"
    redisTemplate.opsForValue().set(redisKey, "combined_value")
    val bucketName = "combined-test-bucket"
    if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
    }

    val pgResult = jdbcTemplate.queryForObject("select name from test_table where name = ?", String::class.java, "combined_test")
    val redisResult = redisTemplate.opsForValue().get(redisKey)
    val minioResult = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())

    assertEquals("combined_test", pgResult)
    assertEquals("combined_value", redisResult)
    assertTrue(minioResult)
  }

  @Test
  fun `verify multi-container cooperation using new aggregation function`() =
    containers(ICacheRedisContainer.redisContainerLazy, IDatabasePostgresqlContainer.postgresqlContainerLazy, IOssMinioContainer.minioContainerLazy) {
      // Access containers through the aggregation context
      val redis = getRedisContainer()
      val postgres = getPostgresContainer()
      val minio = getMinioContainer()

      assertNotNull(redis, "Redis container should exist")
      assertNotNull(postgres, "PostgreSQL container should exist")
      assertNotNull(minio, "MinIO container should exist")

      // Verify all containers are running
      assertTrue(redis!!.isRunning, "Redis container should be running")
      assertTrue(postgres!!.isRunning, "PostgreSQL container should be running")
      assertTrue(minio!!.isRunning, "MinIO container should be running")

      // Cross-container data operations
      jdbcTemplate.update("insert into test_table (name) values (?)", "aggregation_test")
      redisTemplate.opsForValue().set("aggregation:key", "aggregation_value")

      // Verify operation results
      val pgResult = jdbcTemplate.queryForObject("select name from test_table where name = ?", String::class.java, "aggregation_test")
      val redisResult = redisTemplate.opsForValue().get("aggregation:key")

      assertEquals("aggregation_test", pgResult, "PostgreSQL operation should succeed")
      assertEquals("aggregation_value", redisResult, "Redis operation should succeed")

      // Verify container count
      assertEquals(3, getAllContainers().size, "There should be 3 containers")
    }
}
