package io.github.truenine.composeserver.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.Pq
import io.github.truenine.composeserver.testtoolkit.log
import jakarta.annotation.Resource
import java.time.LocalDateTime
import java.time.ZonedDateTime
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class KotlinModuleCompatibilityTest {

  @Resource lateinit var mapper: ObjectMapper

  @Nested
  inner class TimestampSerializationCompatibility {

    @Test
    fun `kotlin types do not interfere with timestamp serialization`() {
      val testData =
        mapOf(
          "pair" to Pair("key", "value"),
          "pageParam" to Pq[1, 20],
          "localDateTime" to LocalDateTime.now(),
          "zonedDateTime" to ZonedDateTime.now(),
          "timestamp" to System.currentTimeMillis(),
        )

      // 序列化
      val json = mapper.writeValueAsString(testData)
      log.info("Serialized mixed data: {}", json)

      // 验证JSON包含预期的结构
      assertTrue(json.contains("\"pair\":[\"key\",\"value\"]"))
      assertTrue(json.contains("\"pageParam\":{"))
      assertTrue(json.contains("\"timestamp\":"))

      // 反序列化
      val deserializedData = mapper.readValue(json, Map::class.java)
      log.info("Deserialized mixed data: {}", deserializedData)

      assertNotNull(deserializedData["pair"])
      assertNotNull(deserializedData["pageParam"])
      assertNotNull(deserializedData["timestamp"])
    }

    @Test
    fun `pair serialization works with time objects`() {
      val timePair = Pair(LocalDateTime.now(), ZonedDateTime.now())

      // 序列化
      val json = mapper.writeValueAsString(timePair)
      log.info("Serialized time pair: {}", json)

      // 验证是数组格式
      assertTrue(json.startsWith("["))
      assertTrue(json.endsWith("]"))

      // 反序列化
      val deserializedPair = mapper.readValue(json, Pair::class.java)
      log.info("Deserialized time pair: {}", deserializedPair)

      assertNotNull(deserializedPair.first)
      assertNotNull(deserializedPair.second)
    }

    @Test
    fun `page param with time fields serializes correctly`() {
      val complexData = mapOf("pagination" to Pq[0, 10], "createdAt" to LocalDateTime.now(), "metadata" to Pair("version", "1.0"))

      // 序列化
      val json = mapper.writeValueAsString(complexData)
      log.info("Serialized complex data: {}", json)

      // 反序列化
      val deserializedData = mapper.readValue(json, Map::class.java)
      log.info("Deserialized complex data: {}", deserializedData)

      assertNotNull(deserializedData["pagination"])
      assertNotNull(deserializedData["createdAt"])
      assertNotNull(deserializedData["metadata"])
    }
  }

  @Nested
  inner class ModuleRegistrationVerification {

    @Test
    fun `verify kotlin module is properly registered`() {
      // 测试Pair序列化
      val pair = Pair("test", 123)
      val pairJson = mapper.writeValueAsString(pair)
      log.info("Pair serialization test: {}", pairJson)
      assertTrue(pairJson == """["test",123]""")

      // 测试IPageParam序列化
      val pageParam = Pq[2, 30]
      val pageParamJson = mapper.writeValueAsString(pageParam)
      log.info("PageParam serialization test: {}", pageParamJson)
      assertTrue(pageParamJson.contains("\"o\":2"))
      assertTrue(pageParamJson.contains("\"s\":30"))
    }

    @Test
    fun `verify no conflicts with other modules`() {
      // 创建包含多种类型的复杂对象
      val complexObject =
        mapOf(
          "string" to "test",
          "number" to 42,
          "boolean" to true,
          "pair" to Pair("a", "b"),
          "pageParam" to Pq[1, 15],
          "dateTime" to LocalDateTime.now(),
          "array" to listOf(1, 2, 3),
          "nested" to mapOf("inner" to "value"),
        )

      // 序列化和反序列化应该都成功
      val json = mapper.writeValueAsString(complexObject)
      log.info("Complex object JSON: {}", json)

      val deserialized = mapper.readValue(json, Map::class.java)
      log.info("Deserialized complex object: {}", deserialized)

      // 验证所有字段都存在
      assertNotNull(deserialized["string"])
      assertNotNull(deserialized["number"])
      assertNotNull(deserialized["boolean"])
      assertNotNull(deserialized["pair"])
      assertNotNull(deserialized["pageParam"])
      assertNotNull(deserialized["dateTime"])
      assertNotNull(deserialized["array"])
      assertNotNull(deserialized["nested"])
    }
  }
}
