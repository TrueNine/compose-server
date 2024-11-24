package net.yan100.compose.rds.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class TakeViewModelFnsTest {
  data class Dto(
    val a: String = "a",
    val b: Int = 2,
    val c: Boolean = true
  )

  @Test
  fun `take take finally end`() {
    var flag = 1
    val result = takeViewModel {
      false takeImmediate 1
      true takeFinallyImmediate 2
      true take {
        flag = 2
        3
      }
    }
    assertEquals(2, result)
    assertEquals(1, flag, "当被终结后，lazy block 不能被执行")
  }

  @Test
  fun `take once result`() {
    val result = takeViewModel {
      false takeOnceImmediate 1
      false takeOnceImmediate 4
      true takeOnceImmediate 3
      true takeOnceImmediate 2
    }
    assertEquals(3, result)
  }

  @Test
  fun `test finally take`() {
    val result = Dto().takeViewModel {
      false takeFinallyImmediate 3
      true takeFinallyImmediate 2
      true takeFinallyImmediate 4
      true takeImmediate 1
    }
    assertEquals(2, result)
  }

  @Test
  fun `be null`() {
    val e: String? = null
    val result = e.takeViewModel {
      todo(String::class)
      true takeFinally { "1" }
    }
    assertNull(result)
  }

  @Test
  fun `assert todo throws end`() {
    assertFails {
      Dto().takeViewModel {
        todo { 1 }
      }
    }
    assertFails {
      Dto().takeViewModel {
        todo<String>()
      }
    }
    assertFails {
      Dto().takeViewModel {
        todo(String::class)
      }
    }
  }

  @Test
  fun `get result`() {
    val result = Dto().takeViewModel {
      true take { "1" }
    }
    assertEquals("1", result)
  }

  @Test
  fun `get default result not be null`() {
    val result = Dto().takeViewModel {
      defaultResult { "1" }
      true take { null }
    }
    assertEquals("1", result)
  }
}
