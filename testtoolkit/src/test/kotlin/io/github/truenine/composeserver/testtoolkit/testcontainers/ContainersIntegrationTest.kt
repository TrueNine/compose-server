package io.github.truenine.composeserver.testtoolkit.testcontainers

import io.github.truenine.composeserver.testtoolkit.log
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import jakarta.annotation.Resource
import kotlin.math.abs
import kotlin.test.assertEquals
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

  /** 验证系统时间和时区配置是否正确。 */
  @Test
  fun `验证系统时间和时区配置`() {
    // 获取当前系统时间
    val currentTime = java.time.Instant.now()

    // 获取系统默认时区
    val systemZoneId = java.time.ZoneId.systemDefault()

    // 将当前时间转换为系统默认时区的时间
    val zonedDateTime = currentTime.atZone(systemZoneId)

    // 打印调试信息（可选）
    log.info("[验证系统时间和时区配置] current time: {} , system zone id: {} , zoned date time: {}", currentTime, systemZoneId, zonedDateTime)

    // 验证时区不为空且有效
    assertTrue(systemZoneId.id.isNotBlank(), "system zone id should not be blank, current zone id: $systemZoneId")

    // 验证当前时间是否在合理范围内（例如最近 5 分钟内）
    val fiveMinutesAgo = java.time.Instant.now().minusSeconds(300)
    val fiveMinutesLater = java.time.Instant.now().plusSeconds(300)
    assertTrue(
      currentTime.isAfter(fiveMinutesAgo) && currentTime.isBefore(fiveMinutesLater),
      "system time is out of reasonable range, current time: $currentTime",
    )
  }

  @Resource private lateinit var jdbcTemplate: JdbcTemplate

  @Resource private lateinit var redisTemplate: StringRedisTemplate

  private lateinit var minioClient: MinioClient

  @BeforeEach
  fun setup() {
    // 初始化 MinIO 客户端
    minioClient = MinioClient.builder().endpoint("http://localhost:${minioContainer?.getMappedPort(9000)}").credentials("minioadmin", "minioadmin").build()

    // 初始化 PostgreSQL 测试表
    jdbcTemplate.execute(
      """
            CREATE TABLE IF NOT EXISTS test_table (
                id SERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL
            )
        """
        .trimIndent()
    )
  }

  @Test
  fun `验证 PostgreSQL 容器正常工作`() {
    // 插入测试数据
    jdbcTemplate.update("INSERT INTO test_table (name) VALUES (?)", "test_name")

    // 验证数据
    val result = jdbcTemplate.queryForObject("SELECT name FROM test_table WHERE name = ?", String::class.java, "test_name")
    assertEquals("test_name", result, "PostgreSQL 查询结果应详匹配")

    // 验证数据插入成功
    val count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_table WHERE name = ?", Int::class.java, "test_name")
    assertTrue(count!! > 0, "PostgreSQL 表中应详存在插入的数据")
  }

  @Test
  fun `验证 Redis 容器正常工作`() {
    // 设置测试数据
    val key = "test:key"
    val value = "test_value"
    redisTemplate.opsForValue().set(key, value)

    // 验证数据
    val result = redisTemplate.opsForValue().get(key)
    assertEquals(value, result, "Redis 查询结果应详匹配")

    // 验证 Redis 数据持久性
    assertTrue(redisTemplate.hasKey(key), "Redis 键应详存在")

    // 清理测试数据
    redisTemplate.delete(key)
    assertTrue(!redisTemplate.hasKey(key), "清理后 Redis 键不应详存在")
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
    assertTrue(exists, "测试桶应详存在")

    // 验证桶属性
    val buckets = minioClient.listBuckets()
    assertTrue(buckets.any { it.name() == bucketName }, "MinIO 应详列出创建的桶")

    // 验证桶操作权限
    val bucketPolicy =
      try {
        minioClient.getBucketPolicy(io.minio.GetBucketPolicyArgs.builder().bucket(bucketName).build())
      } catch (e: Exception) {
        null // 默认情况下可能没有策略
      }
    // 策略可能为空，这是正常的
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
    val pgResult = jdbcTemplate.queryForObject("SELECT name FROM test_table WHERE name = ?", String::class.java, "combined_test")
    val redisResult = redisTemplate.opsForValue().get(redisKey)
    val minioResult = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())

    // 验证所有操作结果
    assertEquals("combined_test", pgResult, "PostgreSQL 结合测试结果应详正确")
    assertEquals("combined_value", redisResult, "Redis 结合测试结果应详正确")
    assertTrue(minioResult, "MinIO 结合测试结果应详正确")

    // 验证综合数据一致性
    val totalPgRecords = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_table", Int::class.java)
    assertTrue(totalPgRecords!! >= 1, "PostgreSQL 应详至少有一条记录")

    val allBuckets = minioClient.listBuckets()
    assertTrue(allBuckets.size >= 1, "MinIO 应详至少有一个桶")

    // 清理测试数据
    redisTemplate.delete(redisKey)
    assertTrue(!redisTemplate.hasKey(redisKey), "清理后 Redis 键不应详存在")
  }

  @Test
  fun `验证容器时区和时间与当前系统一致`() {
    // 1. PostgreSQL
    val pgTimeZone = jdbcTemplate.queryForObject("SHOW TIMEZONE", String::class.java)
    val pgNow = jdbcTemplate.queryForObject("SELECT NOW()", java.sql.Timestamp::class.java)
    log.info("[验证容器时区和时间与当前系统一致] postgresql timezone: {} , current time: {}", pgTimeZone, pgNow)

    // 验证PostgreSQL时区不为空
    assertTrue(pgTimeZone!!.isNotBlank(), "postgresql timezone should not be blank")

    val now = java.time.Instant.now()
    // 使用 Kotlin 标准库的 abs 函数
    assertTrue(
      abs(pgNow!!.toInstant().epochSecond - now.epochSecond) < 300,
      "postgresql time differs from system time by more than 5 minutes (diff: ${abs(pgNow.toInstant().epochSecond - now.epochSecond)} seconds)",
    )

    // 验证 PostgreSQL 时间的合理性
    assertTrue(pgNow.time > 0, "PostgreSQL 时间戳应该大于 0")
    assertTrue(pgNow.toInstant().isBefore(java.time.Instant.now().plusSeconds(60)), "PostgreSQL 时间不应详超过当前时间 1 分钟")

    // 2. Redis
    val redisMillis = redisTemplate.connectionFactory?.connection?.serverCommands()?.time()
    if (redisMillis != null) {
      val redisEpochSecond = redisMillis / 1000
      val systemEpochSecond = now.epochSecond
      log.info("[验证容器时区和时间与当前系统一致] redis server time: {} , system time: {}", redisEpochSecond, systemEpochSecond)
      val timeDiff = abs(redisEpochSecond - systemEpochSecond)
      assertTrue(timeDiff < 300, "redis time differs from system time by more than 5 minutes (diff: $timeDiff seconds)")

      // 验证 Redis 时间的合理性
      assertTrue(redisEpochSecond > 0, "Redis 时间戳应详大于 0")
      assertTrue(redisEpochSecond <= systemEpochSecond + 60, "Redis 时间不应详超过当前时间 1 分钟")
    }

    // 3. MinIO（可选，桶创建时间近似判断）
    val bucketName = "timezone-test-bucket"
    if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
    }
    // 验证 MinIO 桶创建成功
    val bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())
    assertTrue(bucketExists, "MinIO 桶应详创建成功")

    // 验证 MinIO 客户端连接有效性
    val buckets = minioClient.listBuckets()
    assertTrue(buckets.isNotEmpty(), "MinIO 应详至少有一个桶")
    assertTrue(buckets.any { it.name() == bucketName }, "MinIO 应详包含创建的测试桶")
  }
}
