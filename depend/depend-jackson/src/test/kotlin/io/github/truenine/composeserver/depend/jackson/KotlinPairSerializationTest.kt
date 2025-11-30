package io.github.truenine.composeserver.depend.jackson

import io.github.truenine.composeserver.testtoolkit.log
import jakarta.annotation.Resource
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import tools.jackson.databind.ObjectMapper
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
class KotlinPairSerializationTest {

  @Resource lateinit var mapper: ObjectMapper

  @Nested
  inner class PairSerialization {

    @Test
    fun `serialize string pair to json array`() {
      val pair = Pair("first", "second")
      val json = mapper.writeValueAsString(pair)

      log.info("Serialized pair: {}", json)
      assertEquals("""["first","second"]""", json)
    }

    @Test
    fun `serialize number pair to json array`() {
      val pair = Pair(1, 2)
      val json = mapper.writeValueAsString(pair)

      log.info("Serialized number pair: {}", json)
      assertEquals("[1,2]", json)
    }

    @Test
    fun `serialize mixed type pair to json array`() {
      val pair = Pair("key", 42)
      val json = mapper.writeValueAsString(pair)

      log.info("Serialized mixed pair: {}", json)
      assertEquals("""["key",42]""", json)
    }

    @Test
    fun `serialize null pair to null`() {
      val pair: Pair<String, String>? = null
      val json = mapper.writeValueAsString(pair)

      log.info("Serialized null pair: {}", json)
      assertEquals("null", json)
    }
  }

  @Nested
  inner class PairDeserialization {

    @Test
    fun `deserialize json array to string pair`() {
      val json = """["first","second"]"""
      val pair = mapper.readValue(json, Pair::class.java)

      assertNotNull(pair)
      assertEquals("first", pair.first)
      assertEquals("second", pair.second)
      log.info("Deserialized pair: {}", pair)
    }

    @Test
    fun `deserialize json array to number pair`() {
      val json = "[1,2]"
      val pair = mapper.readValue(json, Pair::class.java)

      assertNotNull(pair)
      assertEquals(1, pair.first)
      assertEquals(2, pair.second)
      log.info("Deserialized number pair: {}", pair)
    }

    @Test
    fun `deserialize json array to mixed type pair`() {
      val json = """["key",42]"""
      val pair = mapper.readValue(json, Pair::class.java)

      assertNotNull(pair)
      assertEquals("key", pair.first)
      assertEquals(42, pair.second)
      log.info("Deserialized mixed pair: {}", pair)
    }
  }

  @Nested
  inner class PairRoundTrip {

    @Test
    fun `round trip serialization preserves pair values`() {
      val originalPair = Pair("test", 123)

      // Serialize
      val json = mapper.writeValueAsString(originalPair)
      log.info("Serialized: {}", json)

      // Deserialize
      val deserializedPair = mapper.readValue(json, Pair::class.java)
      log.info("Deserialized: {}", deserializedPair)

      // Verify
      assertEquals(originalPair.first, deserializedPair.first)
      assertEquals(originalPair.second, deserializedPair.second)
    }

    @Test
    fun `round trip with nested objects`() {
      val nestedPair = Pair(Pair("inner1", "inner2"), Pair(100, 200))

      // Serialize
      val json = mapper.writeValueAsString(nestedPair)
      log.info("Serialized nested pair: {}", json)

      // Deserialize
      val deserializedPair = mapper.readValue(json, Pair::class.java)
      log.info("Deserialized nested pair: {}", deserializedPair)

      // Verify outer layer
      assertNotNull(deserializedPair.first)
      assertNotNull(deserializedPair.second)
    }
  }
}
