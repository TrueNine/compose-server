package net.yan100.compose.rds.core.entities

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.Id
import net.yan100.compose.core.RefId
import net.yan100.compose.core.string
import net.yan100.compose.testtookit.log
import kotlin.test.Test
import kotlin.test.assertNotNull


class IEntityTest {
  class A : IEntity {
    private var lateId: RefId = ""
    override fun setId(id: Id) {
      lateId = id
    }

    override fun getId(): Id {
      return this.lateId
    }

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
