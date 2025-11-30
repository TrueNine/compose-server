package io.github.truenine.composeserver.testtoolkit.testcontainers

import jakarta.annotation.Resource
import kotlin.test.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.jdbc.core.JdbcTemplate

/**
 * Extension function feature tests.
 *
 * Verifies new capabilities of the refactored test-container interfaces:
 * - Single-container extension function usage
 * - Multi-container aggregation function usage
 * - Automatic container reset behavior
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
    fun `Redis extension function basic behavior`() =
      redis(resetToInitialState = true) { container ->
        // Verify container is running
        assertTrue(container.isRunning, "Redis container should be in running state")

        // Verify basic Redis operations
        redisTemplate.opsForValue().set("test:key", "test-value")
        val value = redisTemplate.opsForValue().get("test:key")
        assertEquals("test-value", value, "Redis read/write operations should work")

        // Verify container port mapping
        val mappedPort = container.getMappedPort(6379)
        assertTrue(mappedPort > 0, "Redis port mapping should be valid")
      }

    @Test
    fun `Redis extension function data reset behavior`() {
      // First call: set data
      redis(resetToInitialState = false) { _ ->
        redisTemplate.opsForValue().set("persistent:key", "persistent-value")
        assertTrue(redisTemplate.hasKey("persistent:key"), "Key should exist")
      }

      // Second call: enable reset, data should be cleared
      redis(resetToInitialState = true) { _ ->
        val exists = redisTemplate.hasKey("persistent:key")
        assertTrue(!exists, "After reset is enabled, the previous key should not exist")
      }
    }

    @Test
    fun `PostgreSQL extension function basic behavior`() =
      postgres(resetToInitialState = true) { container ->
        // Verify container is running
        assertTrue(container.isRunning, "PostgreSQL container should be in running state")

        // Create test table and insert data
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test_users (id SERIAL PRIMARY KEY, name VARCHAR(100))")
        jdbcTemplate.update("INSERT INTO test_users (name) VALUES (?)", "test-user")

        // Verify data operations
        val count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_users", Int::class.java)
        assertTrue(count!! > 0, "Should be able to insert and query data")

        // Verify container properties
        assertNotNull(container.jdbcUrl, "JDBC URL should exist")
        assertEquals("testdb", container.databaseName, "Database name should be correct")
      }

    @Test
    fun `MinIO extension function basic behavior`() =
      minio(resetToInitialState = true) { container ->
        // Verify container is running
        assertTrue(container.isRunning, "MinIO container should be in running state")

        // Verify port mapping
        val apiPort = container.getMappedPort(9000)
        val consolePort = container.getMappedPort(9001)
        assertTrue(apiPort > 0, "MinIO API port should be mapped correctly")
        assertTrue(consolePort > 0, "MinIO console port should be mapped correctly")

        // Verify environment variables
        val envMap = container.envMap
        assertEquals("minioadmin", envMap["MINIO_ROOT_USER"], "MinIO username should be correct")
        assertEquals("minioadmin", envMap["MINIO_ROOT_PASSWORD"], "MinIO password should be correct")
      }
  }

  @Nested
  inner class MultiContainerAggregationFunction {

    @Test
    fun `multi-container aggregation function basic behavior`() =
      containers(ICacheRedisContainer.redisContainerLazy, IDatabasePostgresqlContainer.postgresqlContainerLazy, IOssMinioContainer.minioContainerLazy) {
        // Verify that the context can access all containers
        val redisContainer = getRedisContainer()
        val postgresContainer = getPostgresContainer()
        val minioContainer = getMinioContainer()

        assertNotNull(redisContainer, "Redis container should exist")
        assertNotNull(postgresContainer, "PostgreSQL container should exist")
        assertNotNull(minioContainer, "MinIO container should exist")

        // Verify that all containers are running
        assertTrue(redisContainer!!.isRunning, "Redis container should be in running state")
        assertTrue(postgresContainer!!.isRunning, "PostgreSQL container should be in running state")
        assertTrue(minioContainer!!.isRunning, "MinIO container should be in running state")

        // Verify getAllContainers behavior
        val allContainers = getAllContainers()
        assertEquals(3, allContainers.size, "There should be 3 containers")
      }

    @Test
    fun `cross-container integration scenario`() =
      containers(ICacheRedisContainer.redisContainerLazy, IDatabasePostgresqlContainer.postgresqlContainerLazy) {
        val redis = getRedisContainer()
        val postgres = getPostgresContainer()

        assertNotNull(redis, "Redis container should exist")
        assertNotNull(postgres, "PostgreSQL container should exist")

        // Create database table
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, name VARCHAR(100))")

        // Create data in PostgreSQL
        jdbcTemplate.update("INSERT INTO users (name) VALUES (?)", "john")
        val userId = jdbcTemplate.queryForObject("SELECT id FROM users WHERE name = ?", Int::class.java, "john")
        assertNotNull(userId, "Should be able to obtain user id")

        // Cache user information in Redis
        redisTemplate.opsForValue().set("user:$userId", "john")
        val cachedName = redisTemplate.opsForValue().get("user:$userId")
        assertEquals("john", cachedName, "Redis should return the cached username")

        // Verify cross-container data consistency
        val dbUser = jdbcTemplate.queryForObject("SELECT name FROM users WHERE id = ?", String::class.java, userId)
        assertEquals(cachedName, dbUser, "Database and cache usernames should be consistent")
      }
  }

  @Nested
  inner class BackwardCompatibilityTests {

    @Test
    fun `containers accessed via scope functions`() {
      // Containers can only be accessed via scope functions; container properties are not exposed directly
      redis { redisContainer -> assertTrue(redisContainer.isRunning, "Redis container should run correctly via scope function") }

      postgres { postgresContainer -> assertTrue(postgresContainer.isRunning, "PostgreSQL container should run correctly via scope function") }

      minio { minioContainer -> assertTrue(minioContainer.isRunning, "MinIO container should run correctly via scope function") }
    }

    @Test
    fun `verify traditional Spring configuration injection still works`() {
      // Spring auto-configured JdbcTemplate and RedisTemplate should still be usable
      assertNotNull(jdbcTemplate, "JdbcTemplate should be injected correctly")
      assertNotNull(redisTemplate, "RedisTemplate should be injected correctly")

      // Verify basic operations
      val version = jdbcTemplate.queryForObject("SELECT version()", String::class.java)
      assertNotNull(version, "Should be able to execute database query")
      assertTrue(version.contains("PostgreSQL"), "Database should be PostgreSQL")

      redisTemplate.opsForValue().set("traditional:test", "works")
      val value = redisTemplate.opsForValue().get("traditional:test")
      assertEquals("works", value, "Redis operations should work")
    }
  }
}
