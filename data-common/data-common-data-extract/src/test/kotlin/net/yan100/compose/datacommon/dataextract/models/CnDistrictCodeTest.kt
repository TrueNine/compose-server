package net.yan100.compose.datacommon.dataextract.models

import org.testng.annotations.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CnDistrictCodeTest {
  @Test
  fun testCreate() {
    val ab = CnDistrictCode("433127000000")
    assertNotNull(ab)
    assertEquals(ab.level, 3)

    assertFailsWith<IllegalArgumentException> {
      CnDistrictCode("43312700000")
    }
    assertFailsWith<IllegalArgumentException> {
      CnDistrictCode("4331270000001")
    }
    assertFailsWith<NullPointerException> {
      CnDistrictCode(null)
    }
  }
}
