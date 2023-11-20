package net.yan100.compose.core.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@SpringBootTest
class ByteArrayDeserializerTest {
  @Autowired
  lateinit var mapper: ObjectMapper


  class S {
    var a: String? = null
    var b: ByteArray? = null
  }

  @Test
  fun `test serialize byte array`() {
    val ab = S().apply {
      a = "a"
      b = byteArrayOf(1, 0, 1, 0, 1, 0)
    }

    val json = mapper.writeValueAsString(ab)
    val ba = mapper.readValue(json, S::class.java)
    println(ba)
    println(json)
    assertEquals("{\"a\":\"a\",\"b\":[1,0,1,0,1,0]}", json)
    assertEquals(ba.a, ab.a)
    assertContentEquals(ba.b, ab.b)
  }
}
