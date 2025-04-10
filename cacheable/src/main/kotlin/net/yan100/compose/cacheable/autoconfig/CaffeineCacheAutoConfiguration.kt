package net.yan100.compose.cacheable.autoconfig

import com.github.benmanes.caffeine.cache.AsyncCache
import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration
import net.yan100.compose.consts.ICacheNames
import net.yan100.compose.slf4j
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

private val log = slf4j(CaffeineCacheAutoConfiguration::class)

@Configuration
class CaffeineCacheAutoConfiguration {
  private fun create(d: Duration): AsyncCache<Any, Any?> {
    return Caffeine.newBuilder().recordStats().expireAfterWrite(d).buildAsync()
  }

  private val cacheMap =
    mapOf(
      ICacheNames.M1 to create(Duration.ofMinutes(1)),
      ICacheNames.M5 to create(Duration.ofMinutes(5)),
      ICacheNames.M10 to create(Duration.ofMinutes(10)),
      ICacheNames.M30 to create(Duration.ofMinutes(30)),
      ICacheNames.H1 to create(Duration.ofHours(1)),
      ICacheNames.H2 to create(Duration.ofHours(2)),
      ICacheNames.H3 to create(Duration.ofHours(3)),
      ICacheNames.D1 to create(Duration.ofDays(1)),
      ICacheNames.D2 to create(Duration.ofDays(2)),
      ICacheNames.D3 to create(Duration.ofDays(3)),
      ICacheNames.D7 to create(Duration.ofDays(7)),
      ICacheNames.D30 to create(Duration.ofDays(30)),
      ICacheNames.D60 to create(Duration.ofDays(60)),
      ICacheNames.D180 to create(Duration.ofDays(180)),
      ICacheNames.D365 to create(Duration.ofDays(365)),
      ICacheNames.FOREVER to create(Duration.ZERO),
    )

  @Primary
  @Bean(name = [ICacheNames.ICaffeine.CACHE_MANAGER])
  fun caffeineCacheManager(): CaffeineCacheManager {
    log.debug("配置 CaffeineCache 缓存")
    val s = CaffeineCacheManager()
    cacheMap.forEach { (k, v) -> s.registerCustomCache(k, v) }
    return s
  }
}
