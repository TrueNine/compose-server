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
  fun `Factory should use latest RESP3 protocol for connections`() {
    val lettuceFactory = assertIs<LettuceConnectionFactory>(factory)
    val connection = lettuceFactory.connection
    try {
      val pingResult = connection.ping()
      assertEquals("PONG", pingResult)
      val clientOptions = lettuceFactory.clientConfiguration.clientOptions.orElse(null)
      assertNotNull(clientOptions, "Client options should not be null")
      val protocolVersion = clientOptions.protocolVersion
      assertEquals("RESP3", protocolVersion.toString(), "The latest RESP3 protocol should be used")
    } finally {
      connection.close()
    }
  }
}
