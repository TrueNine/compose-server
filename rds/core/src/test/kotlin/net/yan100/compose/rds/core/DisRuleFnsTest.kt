package net.yan100.compose.rds.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlin.test.*
import net.yan100.compose.core.DisRule
import net.yan100.compose.rds.core.typing.DisTyping

class DisRuleFnsTest {

  @Test
  fun `test create`() {
    val emptyArray = DisRule(byteArrayOf())
    assertTrue { emptyArray.meta.size == 28 }
    emptyArray.meta.forEach {
      assertTrue { it == 0.toByte() || it == 1.toByte() }
    }
  }

  @Test
  fun `test match`() {
    val emptyArray = DisRule(byteArrayOf(1, 1, 1, 1, 1, 1, 1, 1))
    assertTrue { emptyArray.match(DisTyping.EYE, 2) }
    assertFalse { emptyArray.match(DisTyping.MOUTH, 1) }
    assertTrue { emptyArray.match(DisTyping.EAR, 4) }
    assertFails { emptyArray.match(8, 4) }
    assertFails { emptyArray.match(0, 4) }
    assertFails { emptyArray.match(-1, 4) }
    assertFails { emptyArray.match(1, 5) }
    assertFails { emptyArray.match(1, 0) }
  }

  @Test
  fun `test serialize`() {
    val emptyArray = DisRule(byteArrayOf(1, 1, 1, 1, 1, 1, 1, 1))
    val map = ObjectMapper()

    val json = map.writeValueAsString(emptyArray)
    assertEquals("\"AQEBAQEBAQEAAAAAAAAAAAAAAAAAAAAAAAAAAA==\"", json)
    val readJson = map.readValue<ByteArray>(json)
    assertContentEquals(readJson, emptyArray.meta)
    println(readJson.joinToString())
  }
}
