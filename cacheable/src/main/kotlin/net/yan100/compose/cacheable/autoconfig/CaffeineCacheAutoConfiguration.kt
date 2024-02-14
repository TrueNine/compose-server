package net.yan100.compose.cacheable.autoconfig

import com.github.benmanes.caffeine.cache.Caffeine
import net.yan100.compose.core.consts.CacheFieldNames
import net.yan100.compose.core.lang.slf4j
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.time.Duration

@Configuration
class CaffeineCacheAutoConfiguration {
    companion object {
        private val log = slf4j(CaffeineCacheAutoConfiguration::class)
    }

    private fun create(name: String, d: Duration): CaffeineCache {
        return CaffeineCache(
            name,
            Caffeine.newBuilder()
                .expireAfterWrite(d)
                .build()
        )
    }

    private final val m = mutableListOf<CaffeineCache>()

    init {
        if (m.isEmpty()) {
            m += create(CacheFieldNames.Caffeine.M30, Duration.ofMinutes(30))
            m += create(CacheFieldNames.Caffeine.H1, Duration.ofHours(1))
            m += create(CacheFieldNames.Caffeine.H2, Duration.ofHours(2))
            m += create(CacheFieldNames.Caffeine.H3, Duration.ofHours(3))
            m += create(CacheFieldNames.Caffeine.D1, Duration.ofDays(1))
            m += create(CacheFieldNames.Caffeine.D2, Duration.ofDays(2))
            m += create(CacheFieldNames.Caffeine.D3, Duration.ofDays(3))
            m += create(CacheFieldNames.Caffeine.FOREVER, Duration.ZERO)
        }
    }

    @Primary
    @Bean(name = [CacheFieldNames.Caffeine.CACHE])
    fun caffeineCacheManager(): CacheManager {
        log.debug("配置 CaffeineCache 缓存")
        val s = SimpleCacheManager()

        if (m.isNotEmpty()) s.setCaches(m)
        else throw IllegalStateException("缓存配置为空")

        return s
    }
}
