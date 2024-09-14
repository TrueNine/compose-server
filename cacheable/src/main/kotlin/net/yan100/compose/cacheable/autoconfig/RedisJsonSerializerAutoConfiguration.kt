/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.cacheable.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.consts.ICacheNames
import net.yan100.compose.core.slf4j
import net.yan100.compose.depend.jackson.autoconfig.JacksonAutoConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

private val log = slf4j<RedisJsonSerializerAutoConfiguration>()

/**
 * redis 缓存组件配置
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@Configuration
class RedisJsonSerializerAutoConfiguration(
  @Qualifier(JacksonAutoConfiguration.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME)
  objectMapper: ObjectMapper
) {
  private val jsr = Jackson2JsonRedisSerializer(objectMapper, Any::class.java)

  private val srs = StringRedisSerializer()
  private val cacheManagerConfig =
    RedisCacheConfiguration.defaultCacheConfig()
      .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(srs))
      .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsr))
      .disableCachingNullValues()

  @Primary
  @Bean(name = [ICacheNames.IRedis.HANDLE])
  fun customRedisJsonSerializable(factory: RedisConnectionFactory): RedisTemplate<String, *> {
    log.trace("注册 redisTemplate factory = {}", factory)
    val rt = RedisTemplate<String, Any?>()
    rt.setDefaultSerializer(jsr)
    rt.hashKeySerializer = srs
    rt.keySerializer = srs
    rt.hashValueSerializer = jsr
    rt.valueSerializer = jsr
    rt.isEnableDefaultSerializer = true
    rt.connectionFactory = factory
    return rt
  }

  @Bean(name = [ICacheNames.IRedis.CACHE_MANAGER])
  fun cacheManager2h(factory: RedisConnectionFactory): RedisCacheManager {
    log.debug("注册 RedisCacheManager , factory = {}", factory)
    return asCacheConfig(factory)
  }

  private fun createRedisCacheConfig(dr: Duration): RedisCacheConfiguration {
    return cacheManagerConfig.entryTtl(dr)
  }

  private val cacheMap = mapOf(
    ICacheNames.M1 to createRedisCacheConfig(Duration.ofMinutes(1)),
    ICacheNames.M5 to createRedisCacheConfig(Duration.ofMinutes(5)),
    ICacheNames.M10 to createRedisCacheConfig(Duration.ofMinutes(10)),
    ICacheNames.M30 to createRedisCacheConfig(Duration.ofMinutes(30)),
    ICacheNames.H1 to createRedisCacheConfig(Duration.ofHours(1)),
    ICacheNames.H2 to createRedisCacheConfig(Duration.ofHours(2)),
    ICacheNames.H3 to createRedisCacheConfig(Duration.ofHours(3)),
    ICacheNames.D1 to createRedisCacheConfig(Duration.ofDays(1)),
    ICacheNames.D2 to createRedisCacheConfig(Duration.ofDays(2)),
    ICacheNames.D3 to createRedisCacheConfig(Duration.ofDays(3)),
    ICacheNames.D7 to createRedisCacheConfig(Duration.ofDays(7)),
    ICacheNames.D30 to createRedisCacheConfig(Duration.ofDays(30)),
    ICacheNames.D60 to createRedisCacheConfig(Duration.ofDays(60)),
    ICacheNames.D180 to createRedisCacheConfig(Duration.ofDays(180)),
    ICacheNames.D365 to createRedisCacheConfig(Duration.ofDays(365)),
    ICacheNames.FOREVER to createRedisCacheConfig(Duration.ZERO)
  )

  private fun asCacheConfig(factory: RedisConnectionFactory?): RedisCacheManager {
    return RedisCacheManager.builder(factory!!).cacheDefaults(
      cacheManagerConfig.entryTtl(Duration.ofHours(1))
    )
      .withInitialCacheConfigurations(cacheMap)
      .build()
  }
}
