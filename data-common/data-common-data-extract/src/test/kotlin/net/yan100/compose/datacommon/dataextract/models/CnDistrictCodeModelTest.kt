package net.yan100.compose.datacommon.dataextract.models

import org.testng.annotations.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CnDistrictCodeModelTest {
  @Test
  fun testCreate() {
    val ab = CnDistrictCodeModel("433127000000")
    assertNotNull(ab)
    assertEquals(ab.level, 3)

    assertFailsWith<IllegalArgumentException> {
      CnDistrictCodeModel("43312700000")
    }
    assertFailsWith<IllegalArgumentException> {
      CnDistrictCodeModel("4331270000001")
    }
    assertFailsWith<NullPointerException> {
      CnDistrictCodeModel(null)
    }
  }
}
