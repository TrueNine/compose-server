package net.yan100.compose.security.crypto

import net.yan100.compose.domain.ISensitivity
import net.yan100.compose.testtoolkit.log
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ISensitivityTest {
  abstract class Ab : ISensitivity {
    var ab: String? = null

    override fun changeWithSensitiveData() {
      ab?.also { ab = "ab sensitive" }
    }
  }

  class B : Ab() {
    override fun changeWithSensitiveData() {
      ab?.also { ab = "b sensitive" }
    }
  }

  @Test
  fun `change sensitive`() {
    val b = B()
    b.ab = "123"
    val old = b.ab
    b.changeWithSensitiveData()
    val new = b.ab
    log.info(b.ab)

    assertNotEquals(old, new)
    assertEquals("b sensitive", new, "确保函数调用必须处于最底层")
    assertNotEquals("ab sensitive", new, "确保函数调用必须处于最底层")
  }
}
