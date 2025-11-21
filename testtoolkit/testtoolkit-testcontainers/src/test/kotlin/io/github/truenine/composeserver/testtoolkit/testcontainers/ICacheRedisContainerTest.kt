package io.github.truenine.composeserver.testtoolkit.testcontainers

import jakarta.annotation.Resource
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

/**
 * Redis test container integration tests.
 *
 * Verifies the configuration and runtime behavior of the Redis test
 * container, ensuring that:
 * - Container starts and runs correctly.
 * - Port mappings are configured correctly.
 * - Redis connection configuration is correct.
 * - Spring properties are injected correctly.
 *
 * Coverage:
 * - Basic container behavior tests.
 * - Port mapping verification.
 * - Redis connection verification.
 * - Spring property injection verification.
 *
 * Usage:
 *
 * ```kotlin
 * @SpringBootTest
 * class YourTestClass : ICacheRedisContainer {
 *   // your test code
 * }
 * ```
 *
 * @see ICacheRedisContainer
 * @author TrueNine
 * @since 2024-04-24
 */
@SpringBootTest
@EnableAutoConfiguration(
  excludeName =
    [
      "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
      "org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration",
      "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
    ]
)
class ICacheRedisContainerTest : ICacheRedisContainer {
  lateinit var environment: Environment
    @Resource set

  lateinit var redisConnectionFactory: RedisConnectionFactory
    @Resource set

  lateinit var redisTemplate: RedisTemplate<String, String>
    @Resource set

  @Test
  fun containerShouldBeRunning() {
    redis { container -> assertTrue(container.isRunning) }
  }

  @Test
  fun containerShouldHaveValidPortMapping() {
    redis { container ->
      val redisPort = container.getMappedPort(6379)
      assertTrue(redisPort in 1024..65535)
    }
  }

  @Test
  fun redisConnectionShouldWork() {
    assertNotNull(redisConnectionFactory)
    val connection = redisConnectionFactory.connection
    connection.use { conn ->
      conn.ping()
      assertTrue(conn.isClosed.not())
    }
  }

  @Test
  fun redisBasicOperationsShouldWork() {
    val key = "test:key"
    val value = "test-value"

    redisTemplate.opsForValue().set(key, value)
    val retrievedValue = redisTemplate.opsForValue().get(key)
    assertEquals(value, retrievedValue)

    redisTemplate.delete(key)
  }

  @Test
  fun redisExpirationShouldWork() {
    val key = "test:expiring:key"
    redisTemplate.opsForValue().set(key, "test-value")
    redisTemplate.expire(key, Duration.ofSeconds(2))

    val ttl = redisTemplate.getExpire(key)
    assertTrue(ttl > 0 && ttl <= 2)

    Thread.sleep(2100)
    assertFalse(redisTemplate.hasKey(key))
  }

  @Test
  fun springPropertiesShouldBeInjected() {
    redis { container ->
      val host = environment.getProperty("spring.data.redis.host")
      val port = environment.getProperty("spring.data.redis.port")

      assertEquals(container.host, host)
      assertNotNull(port)
      assertTrue(port!!.toInt() in 1024..65535)
    }
  }
}
