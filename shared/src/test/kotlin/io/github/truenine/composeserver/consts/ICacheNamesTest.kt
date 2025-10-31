package io.github.truenine.composeserver.consts

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Validates cache name constants declared in {@link ICacheNames}. */
class ICacheNamesTest {

  @Test
  fun verifiesRedisCacheConstants() {
    log.info("Verifying Redis cache constants")

    assertEquals("JsonStringRedisTemplateYan100Handle", ICacheNames.IRedis.HANDLE)
    assertEquals("JsonStringRedisTemplateYan100CacheManager", ICacheNames.IRedis.CACHE_MANAGER)

    log.info("Redis HANDLE: {}", ICacheNames.IRedis.HANDLE)
    log.info("Redis CACHE_MANAGER: {}", ICacheNames.IRedis.CACHE_MANAGER)
  }

  @Test
  fun verifiesCaffeineCacheConstants() {
    log.info("Verifying Caffeine cache constants")

    assertEquals("JavaCaffeineTemplateYan100Handle", ICacheNames.ICaffeine.HANDLE)
    assertEquals("JavaCaffeineTemplateYan100CacheManager", ICacheNames.ICaffeine.CACHE_MANAGER)

    log.info("Caffeine HANDLE: {}", ICacheNames.ICaffeine.HANDLE)
    log.info("Caffeine CACHE_MANAGER: {}", ICacheNames.ICaffeine.CACHE_MANAGER)
  }

  @Test
  fun verifiesMinuteDurationCaches() {
    log.info("Verifying minute-based duration cache constants")

    assertEquals("durational_cache_duration_1m", ICacheNames.M1)
    assertEquals("durational_cache_duration_5m", ICacheNames.M5)
    assertEquals("durational_cache_duration_10m", ICacheNames.M10)
    assertEquals("durational_cache_duration_30m", ICacheNames.M30)

    log.info("Minute caches: M1={}, M5={}, M10={}, M30={}", ICacheNames.M1, ICacheNames.M5, ICacheNames.M10, ICacheNames.M30)
  }

  @Test
  fun verifiesDayDurationCaches() {
    log.info("Verifying day-based duration cache constants")

    assertEquals("durational_cache_duration_1d", ICacheNames.D1)
    assertEquals("durational_cache_duration_2d", ICacheNames.D2)
    assertEquals("durational_cache_duration_3d", ICacheNames.D3)
    assertEquals("durational_cache_duration_7d", ICacheNames.D7)
    assertEquals("durational_cache_duration_30d", ICacheNames.D30)
    assertEquals("durational_cache_duration_60d", ICacheNames.D60)
    assertEquals("durational_cache_duration_180d", ICacheNames.D180)
    assertEquals("durational_cache_duration_365d", ICacheNames.D365)

    log.info("Day caches: D1={}, D7={}, D30={}, D365={}", ICacheNames.D1, ICacheNames.D7, ICacheNames.D30, ICacheNames.D365)
  }

  @Test
  fun verifiesDurationAliases() {
    log.info("Verifying special duration cache constants")

    assertEquals("durational_cache_duration_fo", ICacheNames.FOREVER)

    // Validate aliases
    assertEquals(ICacheNames.D7, ICacheNames.W1)
    assertEquals(ICacheNames.D30, ICacheNames.MO1)
    assertEquals(ICacheNames.D365, ICacheNames.Y1)

    log.info("Special constants: FOREVER={}, W1={}, MO1={}, Y1={}", ICacheNames.FOREVER, ICacheNames.W1, ICacheNames.MO1, ICacheNames.Y1)
  }

  @Test
  fun listsAllCacheConstants() {
    log.info("Validating ALL cache constant aggregation")

    val expectedConstants =
      arrayOf(
        ICacheNames.M1,
        ICacheNames.M5,
        ICacheNames.M10,
        ICacheNames.M30,
        ICacheNames.H1,
        ICacheNames.H2,
        ICacheNames.H3,
        ICacheNames.D1,
        ICacheNames.D2,
        ICacheNames.D3,
        ICacheNames.D7,
        ICacheNames.D30,
        ICacheNames.D60,
        ICacheNames.D180,
        ICacheNames.D365,
        ICacheNames.FOREVER,
      )

    assertEquals(expectedConstants.size, ICacheNames.ALL.size, "ALL should include every cache constant")

    expectedConstants.forEach { expected -> assertTrue(ICacheNames.ALL.contains(expected), "ALL should contain constant: $expected") }

    log.info("ALL includes {} cache constants", ICacheNames.ALL.size)
  }
}
