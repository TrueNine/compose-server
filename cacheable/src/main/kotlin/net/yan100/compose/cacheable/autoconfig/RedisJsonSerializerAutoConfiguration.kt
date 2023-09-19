package net.yan100.compose.cacheable.autoconfig

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import net.yan100.compose.core.lang.slf4j
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
class RedisJsonSerializerAutoConfiguration(
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
  @Bean(name = [net.yan100.compose.core.consts.CacheFieldNames.RedisTemplate.STRING_TEMPLATE])
  fun customRedisJsonSerializable(factory: RedisConnectionFactory)
    : RedisTemplate<String, *> {
    log.debug("配置 ${net.yan100.compose.core.consts.CacheFieldNames.RedisTemplate.STRING_TEMPLATE} factory = {}", factory)
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
  @Bean(name = [net.yan100.compose.core.consts.CacheFieldNames.CacheManagerNames.H2])
  fun cacheManager2h(factory: RedisConnectionFactory?): CacheManager? {
    log.debug("配置 ${net.yan100.compose.core.consts.CacheFieldNames.CacheManagerNames.H2} factory = {}", factory)
    return asCacheConfig(factory, Duration.ofHours(2))
  }

  @Bean(name = [net.yan100.compose.core.consts.CacheFieldNames.CacheManagerNames.D3])
  fun cacheManager30day(factory: RedisConnectionFactory?): CacheManager? {
    log.debug("配置 ${net.yan100.compose.core.consts.CacheFieldNames.CacheManagerNames.D3} factory = {}", factory)
    return asCacheConfig(factory, Duration.ofDays(30))
  }

  @Bean(name = [net.yan100.compose.core.consts.CacheFieldNames.CacheManagerNames.M30])
  fun cacheManager30m(factory: RedisConnectionFactory?): CacheManager? {
    log.debug("配置 ${net.yan100.compose.core.consts.CacheFieldNames.CacheManagerNames.M30} factory = {}", factory)
    return asCacheConfig(factory, Duration.ofMinutes(30))
  }

  @Bean(name = [net.yan100.compose.core.consts.CacheFieldNames.CacheManagerNames.FOREVER])
  fun cacheManagerForever(factory: RedisConnectionFactory?): CacheManager? {
    log.debug("配置 ${net.yan100.compose.core.consts.CacheFieldNames.CacheManagerNames.FOREVER} factory = {}", factory)
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
    private val log = slf4j(RedisJsonSerializerAutoConfiguration::class)
  }
}
