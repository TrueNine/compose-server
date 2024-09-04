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
import net.yan100.compose.core.autoconfig.JacksonSerializationAutoConfig
import java.time.Duration
import net.yan100.compose.core.consts.ICacheNames
import net.yan100.compose.core.log.slf4j
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
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

private val log = slf4j<RedisJsonSerializerAutoConfiguration>()

/**
 * redis 缓存组件配置
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@Configuration
class RedisJsonSerializerAutoConfiguration(
  @Qualifier(JacksonSerializationAutoConfig.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME)
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
  fun customRedisJsonSerializable(factory: RedisConnectionFactory): RedisTemplate<String, Any?> {
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

  private fun withDurationConfig(dr: Duration): RedisCacheConfiguration {
    return cacheManagerConfig.entryTtl(dr)
  }

  private val cacheMap = mapOf(
    ICacheNames.IRedis.M1 to withDurationConfig(Duration.ofMinutes(1)),
    ICacheNames.IRedis.M5 to withDurationConfig(Duration.ofMinutes(5)),
    ICacheNames.IRedis.M10 to withDurationConfig(Duration.ofMinutes(10)),
    ICacheNames.IRedis.M30 to withDurationConfig(Duration.ofMinutes(30)),
    ICacheNames.IRedis.H1 to withDurationConfig(Duration.ofHours(1)),
    ICacheNames.IRedis.H2 to withDurationConfig(Duration.ofHours(2)),
    ICacheNames.IRedis.H3 to withDurationConfig(Duration.ofHours(3)),
    ICacheNames.IRedis.D1 to withDurationConfig(Duration.ofDays(1)),
    ICacheNames.IRedis.D2 to withDurationConfig(Duration.ofDays(2)),
    ICacheNames.IRedis.D3 to withDurationConfig(Duration.ofDays(3)),
    ICacheNames.IRedis.D7 to withDurationConfig(Duration.ofDays(7)),
    ICacheNames.IRedis.D30 to withDurationConfig(Duration.ofDays(30)),
    ICacheNames.IRedis.D60 to withDurationConfig(Duration.ofDays(60)),
    ICacheNames.IRedis.D180 to withDurationConfig(Duration.ofDays(180)),
    ICacheNames.IRedis.D365 to withDurationConfig(Duration.ofDays(365))
  )

  private fun asCacheConfig(factory: RedisConnectionFactory?): RedisCacheManager {
    return RedisCacheManager.builder(factory!!).cacheDefaults(
      cacheManagerConfig.entryTtl(Duration.ofHours(1))
    )
      .withInitialCacheConfigurations(cacheMap)
      .build()
  }
}
