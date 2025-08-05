package io.github.truenine.composeserver.rds

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.truenine.composeserver.domain.DisRule
import io.github.truenine.composeserver.rds.enums.DisTyping
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class DisRuleFnsTest {

  @Test
  fun `test create`() {
    val emptyArray = DisRule(byteArrayOf())
    assertTrue { emptyArray.meta.size == 28 }
    emptyArray.meta.forEach { assertTrue { it == 0.toByte() || it == 1.toByte() } }
  }

  @Test
  fun `test match`() {
    val emptyArray = DisRule(byteArrayOf(1, 1, 1, 1, 1, 1, 1, 1))
    assertTrue { emptyArray.match(DisTyping.EYE.value, 2) }
    assertFalse { emptyArray.match(DisTyping.MOUTH.value, 1) }
    assertTrue { emptyArray.match(DisTyping.EAR.value, 4) }
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
