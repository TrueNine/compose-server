package itest.integrate.cacheable.factory

import io.github.truenine.composeserver.testtoolkit.testcontainers.ICacheRedisContainer
import jakarta.annotation.Resource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
class RedisConnectionFactoryIntegrationTest : ICacheRedisContainer {

  @Resource
  lateinit var factory: RedisConnectionFactory

  @Test
  fun `验证工厂使用最新的RESP3协议连接`() {
    val lettuceFactory = assertIs<LettuceConnectionFactory>(factory)
    
    // 验证连接成功
    val connection = lettuceFactory.connection
    try {
      val pingResult = connection.ping()
      assertEquals("PONG", pingResult)
      
      // 获取客户端选项并验证协议版本
      val clientOptions = lettuceFactory.clientConfiguration.clientOptions.orElse(null)
      assertNotNull(clientOptions, "客户端选项不应为空")
      
      // 验证协议版本为 RESP3
      val protocolVersion = clientOptions.protocolVersion
      assertEquals("RESP3", protocolVersion.toString(), "应该使用最新的 RESP3 协议")
      
    } finally {
      connection.close()
    }
  }
}
