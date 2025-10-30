package io.github.truenine.composeserver.testtoolkit.testcontainers

import io.github.truenine.composeserver.testtoolkit.log
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import jakarta.annotation.Resource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.jdbc.core.JdbcTemplate

/**
 * # 容器集成测试
 *
 * 该测试类验证三个测试容器（PostgreSQL、Redis、MinIO）的组合使用。 测试确保所有容器能够正常启动、配置正确并且可以进行基本操作。
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
    // 初始化 PostgreSQL 测试表并清理数据
    // 先删除表（如果存在）以确保干净的状态
    jdbcTemplate.execute("drop table if exists test_table")

    // 重新创建表
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

    // 清理 MinIO 测试桶
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
  fun `验证 PostgreSQL 容器正常工作`() {
    jdbcTemplate.update("insert into test_table (name) values (?)", "test_name")
    val result = jdbcTemplate.queryForObject("select name from test_table where name = ?", String::class.java, "test_name")
    assertEquals("test_name", result)
  }

  @Test
  fun `验证 Redis 容器正常工作`() {
    val key = "test:key"
    val value = "test_value"
    redisTemplate.opsForValue().set(key, value)
    val result = redisTemplate.opsForValue().get(key)
    assertEquals(value, result)
  }

  @Test
  fun `验证 MinIO 容器正常工作`() {
    val bucketName = "test-bucket"
    if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
    }
    val exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())
    assertTrue(exists)
  }

  @Test
  fun `验证所有容器能够同时正常工作`() {
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
  fun `使用新的容器聚合函数验证多容器协作`() =
    containers(ICacheRedisContainer.redisContainerLazy, IDatabasePostgresqlContainer.postgresqlContainerLazy, IOssMinioContainer.minioContainerLazy) {
      // 通过上下文访问容器
      val redis = getRedisContainer()
      val postgres = getPostgresContainer()
      val minio = getMinioContainer()

      assertNotNull(redis, "Redis 容器应该存在")
      assertNotNull(postgres, "PostgreSQL 容器应该存在")
      assertNotNull(minio, "MinIO 容器应该存在")

      // 验证所有容器都在运行
      assertTrue(redis!!.isRunning, "Redis 容器应该运行")
      assertTrue(postgres!!.isRunning, "PostgreSQL 容器应该运行")
      assertTrue(minio!!.isRunning, "MinIO 容器应该运行")

      // 跨容器数据操作测试
      jdbcTemplate.update("insert into test_table (name) values (?)", "aggregation_test")
      redisTemplate.opsForValue().set("aggregation:key", "aggregation_value")

      // 验证操作结果
      val pgResult = jdbcTemplate.queryForObject("select name from test_table where name = ?", String::class.java, "aggregation_test")
      val redisResult = redisTemplate.opsForValue().get("aggregation:key")

      assertEquals("aggregation_test", pgResult, "PostgreSQL 操作应该成功")
      assertEquals("aggregation_value", redisResult, "Redis 操作应该成功")

      // 验证容器数量
      assertEquals(3, getAllContainers().size, "应该有3个容器")
    }
}
