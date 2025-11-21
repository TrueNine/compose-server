package itest.integrate.cacheable.factory

import io.github.truenine.composeserver.testtoolkit.testcontainers.ICacheRedisContainer
import jakarta.annotation.Resource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@SpringBootTest
class RedisConnectionFactoryIntegrationTest : ICacheRedisContainer {

  @Resource lateinit var factory: RedisConnectionFactory

  @Test
  fun `验证工厂使用最新的RESP3协议连接`() {
    val lettuceFactory = assertIs<LettuceConnectionFactory>(factory)
    val connection = lettuceFactory.connection
    try {
      val pingResult = connection.ping()
      assertEquals("PONG", pingResult)
      val clientOptions = lettuceFactory.clientConfiguration.clientOptions.orElse(null)
      assertNotNull(clientOptions, "客户端选项不应为空")
      val protocolVersion = clientOptions.protocolVersion
      assertEquals("RESP3", protocolVersion.toString(), "应该使用最新的 RESP3 协议")
    } finally {
      connection.close()
    }
  }
}
