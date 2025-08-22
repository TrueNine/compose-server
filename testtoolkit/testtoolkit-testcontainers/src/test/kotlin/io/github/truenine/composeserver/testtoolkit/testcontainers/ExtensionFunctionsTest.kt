package io.github.truenine.composeserver.testtoolkit.testcontainers

import jakarta.annotation.Resource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.jdbc.core.JdbcTemplate

/**
 * # 扩展函数功能测试
 *
 * 该测试类验证重构后的测试容器接口的新功能：
 * - 单容器扩展函数的使用
 * - 多容器聚合函数的使用
 * - 容器自动重置功能的验证
 *
 * @author TrueNine
 * @since 2025-08-09
 */
@SpringBootTest
@Import(TestConfiguration::class)
class ExtensionFunctionsTest : ICacheRedisContainer, IDatabasePostgresqlContainer, IOssMinioContainer {

  lateinit var jdbcTemplate: JdbcTemplate
    @Resource set

  lateinit var redisTemplate: StringRedisTemplate
    @Resource set

  @Nested
  inner class SingleContainerExtensionFunctions {

    @Test
    fun `测试 Redis 扩展函数基本功能`() =
      redis(resetToInitialState = true) { container ->
        // 验证容器正在运行
        assertTrue(container.isRunning, "Redis 容器应该处于运行状态")

        // 验证 Redis 基本操作
        redisTemplate.opsForValue().set("test:key", "test-value")
        val value = redisTemplate.opsForValue().get("test:key")
        assertEquals("test-value", value, "Redis 读写操作应该正常")

        // 验证容器端口映射
        val mappedPort = container.getMappedPort(6379)
        assertTrue(mappedPort > 0, "Redis 端口映射应该有效")
      }

    @Test
    fun `测试 Redis 扩展函数数据重置功能`() {
      // 第一次调用，设置数据
      redis(resetToInitialState = false) { _ ->
        redisTemplate.opsForValue().set("persistent:key", "persistent-value")
        assertTrue(redisTemplate.hasKey("persistent:key"), "键应该存在")
      }

      // 第二次调用，启用重置，数据应该被清空
      redis(resetToInitialState = true) { _ ->
        val exists = redisTemplate.hasKey("persistent:key")
        assertTrue(!exists, "启用重置后，之前的键应该不存在")
      }
    }

    @Test
    fun `测试 PostgreSQL 扩展函数基本功能`() =
      postgres(resetToInitialState = true) { container ->
        // 验证容器正在运行
        assertTrue(container.isRunning, "PostgreSQL 容器应该处于运行状态")

        // 创建测试表并插入数据
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test_users (id SERIAL PRIMARY KEY, name VARCHAR(100))")
        jdbcTemplate.update("INSERT INTO test_users (name) VALUES (?)", "test-user")

        // 验证数据操作
        val count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_users", Int::class.java)
        assertTrue(count!! > 0, "应该能够插入和查询数据")

        // 验证容器属性
        assertNotNull(container.jdbcUrl, "JDBC URL 应该存在")
        assertEquals("testdb", container.databaseName, "数据库名称应该正确")
      }

    @Test
    fun `测试 MinIO 扩展函数基本功能`() =
      minio(resetToInitialState = true) { container ->
        // 验证容器正在运行
        assertTrue(container.isRunning, "MinIO 容器应该处于运行状态")

        // 验证端口映射
        val apiPort = container.getMappedPort(9000)
        val consolePort = container.getMappedPort(9001)
        assertTrue(apiPort > 0, "MinIO API 端口应该被正确映射")
        assertTrue(consolePort > 0, "MinIO 控制台端口应该被正确映射")

        // 验证环境变量
        val envMap = container.envMap
        assertEquals("minioadmin", envMap["MINIO_ROOT_USER"], "MinIO 用户名应该正确")
        assertEquals("minioadmin", envMap["MINIO_ROOT_PASSWORD"], "MinIO 密码应该正确")
      }
  }

  @Nested
  inner class MultiContainerAggregationFunction {

    @Test
    fun `测试多容器聚合函数基本功能`() =
      containers(ICacheRedisContainer.redisContainerLazy, IDatabasePostgresqlContainer.postgresqlContainerLazy, IOssMinioContainer.minioContainerLazy) {
        // 验证上下文中能够访问所有容器
        val redisContainer = getRedisContainer()
        val postgresContainer = getPostgresContainer()
        val minioContainer = getMinioContainer()

        assertNotNull(redisContainer, "Redis 容器应该存在")
        assertNotNull(postgresContainer, "PostgreSQL 容器应该存在")
        assertNotNull(minioContainer, "MinIO 容器应该存在")

        // 验证所有容器都在运行
        assertTrue(redisContainer!!.isRunning, "Redis 容器应该处于运行状态")
        assertTrue(postgresContainer!!.isRunning, "PostgreSQL 容器应该处于运行状态")
        assertTrue(minioContainer!!.isRunning, "MinIO 容器应该处于运行状态")

        // 验证 getAllContainers 功能
        val allContainers = getAllContainers()
        assertEquals(3, allContainers.size, "应该有3个容器")
      }

    @Test
    fun `测试跨容器集成场景`() =
      containers(ICacheRedisContainer.redisContainerLazy, IDatabasePostgresqlContainer.postgresqlContainerLazy) {
        val redis = getRedisContainer()
        val postgres = getPostgresContainer()

        assertNotNull(redis, "Redis 容器应该存在")
        assertNotNull(postgres, "PostgreSQL 容器应该存在")

        // 创建数据库表
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, name VARCHAR(100))")

        // 在 PostgreSQL 中创建数据
        jdbcTemplate.update("INSERT INTO users (name) VALUES (?)", "john")
        val userId = jdbcTemplate.queryForObject("SELECT id FROM users WHERE name = ?", Int::class.java, "john")
        assertNotNull(userId, "应该能够获取用户ID")

        // 在 Redis 中缓存用户信息
        redisTemplate.opsForValue().set("user:$userId", "john")
        val cachedName = redisTemplate.opsForValue().get("user:$userId")
        assertEquals("john", cachedName, "Redis 中应该能够获取缓存的用户名")

        // 验证跨容器数据一致性
        val dbUser = jdbcTemplate.queryForObject("SELECT name FROM users WHERE id = ?", String::class.java, userId)
        assertEquals(cachedName, dbUser, "数据库和缓存中的用户名应该一致")
      }
  }

  @Nested
  inner class BackwardCompatibilityTests {

    @Test
    fun `验证容器通过 scope 函数访问`() {
      // 容器只能通过 scope 函数访问，无法直接获取容器属性
      redis { redisContainer -> assertTrue(redisContainer.isRunning, "Redis 容器应该通过 scope 函数正常运行") }

      postgres { postgresContainer -> assertTrue(postgresContainer.isRunning, "PostgreSQL 容器应该通过 scope 函数正常运行") }

      minio { minioContainer -> assertTrue(minioContainer.isRunning, "MinIO 容器应该通过 scope 函数正常运行") }
    }

    @Test
    fun `验证传统 Spring 配置注入依然工作`() {
      // Spring 自动配置的 JdbcTemplate 和 RedisTemplate 应该依然可用
      assertNotNull(jdbcTemplate, "JdbcTemplate 应该被正确注入")
      assertNotNull(redisTemplate, "RedisTemplate 应该被正确注入")

      // 验证基本操作
      val version = jdbcTemplate.queryForObject("SELECT version()", String::class.java)
      assertNotNull(version, "应该能够执行数据库查询")
      assertTrue(version.contains("PostgreSQL"), "应该是 PostgreSQL 数据库")

      redisTemplate.opsForValue().set("traditional:test", "works")
      val value = redisTemplate.opsForValue().get("traditional:test")
      assertEquals("works", value, "Redis 操作应该正常")
    }
  }
}
