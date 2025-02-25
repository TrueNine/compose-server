package net.yan100.compose.data.extract.service

import kotlin.test.Test
import kotlin.test.assertEquals

class IChinaFirstNameServiceTest {
  @Test
  fun `test not repeat`() {
    val a =
      IChinaFirstNameService.CHINA_FIRST_NAMES.groupBy { it }
        .filter { it.value.size > 1 }
    assertEquals(a.size, 0, message = "re ${a.keys}")
  }
}
