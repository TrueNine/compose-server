package io.github.truenine.composeserver.testtoolkit.testcontainers

import jakarta.annotation.Resource
import java.net.InetSocketAddress
import java.net.Socket
import java.time.Duration
import java.util.function.Supplier
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.DynamicPropertyRegistry

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

  @Nested
  inner class ContainerBasicTests {
    @Test
    fun verify_container_instance_exists_and_running() {
      redis { container -> assertTrue(container.isRunning, "Redis 容器应该处于运行状态") }
    }

    @Test
    fun verify_container_network_configuration() {
      redis { container ->
        // 验证端口映射
        val redisPort = container.getMappedPort(6379)
        assertTrue(redisPort in 1024..65535, "Redis 端口映射应在有效范围内")
      }
    }

    @Test
    fun verify_container_log_output() {
      redis { container ->
        val logs = container.logs
        assertTrue(logs.contains("Ready to accept connections"), "Redis 容器日志应包含启动成功信息")
      }
    }
  }

  @Nested
  inner class RedisConnectionTests {
    @Test
    fun verify_redis_connection_factory_configuration() {
      assertNotNull(redisConnectionFactory, "Redis 连接工厂不应为空")

      val connection = redisConnectionFactory.connection
      assertNotNull(connection, "应该能够获取 Redis 连接")

      connection.use { conn ->
        assertTrue(conn.isClosed.not(), "Redis 连接应该是开启的")

        // 验证连接响应时间
        val startTime = System.currentTimeMillis()
        conn.ping()
        val responseTime = System.currentTimeMillis() - startTime
        assertTrue(responseTime < 1000, "Redis 连接响应时间应小于 1 秒 (实际: ${responseTime}ms)")

        // 验证连接有效性（通过基本操作验证）
        assertNotNull(conn, "连接对象不应为null")
        // ping 操作已经证明连接有效，无需额外验证
        Unit
      }
    }

    @Test
    fun verify_redis_basic_operations() {
      // 测试字符串操作
      val key = "test:key"
      val value = "test-value"

      redisTemplate.opsForValue().set(key, value)
      val retrievedValue = redisTemplate.opsForValue().get(key)

      assertEquals(value, retrievedValue, "Redis 读写操作应该正常")

      // 验证数据持久性
      assertTrue(redisTemplate.hasKey(key), "读写操作后键应该存在")

      // 清理测试数据
      val deleteResult = redisTemplate.delete(key)
      assertTrue(deleteResult, "删除操作应该成功")
    }

    @Test
    fun verify_redis_expiration_time_setting() {

      val key = "test:expiring:key"
      val value = "test-value"

      redisTemplate.opsForValue().set(key, value)

      // 设置过期时间
      redisTemplate.expire(key, Duration.ofSeconds(2))

      val exists = redisTemplate.hasKey(key)
      assertTrue(exists, "键应该存在")

      // 验证过期时间设置
      val ttl = redisTemplate.getExpire(key)
      assertTrue(ttl > 0, "过期时间应该大于 0 (实际: ${ttl} 秒)")
      assertTrue(ttl <= 2, "过期时间应该小于等于 2 秒 (实际: ${ttl} 秒)")

      // 等待过期并验证
      Thread.sleep(2100)
      val existsAfterExpiry = redisTemplate.hasKey(key)
      assertTrue(!existsAfterExpiry, "过期键应该不存在")
    }
  }

  @Nested
  inner class SpringPropertiesTests {
    @Test
    fun verify_dynamic_property_registration() {
      val registry = mutableMapOf<String, String>()
      val mockRegistry =
        object : DynamicPropertyRegistry {
          override fun add(name: String, valueSupplier: Supplier<in Any>) {
            registry[name] = valueSupplier.get().toString()
          }
        }

      ICacheRedisContainer.properties(mockRegistry)

      // 验证所有必需的属性都已配置
      redis { container ->
        val expectedProperties = mapOf("spring.data.redis.host" to container.host, "spring.data.redis.port" to container.getMappedPort(6379).toString())

        expectedProperties.forEach { (prop, expectedValue) ->
          assertTrue(registry.containsKey(prop), "property $prop must exist")
          assertEquals(expectedValue, registry[prop], "property $prop value is incorrect")
        }
      }
    }

    @Test
    fun verify_environment_variable_injection() {
      redis { container ->
        val expectedProperties = mapOf("spring.data.redis.host" to container.host)

        expectedProperties.forEach { (prop, expectedValue) ->
          val actualValue = environment.getProperty(prop)
          assertNotNull(actualValue, "environment variable missing property: $prop")
          assertEquals(expectedValue, actualValue, "environment variable $prop value is incorrect")
        }
      }

      // 特殊验证端口属性（因为端口是动态分配的）
      val portValue = environment.getProperty("spring.data.redis.port")
      assertNotNull(portValue, "environment variable missing port configuration")

      val portInt = portValue.toInt()
      assertTrue(portInt in 1024..65535, "port value should be in valid range (actual: $portInt)")
      val socket = Socket()
      try {
        socket.connect(InetSocketAddress("localhost", portInt), 5000)
        assertTrue(socket.isConnected, "端口应该可访问")
      } finally {
        socket.close()
      }
    }
  }
}
