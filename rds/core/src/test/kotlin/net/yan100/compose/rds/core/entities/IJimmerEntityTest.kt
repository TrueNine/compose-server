package net.yan100.compose.rds.core.entities

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.string
import net.yan100.compose.testtookit.log
import kotlin.test.Test
import kotlin.test.assertNotNull


class IJimmerEntityTest {
  class A : IJpaEntity by entity() {
    var a: string? = null
  }

  private val mapper: ObjectMapper = ObjectMapper()

  @Test
  fun `serialize json data`() {
    val value = A()
    val json = mapper.writeValueAsString(value)
    log.info(json)
    assertNotNull(json)
  }
}
