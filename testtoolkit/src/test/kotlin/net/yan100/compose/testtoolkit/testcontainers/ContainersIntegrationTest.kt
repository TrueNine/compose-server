package net.yan100.compose.testtoolkit.testcontainers

import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import jakarta.annotation.Resource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * # 容器集成测试
 *
 * 该测试类验证三个测试容器（PostgreSQL、Redis、MinIO）的组合使用。
 * 测试确保所有容器能够正常启动、配置正确并且可以进行基本操作。
 *
 * @author TrueNine
 * @since 2025-04-24
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfiguration::class)
class ContainersIntegrationTest :
  IDatabasePostgresqlContainer,
  ICacheRedisContainer,
  IOssMinioContainer {

  @Resource
  private lateinit var jdbcTemplate: JdbcTemplate

  @Resource
  private lateinit var redisTemplate: StringRedisTemplate

  private lateinit var minioClient: MinioClient

  @BeforeEach
  fun setup() {
    // 初始化 MinIO 客户端
    minioClient = MinioClient.builder()
      .endpoint("http://localhost:${minioContainer?.getMappedPort(9000)}")
      .credentials("minioadmin", "minioadmin")
      .build()

    // 初始化 PostgreSQL 测试表
    jdbcTemplate.execute(
      """
            CREATE TABLE IF NOT EXISTS test_table (
                id SERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL
            )
        """.trimIndent()
    )
  }

  @Test
  fun `验证 PostgreSQL 容器正常工作`() {
    // 插入测试数据
    jdbcTemplate.update("INSERT INTO test_table (name) VALUES (?)", "test_name")

    // 验证数据
    val result = jdbcTemplate.queryForObject(
      "SELECT name FROM test_table WHERE name = ?",
      String::class.java,
      "test_name"
    )
    assertEquals("test_name", result)
  }

  @Test
  fun `验证 Redis 容器正常工作`() {
    // 设置测试数据
    val key = "test:key"
    val value = "test_value"
    redisTemplate.opsForValue().set(key, value)

    // 验证数据
    val result = redisTemplate.opsForValue().get(key)
    assertEquals(value, result)
  }

  @Test
  fun `验证 MinIO 容器正常工作`() {
    // 创建测试桶
    val bucketName = "test-bucket"
    if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
    }

    // 验证桶是否创建成功
    val exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())
    assertTrue(exists, "测试桶应该存在")
  }

  @Test
  fun `验证所有容器能够同时正常工作`() {
    // PostgreSQL 测试
    jdbcTemplate.update("INSERT INTO test_table (name) VALUES (?)", "combined_test")

    // Redis 测试
    val redisKey = "combined:test"
    redisTemplate.opsForValue().set(redisKey, "combined_value")

    // MinIO 测试
    val bucketName = "combined-test-bucket"
    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())

    // 验证所有操作
    val pgResult = jdbcTemplate.queryForObject(
      "SELECT name FROM test_table WHERE name = ?",
      String::class.java,
      "combined_test"
    )
    val redisResult = redisTemplate.opsForValue().get(redisKey)
    val minioResult = minioClient.bucketExists(
      BucketExistsArgs.builder().bucket(bucketName).build()
    )

    assertEquals("combined_test", pgResult)
    assertEquals("combined_value", redisResult)
    assertTrue(minioResult)
  }
} 
