package net.yan100.compose.core.lang

import org.testng.annotations.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class StrTest {
  @Test
  fun testNonText() {
    assertTrue {
      Str.nonText("")
    }
  }

  @Test
  fun testInLine() {
    val a = Str.inLine("1\n")
    assertFalse {
      a.contains("\n")
    }
  }

  @Test
  fun testHasText() {
    assertTrue {
      Str.hasText("abc")
    }
  }

  @Test
  fun testOmit() {
    val b = Str.omit("abc", 2)
    assertFalse {
      b.contains("c")
    }
  }
}
