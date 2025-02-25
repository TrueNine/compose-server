package net.yan100.compose.cacheable.autoconfig

import jakarta.annotation.Resource
import kotlin.test.Test
import kotlin.test.assertNotNull
import net.yan100.compose.cacheable.CacheableEntrance
import net.yan100.compose.core.slf4j
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.caffeine.CaffeineCacheManager

private val log = slf4j<AutoConfigurationBeanTest>()

@SpringBootTest(classes = [CacheableEntrance::class])
class AutoConfigurationBeanTest {
  @Resource lateinit var caffeineCacheManager: CaffeineCacheManager

  @Test
  fun `has caffeine cache manager`() {
    log.info("caffeine cache manager: {}", caffeineCacheManager)
    assertNotNull(caffeineCacheManager)
  }
}
