package net.yan100.compose.core.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest
class ByteArrayDeserializerTest {
  @Autowired
  lateinit var mapper: ObjectMapper

  @Test
  fun `test serialize byte array`() {
    val ab = byteArrayOf(1, 0, 1, 0, 1)
    val json = mapper.writeValueAsString(ab)
    val ba = mapper.readValue(json, ByteArray::class.java)
    println(ba.contentToString())
    println(json)
    assertEquals("\"AQABAAE=\"", json)
    assertEquals(ba.contentToString(), ab.contentToString())
  }
}
