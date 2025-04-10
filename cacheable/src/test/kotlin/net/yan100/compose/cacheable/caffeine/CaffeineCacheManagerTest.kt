package net.yan100.compose.cacheable.caffeine

import jakarta.annotation.Resource
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import net.yan100.compose.cacheable.CacheableEntrance
import net.yan100.compose.cacheable.get
import net.yan100.compose.consts.ICacheNames
import net.yan100.compose.slf4j
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.cache.get
import org.springframework.cache.set

private val log = slf4j<CaffeineCacheManagerTest>()

@SpringBootTest(classes = [CacheableEntrance::class])
class CaffeineCacheManagerTest {
  @Resource lateinit var cacheManager: CacheManager

  /** 首选必须为 caffeine 缓存 */
  @BeforeTest
  fun `before check is caffeine cache manager`() {
    assertNotNull(cacheManager)
    assertIs<CaffeineCacheManager>(cacheManager)
  }

  @Test
  fun `primary is memory cache manager`() {
    log.info(cacheManager.toString())
    assertIs<CaffeineCacheManager>(cacheManager)
  }

  @Test
  fun `get duration caches`() {
    val cache = cacheManager[ICacheNames.D1]
    assertNotNull(cache)
    val names = cacheManager.cacheNames
    assertTrue { names.containsAll(ICacheNames.ALL.toList()) }
    names.forEach {
      val c = cacheManager[it]
      assertNotNull(c)
    }
    cacheManager[ICacheNames.H2]!!["cache_key"] = "123"
    val value = cacheManager[ICacheNames.H2]!!["cache_key", String::class]
    assertNotNull(value)
    assertEquals("123", value)
  }
}
