package net.yan100.compose.core.models

import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class IIdcard2CodeTest {

  @Test
  fun getIdcard2Code() {
  }

  @Test
  fun getIdcardBirthday() {
  }

  @Test
  fun getIdcardSexCode() {
  }

  @Test
  fun getIdcardSex() {
  }

  @Test
  fun getIdcardDistrictCode() {
  }

  @Test
  fun idcardUpperCase() {
  }

  @Test
  fun of() {
    IIdcard2Code.of("110101199001011234").let {
      assertEquals("110101", it.idcardDistrictCode)
      assertEquals(LocalDate.of(1990, 1, 1), it.idcardBirthday)
      assertEquals(true, it.idcardSex)
    }
    IIdcard2Code.of("110101199001011204").let {
      assertEquals("110101", it.idcardDistrictCode)
      assertEquals(LocalDate.of(1990, 1, 1), it.idcardBirthday)
      assertEquals(false, it.idcardSex)
    }
  }
}
