package net.yan100.compose.cacheable.autoconfig

import com.github.benmanes.caffeine.cache.Caffeine
import kotlin.test.Test
import kotlin.test.assertNull


class CaffeineCacheAutoConfigurationTest {

    @Test
    fun `test caffeine cache`() {
        val ac = Caffeine.newBuilder().build<String, String>()
        val bc = Caffeine.newBuilder().build<String, String>()

        ac.put("acc", "ess")
        val bResult = bc.getIfPresent("acc")

        assertNull(bResult)
    }
}
