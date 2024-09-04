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

import com.github.benmanes.caffeine.cache.AsyncCache
import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration
import net.yan100.compose.core.consts.ICacheNames
import net.yan100.compose.core.log.slf4j
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

private val log = slf4j(CaffeineCacheAutoConfiguration::class)

@Configuration
class CaffeineCacheAutoConfiguration {
  private fun create(d: Duration): AsyncCache<Any?, Any?> {
    return Caffeine.newBuilder().expireAfterWrite(d).buildAsync()
  }

  private val cacheMap = mapOf(
    ICacheNames.ICaffeine.M1 to create(Duration.ofMinutes(1)),
    ICacheNames.ICaffeine.M5 to create(Duration.ofMinutes(5)),
    ICacheNames.ICaffeine.M10 to create(Duration.ofMinutes(10)),
    ICacheNames.ICaffeine.M30 to create(Duration.ofMinutes(30)),
    ICacheNames.ICaffeine.H1 to create(Duration.ofHours(1)),
    ICacheNames.ICaffeine.H2 to create(Duration.ofHours(2)),
    ICacheNames.ICaffeine.H3 to create(Duration.ofHours(3)),
    ICacheNames.ICaffeine.D1 to create(Duration.ofDays(1)),
    ICacheNames.ICaffeine.D2 to create(Duration.ofDays(2)),
    ICacheNames.ICaffeine.D3 to create(Duration.ofDays(3)),
    ICacheNames.ICaffeine.D7 to create(Duration.ofDays(7)),
    ICacheNames.ICaffeine.D30 to create(Duration.ofDays(30)),
    ICacheNames.ICaffeine.D60 to create(Duration.ofDays(60)),
    ICacheNames.ICaffeine.D180 to create(Duration.ofDays(180)),
    ICacheNames.ICaffeine.D365 to create(Duration.ofDays(365)),
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
