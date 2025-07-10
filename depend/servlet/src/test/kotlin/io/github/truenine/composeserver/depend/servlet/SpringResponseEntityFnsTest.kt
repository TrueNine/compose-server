package io.github.truenine.composeserver.depend.servlet

import kotlin.test.Test
import kotlin.test.assertEquals

class SpringResponseEntityFnsTest {
  @Test
  fun `test exists response entity`() {
    val r = headMethodResponse {
      exists { true }
      assertEquals(200, this@headMethodResponse.status)
      exists { false }
      assertEquals(404, this@headMethodResponse.status)

      exists { null }
      assertEquals(404, this@headMethodResponse.status)

      exists { 0 }
      assertEquals(200, this@headMethodResponse.status)
      exists { -1 }
      assertEquals(404, this@headMethodResponse.status)
    }
  }
}
