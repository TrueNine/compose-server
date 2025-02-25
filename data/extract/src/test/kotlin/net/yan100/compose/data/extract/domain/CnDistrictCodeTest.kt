package net.yan100.compose.data.extract.domain

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

class CnDistrictCodeTest {
  @Test
  fun testCreate() {
    val ab = CnDistrictCode("433127000000")
    assertNotNull(ab)
    assertEquals(ab.level, 3)
  }
}
