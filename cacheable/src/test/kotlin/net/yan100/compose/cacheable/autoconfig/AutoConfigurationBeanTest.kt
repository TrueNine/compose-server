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

import jakarta.annotation.Resource
import net.yan100.compose.cacheable.CacheableEntrance
import net.yan100.compose.core.slf4j
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.caffeine.CaffeineCacheManager
import kotlin.test.Test
import kotlin.test.assertNotNull

private val log = slf4j<AutoConfigurationBeanTest>()

@SpringBootTest(classes = [CacheableEntrance::class])
class AutoConfigurationBeanTest {
  @Resource
  lateinit var caffeineCacheManager: CaffeineCacheManager

  @Test
  fun `has caffeine cache manager`() {
    log.info("caffeine cache manager: {}", caffeineCacheManager)
    assertNotNull(caffeineCacheManager)
  }
}
