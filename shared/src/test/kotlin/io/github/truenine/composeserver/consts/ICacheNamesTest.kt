package io.github.truenine.composeserver.consts

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * # 缓存名称常量测试
 *
 * 测试 ICacheNames 中定义的各种缓存名称常量
 */
class ICacheNamesTest {

  @Test
  fun `测试 Redis 缓存常量`() {
    log.info("测试 Redis 缓存常量")

    assertEquals("JsonStringRedisTemplateYan100Handle", ICacheNames.IRedis.HANDLE)
    assertEquals("JsonStringRedisTemplateYan100CacheManager", ICacheNames.IRedis.CACHE_MANAGER)

    log.info("Redis HANDLE: {}", ICacheNames.IRedis.HANDLE)
    log.info("Redis CACHE_MANAGER: {}", ICacheNames.IRedis.CACHE_MANAGER)
  }

  @Test
  fun `测试 Caffeine 缓存常量`() {
    log.info("测试 Caffeine 缓存常量")

    assertEquals("JavaCaffeineTemplateYan100Handle", ICacheNames.ICaffeine.HANDLE)
    assertEquals("JavaCaffeineTemplateYan100CacheManager", ICacheNames.ICaffeine.CACHE_MANAGER)

    log.info("Caffeine HANDLE: {}", ICacheNames.ICaffeine.HANDLE)
    log.info("Caffeine CACHE_MANAGER: {}", ICacheNames.ICaffeine.CACHE_MANAGER)
  }

  @Test
  fun `测试持续时间缓存常量 - 分钟级别`() {
    log.info("测试分钟级别缓存常量")

    assertEquals("durational_cache_duration_1m", ICacheNames.M1)
    assertEquals("durational_cache_duration_5m", ICacheNames.M5)
    assertEquals("durational_cache_duration_10m", ICacheNames.M10)
    assertEquals("durational_cache_duration_30m", ICacheNames.M30)

    log.info("分钟级别缓存常量: M1={}, M5={}, M10={}, M30={}", ICacheNames.M1, ICacheNames.M5, ICacheNames.M10, ICacheNames.M30)
  }

  @Test
  fun `测试持续时间缓存常量 - 小时级别`() {
    log.info("测试小时级别缓存常量")

    assertEquals("durational_cache_duration_1h", ICacheNames.H1)
    assertEquals("durational_cache_duration_2h", ICacheNames.H2)
    assertEquals("durational_cache_duration_3h", ICacheNames.H3)

    log.info("小时级别缓存常量: H1={}, H2={}, H3={}", ICacheNames.H1, ICacheNames.H2, ICacheNames.H3)
  }

  @Test
  fun `测试持续时间缓存常量 - 天级别`() {
    log.info("测试天级别缓存常量")

    assertEquals("durational_cache_duration_1d", ICacheNames.D1)
    assertEquals("durational_cache_duration_2d", ICacheNames.D2)
    assertEquals("durational_cache_duration_3d", ICacheNames.D3)
    assertEquals("durational_cache_duration_7d", ICacheNames.D7)
    assertEquals("durational_cache_duration_30d", ICacheNames.D30)
    assertEquals("durational_cache_duration_60d", ICacheNames.D60)
    assertEquals("durational_cache_duration_180d", ICacheNames.D180)
    assertEquals("durational_cache_duration_365d", ICacheNames.D365)

    log.info("天级别缓存常量: D1={}, D7={}, D30={}, D365={}", ICacheNames.D1, ICacheNames.D7, ICacheNames.D30, ICacheNames.D365)
  }

  @Test
  fun `测试特殊持续时间缓存常量`() {
    log.info("测试特殊持续时间缓存常量")

    assertEquals("durational_cache_duration_fo", ICacheNames.FOREVER)

    // 测试别名
    assertEquals(ICacheNames.D7, ICacheNames.W1)
    assertEquals(ICacheNames.D30, ICacheNames.MO1)
    assertEquals(ICacheNames.D365, ICacheNames.Y1)

    log.info("特殊常量: FOREVER={}, W1={}, MO1={}, Y1={}", ICacheNames.FOREVER, ICacheNames.W1, ICacheNames.MO1, ICacheNames.Y1)
  }

  @Test
  fun `测试所有缓存常量数组`() {
    log.info("测试所有缓存常量数组")

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

    assertEquals(expectedConstants.size, ICacheNames.ALL.size, "ALL 数组大小应该匹配")

    expectedConstants.forEach { expected -> assertTrue(ICacheNames.ALL.contains(expected), "ALL 数组应该包含常量: $expected") }

    log.info("ALL 数组包含 {} 个缓存常量", ICacheNames.ALL.size)
    ICacheNames.ALL.forEachIndexed { index, constant -> log.info("ALL[{}] = {}", index, constant) }
  }

  @Test
  fun `测试缓存常量的唯一性`() {
    log.info("测试缓存常量的唯一性")

    val allConstants = ICacheNames.ALL.toList()
    val uniqueConstants = allConstants.toSet()

    assertEquals(allConstants.size, uniqueConstants.size, "所有缓存常量应该是唯一的")

    log.info("验证了 {} 个缓存常量的唯一性", allConstants.size)
  }

  @Test
  fun `测试缓存常量命名规范`() {
    log.info("测试缓存常量命名规范")

    // 验证所有常量都以 "durational_cache_duration_" 开头（除了 FOREVER）
    val durationConstants = ICacheNames.ALL.filter { it != ICacheNames.FOREVER }

    durationConstants.forEach { constant ->
      assertTrue(constant.startsWith("durational_cache_duration_"), "持续时间常量应该以 'durational_cache_duration_' 开头: $constant")
    }

    // 验证 FOREVER 常量
    assertTrue(ICacheNames.FOREVER.startsWith("durational_cache_duration_"), "FOREVER 常量也应该遵循命名规范")
    assertTrue(ICacheNames.FOREVER.endsWith("_fo"), "FOREVER 常量应该以 '_fo' 结尾")

    log.info("验证了 {} 个常量的命名规范", ICacheNames.ALL.size)
  }
}
