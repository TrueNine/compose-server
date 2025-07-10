package net.yan100.compose.testtoolkit.testcontainers

import jakarta.annotation.Resource
import java.util.function.Supplier
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.DisplayName
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
  @DisplayName("容器基本功能测试")
  inner class ContainerBasicTests {
    @Test
    @DisplayName("验证容器实例存在且正在运行")
    fun `验证容器实例存在且正在运行`() {
      assertNotNull(redisContainer, "Redis 容器实例不应为空")
      assertTrue(redisContainer!!.isRunning, "Redis 容器应该处于运行状态")
    }

    @Test
    @DisplayName("验证容器网络配置正确")
    fun `验证容器网络配置正确`() {
      val container = redisContainer!!

      // 验证端口映射
      val redisPort = container.getMappedPort(6379)
      assertTrue(redisPort in 1024..65535, "Redis 端口映射应在有效范围内")
    }

    @Test
    @DisplayName("验证容器日志输出正确")
    fun `验证容器日志输出正确`() {
      val container = redisContainer!!
      val logs = container.logs

      assertTrue(logs.contains("Ready to accept connections"), "Redis 容器日志应包含启动成功信息")
    }
  }

  @Nested
  @DisplayName("Redis 连接测试")
  inner class RedisConnectionTests {
    @Test
    @DisplayName("验证 Redis 连接工厂配置正确")
    fun `验证 Redis 连接工厂配置正确`() {
      assertNotNull(redisConnectionFactory, "Redis 连接工厂不应为空")

      val connection = redisConnectionFactory.connection
      assertNotNull(connection, "应该能够获取 Redis 连接")

      connection.use { conn -> assertTrue(conn.isClosed.not(), "Redis 连接应该是开启的") }
    }

    @Test
    @DisplayName("验证 Redis 基本操作正常")
    fun `验证 Redis 基本操作正常`() {
      // 测试字符串操作
      val key = "test:key"
      val value = "test-value"

      redisTemplate.opsForValue().set(key, value)
      val retrievedValue = redisTemplate.opsForValue().get(key)

      assertEquals(value, retrievedValue, "Redis 读写操作应该正常")

      // 清理测试数据
      redisTemplate.delete(key)
    }

    @Test
    @DisplayName("验证 Redis 过期时间设置正常")
    fun `验证 Redis 过期时间设置正常`() {
      val key = "test:expiring:key"
      val value = "test-value"

      redisTemplate.opsForValue().set(key, value)
      val exists = redisTemplate.hasKey(key)

      assertTrue(exists, "键应该存在")

      // 清理测试数据
      redisTemplate.delete(key)
    }
  }

  @Nested
  @DisplayName("Spring 属性注入测试")
  inner class SpringPropertiesTests {
    @Test
    @DisplayName("验证动态属性注册正确")
    fun `验证动态属性注册正确`() {
      val registry = mutableMapOf<String, String>()
      val mockRegistry =
        object : DynamicPropertyRegistry {
          override fun add(name: String, valueSupplier: Supplier<in Any>) {
            registry[name] = valueSupplier.get().toString()
          }
        }

      ICacheRedisContainer.properties(mockRegistry)

      // 验证所有必需的属性都已配置
      val expectedProperties =
        mapOf("spring.data.redis.host" to redisContainer!!.host, "spring.data.redis.port" to redisContainer!!.getMappedPort(6379).toString())

      expectedProperties.forEach { (prop, expectedValue) ->
        assertTrue(registry.containsKey(prop), "property $prop must exist")
        assertEquals(expectedValue, registry[prop], "property $prop value is incorrect")
      }
    }

    @Test
    @DisplayName("验证环境变量注入正确")
    fun `验证环境变量注入正确`() {
      val expectedProperties = mapOf("spring.data.redis.host" to redisContainer!!.host)

      expectedProperties.forEach { (prop, expectedValue) ->
        val actualValue = environment.getProperty(prop)
        assertNotNull(actualValue, "environment variable missing property: $prop")
        assertEquals(expectedValue, actualValue, "environment variable $prop value is incorrect")
      }

      // 特殊验证端口属性（因为端口是动态分配的）
      val portValue = environment.getProperty("spring.data.redis.port")
      assertNotNull(portValue, "environment variable missing port configuration")
      assertTrue(portValue.toInt() in 1024..65535, "port value should be in valid range")
    }
  }
}
