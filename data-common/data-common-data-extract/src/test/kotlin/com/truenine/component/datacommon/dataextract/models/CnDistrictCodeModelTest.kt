package com.truenine.component.datacommon.dataextract.models

import org.testng.annotations.Test
import java.lang.NullPointerException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CnDistrictCodeModelTest {
  @Test
  fun testCreate() {
    val ab = CnDistrictCodeModel(433_127_000_000)
    assertNotNull(ab)
    assertEquals(ab.level, 3)

    assertFailsWith<IllegalArgumentException> {
      CnDistrictCodeModel(433_127_000_00)
    }
    assertFailsWith<IllegalArgumentException> {
      CnDistrictCodeModel(433_127_000_000_1)
    }
    assertFailsWith<NullPointerException> {
      CnDistrictCodeModel(null)
    }
  }
}
