/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.cacheable.autoconfig

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import java.time.Duration
import net.yan100.compose.core.consts.CacheFieldNames
import net.yan100.compose.core.log.slf4j
import org.springframework.cache.CacheManager
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

/**
 * redis 缓存组件配置
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@Configuration
class RedisJsonSerializerAutoConfiguration(
  objectMapper: ObjectMapper,
) {
  private val log = slf4j(RedisJsonSerializerAutoConfiguration::class)

  private val jsr =
    Jackson2JsonRedisSerializer(
      objectMapper.copy().run {
        this.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
          .setSerializationInclusion(JsonInclude.Include.NON_NULL)
          .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
          .setAnnotationIntrospector(AnnotationIntrospector.nopInstance())
          .activateDefaultTyping(
            polymorphicTypeValidator,
            ObjectMapper.DefaultTyping.EVERYTHING,
            JsonTypeInfo.As.WRAPPER_ARRAY,
          )
      },
      Any::class.java,
    )

  private val srs = StringRedisSerializer()
  private val cacheManagerConfig =
    RedisCacheConfiguration.defaultCacheConfig()
      .serializeKeysWith(
        RedisSerializationContext.SerializationPair.fromSerializer(
          srs,
        ),
      )
      .serializeValuesWith(
        RedisSerializationContext.SerializationPair.fromSerializer(
          jsr,
        ),
      )
      .disableCachingNullValues()

  @Primary
  @Bean(name = [CacheFieldNames.RedisTemplate.STRING_TEMPLATE])
  fun customRedisJsonSerializable(factory: RedisConnectionFactory): RedisTemplate<String, *> {
    log.debug("配置 ${CacheFieldNames.RedisTemplate.STRING_TEMPLATE} factory = {}", factory)
    val rt = RedisTemplate<String, Any>()

    rt.setDefaultSerializer(jsr)

    rt.hashKeySerializer = srs
    rt.keySerializer = srs
    rt.hashValueSerializer = jsr
    rt.valueSerializer = jsr
    rt.isEnableDefaultSerializer = true

    rt.connectionFactory = factory
    return rt
  }

  @Primary
  @Bean(name = [CacheFieldNames.Redis.H2])
  fun cacheManager2h(factory: RedisConnectionFactory?): CacheManager? {
    log.debug("配置 ${CacheFieldNames.Redis.H2} factory = {}", factory)
    return asCacheConfig(factory, Duration.ofHours(2))
  }

  @Primary
  @Bean(name = [CacheFieldNames.Redis.H1])
  fun cacheManager1h(factory: RedisConnectionFactory?): CacheManager? {
    log.debug("配置 ${CacheFieldNames.Redis.H2} factory = {}", factory)
    return asCacheConfig(factory, Duration.ofHours(1))
  }

  @Bean(name = [CacheFieldNames.Redis.D3])
  fun cacheManager30day(factory: RedisConnectionFactory?): CacheManager? {
    log.debug("配置 ${CacheFieldNames.Redis.D3} factory = {}", factory)
    return asCacheConfig(factory, Duration.ofDays(30))
  }

  @Bean(name = [CacheFieldNames.Redis.M30])
  fun cacheManager30m(factory: RedisConnectionFactory?): CacheManager? {
    log.debug("配置 ${CacheFieldNames.Redis.M30} factory = {}", factory)
    return asCacheConfig(factory, Duration.ofMinutes(30))
  }

  @Bean(name = [CacheFieldNames.Redis.FOREVER])
  fun cacheManagerForever(factory: RedisConnectionFactory?): CacheManager? {
    log.debug("配置 ${CacheFieldNames.Redis.FOREVER} factory = {}", factory)
    return asCacheConfig(factory, Duration.ZERO)
  }

  private fun asCacheConfig(
    factory: RedisConnectionFactory?,
    dr: Duration,
  ): RedisCacheManager {
    return RedisCacheManager.builder(factory!!)
      .cacheDefaults(cacheManagerConfig.entryTtl(dr))
      .build()
  }
}
