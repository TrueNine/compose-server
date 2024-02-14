package net.yan100.compose.datacommon.dataextract.models

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CnDistrictCodeTest {
    @Test
    fun testCreate() {
        val ab = CnDistrictCode("433127000000")
        assertNotNull(ab)
        assertEquals(ab.level, 3)
    }
}
