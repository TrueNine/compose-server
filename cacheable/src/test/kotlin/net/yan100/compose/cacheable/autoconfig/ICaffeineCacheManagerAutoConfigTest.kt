package net.yan100.compose.cacheable.autoconfig

import net.yan100.compose.cacheable.CacheableEntrance
import net.yan100.compose.cacheable.extensionfunctions.get
import net.yan100.compose.core.consts.ICacheNames
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.cache.get
import org.springframework.cache.set
import kotlin.test.*

@SpringBootTest(classes = [CacheableEntrance::class])
class ICaffeineCacheManagerAutoConfigTest {
  @Autowired
  lateinit var cacheManager: CacheManager

  @BeforeTest
  fun before() {
    assertNotNull(cacheManager)
    assertIs<CaffeineCacheManager>(cacheManager)
  }

  @Test
  fun `test primary memory cache manager`() {
    println(cacheManager)
    assertIs<CaffeineCacheManager>(cacheManager)
  }

  @Test
  fun `test get duration caches`() {
    val cache = cacheManager[ICacheNames.ICaffeine.D1]
    assertNotNull(cache)
    val names = cacheManager.cacheNames
    assertTrue {
      names.containsAll(ICacheNames.ICaffeine.ALL.toList())
    }
    names.forEach {
      val c = cacheManager[it]
      assertNotNull(c)
    }
    cacheManager[ICacheNames.ICaffeine.H2]!!["cache_key"] = "123"
    val value = cacheManager[ICacheNames.ICaffeine.H2]!!["cache_key", String::class]
    assertNotNull(value)
    assertEquals("123", value)
  }
}
