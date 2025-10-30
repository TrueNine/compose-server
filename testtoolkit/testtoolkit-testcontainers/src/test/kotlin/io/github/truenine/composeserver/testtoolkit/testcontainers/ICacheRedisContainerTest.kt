package io.github.truenine.composeserver.testtoolkit.testcontainers

import jakarta.annotation.Resource
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

/**
 * # Redis 测试容器集成测试
 *
 * 该测试类验证 Redis 测试容器的配置和运行状态，确保：
 * - 容器正确启动和运行
 * - 端口映射配置正确
 * - Redis 连接配置正确
 * - Spring 属性注入正确
 *
 * ## 测试覆盖范围
 * - 容器基本功能测试
 * - 端口映射验证
 * - Redis 连接验证
 * - Spring 属性注入验证
 *
 * ## 使用方式
 *
 * ```kotlin
 * @SpringBootTest
 * class YourTestClass : ICacheRedisContainer {
 *   // 你的测试代码
 * }
 * ```
 *
 * @see ICacheRedisContainer
 * @author TrueNine
 * @since 2024-04-24
 */
@SpringBootTest
@EnableAutoConfiguration(
  exclude = [DataSourceAutoConfiguration::class, DataSourceTransactionManagerAutoConfiguration::class, HibernateJpaAutoConfiguration::class]
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
