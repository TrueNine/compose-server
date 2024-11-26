package net.yan100.compose.core

import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.testtookit.info
import net.yan100.compose.testtookit.log
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * # kotlin 特性测试
 *
 * 保证某些 kotlin 特性的稳定性
 */
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
