package net.yan100.compose.core.kotlin

import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.testtookit.log
import org.junit.jupiter.api.Test

class KotlinTest {
  @Test
  fun `test get function name`() {
    val schemaTitle = Schema::title

    log.info("schema: {}", schemaTitle)
    log.info("schemaName: {}", schemaTitle.name)
  }
}
