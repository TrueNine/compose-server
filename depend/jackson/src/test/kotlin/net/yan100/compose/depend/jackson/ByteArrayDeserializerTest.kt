package net.yan100.compose.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Resource
import net.yan100.compose.depend.jackson.autoconfig.JacksonAutoConfiguration
import net.yan100.compose.testtoolkit.log
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@SpringBootTest
class ByteArrayDeserializerTest {
  lateinit var mapper: ObjectMapper
    @Resource set

  lateinit var map: ObjectMapper
    @Resource(
      name = JacksonAutoConfiguration.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME
    )
    set

  class S {
    var a: String? = null
    var b: ByteArray? = null
  }

  @Test
  fun `test serialize byte array`() {
    val ab =
      S().apply {
        a = "a"
        b = byteArrayOf(1, 0, 1, 0, 1, 0)
      }

    val json = mapper.writeValueAsString(ab)
    val ba = mapper.readValue(json, S::class.java)
    log.info("ba: {}", ba)
    log.info("json: {}", json)
    assertEquals("{\"a\":\"a\",\"b\":\"AQABAAEA\"}", json)
    assertEquals(ba.a, ab.a)
    assertContentEquals(ba.b, ab.b)
  }

  @Test
  fun `test deserialize byte array`() {
    val ab =
      S().apply {
        a = "a"
        b = byteArrayOf(1, 0, 1, 0, 1, 0)
      }

    val json = map.writeValueAsString(ab)
    log.info("json a: {}", json)

    val ba = map.readValue(json, S::class.java)
    log.info("ba: {}", ba)

    assertEquals(
      "{\"@class\":\"net.yan100.compose.depend.jackson.ByteArrayDeserializerTest\$S\",\"a\":\"a\",\"b\":[\"[B\",\"AQABAAEA\"]}",
      json,
    )
    assertEquals(ba.a, ab.a)
    assertContentEquals(ba.b, ab.b)
  }
}
