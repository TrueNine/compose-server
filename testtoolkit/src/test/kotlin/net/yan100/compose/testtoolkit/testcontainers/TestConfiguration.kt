package net.yan100.compose.testtoolkit.testcontainers

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate

/**
 * 测试配置类
 *
 * 提供测试所需的 Spring Bean 配置
 */
@TestConfiguration
open class TestConfiguration {

  /**
   * 配置 StringRedisTemplate
   *
   * @param connectionFactory Redis 连接工厂
   * @return StringRedisTemplate 实例
   */
  @Bean
  open fun redisTemplate(connectionFactory: RedisConnectionFactory): StringRedisTemplate {
    return StringRedisTemplate().apply {
      setConnectionFactory(connectionFactory)
    }
  }
} 
