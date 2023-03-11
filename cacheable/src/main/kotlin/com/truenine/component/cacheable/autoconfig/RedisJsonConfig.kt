package com.truenine.component.cacheable.autoconfig

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.truenine.component.core.cache.Cf
import com.truenine.component.core.lang.LogKt
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
import java.time.Duration

/**
 * redis 缓存组件配置
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@Configuration
open class RedisJsonConfig(
  objectMapper: ObjectMapper,
) {
  private val jsr = Jackson2JsonRedisSerializer(objectMapper.copy().run {
    this.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
      .setSerializationInclusion(JsonInclude.Include.NON_NULL)
      .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
      .setAnnotationIntrospector(AnnotationIntrospector.nopInstance())
      .activateDefaultTyping(
        polymorphicTypeValidator,
        ObjectMapper.DefaultTyping.EVERYTHING,
        JsonTypeInfo.As.WRAPPER_ARRAY
      )
  }, Any::class.java)

  private val srs = StringRedisSerializer()
  private val cacheManagerConfig = RedisCacheConfiguration.defaultCacheConfig()
    .serializeKeysWith(
      RedisSerializationContext.SerializationPair.fromSerializer(
        srs
      )
    )
    .serializeValuesWith(
      RedisSerializationContext.SerializationPair.fromSerializer(
        jsr
      )
    )
    .disableCachingNullValues()


  @Primary
  @Bean(name = [Cf.RedisTemplate.STRING_TEMPLATE])
  open fun customRedisJsonSerializable(factory: RedisConnectionFactory)
    : RedisTemplate<String, *> {
    log.info("配置 ${Cf.RedisTemplate.STRING_TEMPLATE} factory = {}", factory)
    val rt = RedisTemplate<String, Any>()

    rt.setDefaultSerializer(jsr)

    rt.hashKeySerializer = srs
    rt.keySerializer = srs
    rt.hashValueSerializer = jsr
    rt.valueSerializer = jsr
    rt.isEnableDefaultSerializer = true

    rt.setConnectionFactory(factory)
    return rt
  }

  @Primary
  @Bean(name = [Cf.CacheManager.H2])
  open fun cacheManager2h(factory: RedisConnectionFactory?): CacheManager? {
    log.info("配置 ${Cf.CacheManager.H2} factory = {}", factory)
    return asCacheConfig(factory, Duration.ofHours(2))
  }

  @Bean(name = [Cf.CacheManager.D3])
  open fun cacheManager30day(factory: RedisConnectionFactory?): CacheManager? {
    log.info("配置 ${Cf.CacheManager.D3} factory = {}", factory)
    return asCacheConfig(factory, Duration.ofDays(30))
  }

  @Bean(name = [Cf.CacheManager.M30])
  open fun cacheManager30m(factory: RedisConnectionFactory?): CacheManager? {
    log.info("配置 ${Cf.CacheManager.M30} factory = {}", factory)
    return asCacheConfig(factory, Duration.ofMinutes(30))
  }

  @Bean(name = [Cf.CacheManager.FOREVER])
  open fun cacheManagerForever(factory: RedisConnectionFactory?): CacheManager? {
    log.info("配置 ${Cf.CacheManager.FOREVER} factory = {}", factory)
    return asCacheConfig(factory, Duration.ZERO)
  }

  private fun asCacheConfig(
    factory: RedisConnectionFactory?,
    dr: Duration
  ): RedisCacheManager {
    return RedisCacheManager.builder(factory!!)
      .cacheDefaults(cacheManagerConfig.entryTtl(dr))
      .build()
  }

  companion object {
    @JvmStatic
    private val log = LogKt.getLog(RedisJsonConfig::class)
  }
}
