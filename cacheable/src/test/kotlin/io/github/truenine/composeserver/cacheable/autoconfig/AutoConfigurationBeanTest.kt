package io.github.truenine.composeserver.cacheable.autoconfig

import io.github.truenine.composeserver.cacheable.CacheableEntrance
import io.github.truenine.composeserver.slf4j
import jakarta.annotation.Resource
import kotlin.test.Test
import kotlin.test.assertNotNull
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
