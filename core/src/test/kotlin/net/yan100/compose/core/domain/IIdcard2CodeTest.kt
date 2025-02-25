package net.yan100.compose.core.domain

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class IIdcard2CodeTest {
  @Test
  fun get() {
    IIdcard2Code["110101199001011234"].let {
      assertEquals("110101", it.idcardDistrictCode)
      assertEquals(LocalDate.of(1990, 1, 1), it.idcardBirthday)
      assertEquals(true, it.idcardSex)
    }
    IIdcard2Code["110101199001011204"].let {
      assertEquals("110101", it.idcardDistrictCode)
      assertEquals(LocalDate.of(1990, 1, 1), it.idcardBirthday)
      assertEquals(false, it.idcardSex)
    }
  }
}
