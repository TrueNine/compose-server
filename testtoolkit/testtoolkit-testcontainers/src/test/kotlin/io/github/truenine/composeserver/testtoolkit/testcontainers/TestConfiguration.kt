package io.github.truenine.composeserver.testtoolkit.testcontainers

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate

/**
 * Test configuration class.
 *
 * Provides Spring bean configuration required for tests.
 */
@TestConfiguration
open class TestConfiguration {

  /**
   * Configures the StringRedisTemplate.
   *
   * @param connectionFactory Redis connection factory
   * @return StringRedisTemplate instance
   */
  @Bean
  open fun redisTemplate(connectionFactory: RedisConnectionFactory): StringRedisTemplate {
    return StringRedisTemplate().apply { setConnectionFactory(connectionFactory) }
  }
}
