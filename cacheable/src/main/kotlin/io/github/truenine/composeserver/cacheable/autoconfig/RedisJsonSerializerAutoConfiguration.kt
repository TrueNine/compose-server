package io.github.truenine.composeserver.cacheable.autoconfig

import io.github.truenine.composeserver.consts.ICacheNames
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import io.github.truenine.composeserver.logger
import java.time.Duration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.*
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper

/**
 * Redis cache component configuration
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@Configuration
@ConditionalOnBean(RedisConnectionFactory::class)
class RedisJsonSerializerAutoConfiguration(@Qualifier(JacksonAutoConfiguration.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME) jsonMapper: JsonMapper) {
  companion object {
    @JvmStatic private val log = logger<RedisJsonSerializerAutoConfiguration>()
    private const val VIRTUAL_THREAD_REDIS_FACTORY_BEAN_NAME = "redisConnectionFactoryVirtualThreads"
  }

  private val jsr =
    GenericJacksonJsonRedisSerializer.builder().apply { customize { it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) } }.build()

  private val srs = StringRedisSerializer()
  private val cacheManagerConfig =
    RedisCacheConfiguration.defaultCacheConfig()
      .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(srs))
      .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsr))
      .disableCachingNullValues()

  @Bean(name = [ICacheNames.IRedis.HANDLE])
  fun customRedisJsonSerializable(@Qualifier(VIRTUAL_THREAD_REDIS_FACTORY_BEAN_NAME) factory: RedisConnectionFactory): RedisTemplate<String, *> {
    log.trace("register redisTemplate factory: {}", factory)
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
  fun cacheManager2h(@Qualifier(VIRTUAL_THREAD_REDIS_FACTORY_BEAN_NAME) factory: RedisConnectionFactory): RedisCacheManager {
    log.debug("register RedisCacheManager , factory: {}", factory)
    return asCacheConfig(factory)
  }

  private fun createRedisCacheConfig(dr: Duration): RedisCacheConfiguration {
    return cacheManagerConfig.entryTtl(dr)
  }

  private val cacheMap =
    mapOf(
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
      ICacheNames.FOREVER to createRedisCacheConfig(Duration.ZERO),
    )

  private fun asCacheConfig(factory: RedisConnectionFactory): RedisCacheManager {
    return RedisCacheManager.builder(factory).cacheDefaults(cacheManagerConfig.entryTtl(Duration.ofHours(1))).withInitialCacheConfigurations(cacheMap).build()
  }
}
