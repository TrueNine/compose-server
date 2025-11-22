package io.github.truenine.composeserver

import io.github.truenine.composeserver.testtoolkit.info
import io.github.truenine.composeserver.testtoolkit.log
import io.swagger.v3.oas.annotations.media.Schema
import kotlin.test.Test
import kotlin.test.assertEquals

/** Verifies selected Kotlin language behaviours relied upon by the codebase. */
class KotlinLangTest {
  class FunClass(var count: Int = 0) {
    private fun abc() {
      count += 1
    }

    fun b(a: String): String {
      abc()
      return "var = $a + $count"
    }
  }

  @Test
  fun `call class function`() {
    val fc = FunClass()
    val b = fc::b
    b("a")
    log.info(fc::count)
  }

  @Test
  fun `get reflect function name`() {
    val schemaTitle = Schema::title
    log.info("schema: {}", schemaTitle)
    log.info("schemaName: {}", schemaTitle.name)
    assertEquals(schemaTitle.name, "title")
  }
}
