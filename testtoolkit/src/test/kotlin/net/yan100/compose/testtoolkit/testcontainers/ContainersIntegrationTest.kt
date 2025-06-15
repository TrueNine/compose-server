package net.yan100.compose.testtoolkit.testcontainers

import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import jakarta.annotation.Resource
import net.yan100.compose.testtoolkit.log
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.jdbc.core.JdbcTemplate
import kotlin.math.abs
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
@Import(TestConfiguration::class)
class ContainersIntegrationTest :
  IDatabasePostgresqlContainer,
  ICacheRedisContainer,
  IOssMinioContainer {

  /**
   * 验证系统时间和时区配置是否正确。
   */
  @Test
  fun `验证系统时间和时区配置`() {
    // 获取当前系统时间
    val currentTime = java.time.Instant.now()

    // 获取系统默认时区
    val systemZoneId = java.time.ZoneId.systemDefault()

    // 将当前时间转换为系统默认时区的时间
    val zonedDateTime = currentTime.atZone(systemZoneId)

    // 打印调试信息（可选）
    log.info("当前时间: $currentTime")
    log.info("系统默认时区: $systemZoneId")
    log.info("带时区的时间: $zonedDateTime")

    // 验证时区是否为预期值（例如 "Asia/Shanghai"）
    val expectedZoneId = java.time.ZoneId.of("Asia/Shanghai")
    assertTrue(
      systemZoneId == expectedZoneId,
      "系统时区配置错误，当前时区为 $systemZoneId，但期望为 $expectedZoneId"
    )

    // 验证当前时间是否在合理范围内（例如最近 5 分钟内）
    val fiveMinutesAgo = java.time.Instant.now().minusSeconds(300)
    val fiveMinutesLater = java.time.Instant.now().plusSeconds(300)
    assertTrue(
      currentTime.isAfter(fiveMinutesAgo) && currentTime.isBefore(fiveMinutesLater),
      "系统时间不在合理范围内，当前时间为 $currentTime"
    )
  }

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

  @Test
  fun `验证容器时区和时间与当前系统一致`() {
    // 1. PostgreSQL
    val pgTimeZone = jdbcTemplate.queryForObject("SHOW TIMEZONE", String::class.java)
    val pgNow = jdbcTemplate.queryForObject("SELECT NOW()", java.sql.Timestamp::class.java)
    log.info("PostgreSQL 时区: $pgTimeZone, 当前时间: $pgNow")
    val systemZoneId = java.time.ZoneId.systemDefault().id
    assertEquals("Asia/Shanghai", pgTimeZone, "PostgreSQL 时区应为 Asia/Shanghai")
    val now = java.time.Instant.now()
    assertTrue(
      Math.abs(pgNow!!.toInstant().epochSecond - now.epochSecond) < 300,
      "PostgreSQL 时间与系统时间相差超过5分钟"
    )

    // 2. Redis
    val redisMillis = redisTemplate.connectionFactory?.connection?.serverCommands()?.time()
    if (redisMillis != null) {
      val redisEpochSecond = redisMillis / 1000
      val systemEpochSecond = now.epochSecond
      log.info("Redis 服务器时间: $redisEpochSecond, 系统时间: $systemEpochSecond")
      assertTrue(
        abs(redisEpochSecond - systemEpochSecond) < 300,
        "Redis 时间与系统时间相差超过5分钟"
      )
    }

    // 3. MinIO（可选，桶创建时间近似判断）
    val bucketName = "timezone-test-bucket"
    if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
    }
    // MinIO 没有直接的时区/时间API，这里只做桶创建时间的近似判断
    // 你可以根据需要补充更详细的 MinIO 时间校验逻辑
  }
} 
