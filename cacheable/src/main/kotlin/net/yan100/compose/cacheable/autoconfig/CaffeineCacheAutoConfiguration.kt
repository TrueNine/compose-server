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

import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration
import net.yan100.compose.core.consts.CacheFieldNames
import net.yan100.compose.core.log.slf4j
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class CaffeineCacheAutoConfiguration {
  companion object {
    private val log = slf4j(CaffeineCacheAutoConfiguration::class)
  }

  private fun create(
    name: String,
    d: Duration,
  ): CaffeineCache {
    return CaffeineCache(
      name,
      Caffeine.newBuilder().expireAfterWrite(d).build(),
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

    if (m.isNotEmpty()) {
      s.setCaches(m)
    } else {
      throw IllegalStateException("缓存配置为空")
    }

    return s
  }
}
