package io.github.truenine.composeserver.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.Pq
import io.github.truenine.composeserver.domain.IPageParam
import io.github.truenine.composeserver.domain.IPageParamLike
import io.github.truenine.composeserver.testtoolkit.log
import jakarta.annotation.Resource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class IPageParamEnhancedTest {

  @Resource lateinit var mapper: ObjectMapper

  @Nested
  inner class IPageParamDeserialization {

    @Test
    fun `deserialize complete page param json`() {
      val json = """{"o":1,"s":20}"""
      val pageParam = mapper.readValue(json, IPageParam::class.java)

      assertNotNull(pageParam)
      assertEquals(1, pageParam.o)
      assertEquals(20, pageParam.s)
      log.info("Deserialized complete page param: {}", pageParam)
    }

    @Test
    fun `deserialize page param with unpage flag`() {
      val json = """{"o":0,"s":10,"u":true}"""
      val pageParam = mapper.readValue(json, IPageParam::class.java)

      assertNotNull(pageParam)
      assertEquals(0, pageParam.o)
      assertEquals(Int.MAX_VALUE, pageParam.s) // unPage=true时应该设置为最大值
      // 验证unPage行为 - 注意u属性已弃用，但仍需验证功能
      log.info("Deserialized unpage param: {}", pageParam)
    }

    @Test
    fun `deserialize page param with null values`() {
      val json = """{"o":null,"s":null}"""
      val pageParam = mapper.readValue(json, IPageParam::class.java)

      assertNotNull(pageParam)
      assertEquals(0, pageParam.o) // 默认值
      assertEquals(42, pageParam.s) // 默认值
      log.info("Deserialized page param with nulls: {}", pageParam)
    }

    @Test
    fun `deserialize page param with partial fields`() {
      val json = """{"s":15}"""
      val pageParam = mapper.readValue(json, IPageParam::class.java)

      assertNotNull(pageParam)
      assertEquals(0, pageParam.o) // 默认值
      assertEquals(15, pageParam.s)
      log.info("Deserialized partial page param: {}", pageParam)
    }

    @Test
    fun `deserialize page param with unknown fields`() {
      val json = """{"o":2,"s":25,"unknown":"value","extra":123}"""
      val pageParam = mapper.readValue(json, IPageParam::class.java)

      assertNotNull(pageParam)
      assertEquals(2, pageParam.o)
      assertEquals(25, pageParam.s)
      log.info("Deserialized page param with unknown fields: {}", pageParam)
    }
  }

  @Nested
  inner class IPageParamLikeDeserialization {

    @Test
    fun `deserialize to IPageParamLike interface`() {
      val json = """{"o":3,"s":30}"""
      val pageParamLike = mapper.readValue(json, IPageParamLike::class.java)

      assertNotNull(pageParamLike)
      assertEquals(3, pageParamLike.o)
      assertEquals(30, pageParamLike.s)
      log.info("Deserialized IPageParamLike: {}", pageParamLike)
    }
  }

  @Nested
  inner class ErrorHandling {

    @Test
    fun `handle empty json object`() {
      val json = """{}"""
      val pageParam = mapper.readValue(json, IPageParam::class.java)

      assertNotNull(pageParam)
      assertEquals(0, pageParam.o) // 默认值
      assertEquals(42, pageParam.s) // 默认值
      log.info("Deserialized empty object: {}", pageParam)
    }
  }

  @Nested
  inner class RoundTripSerialization {

    @Test
    fun `round trip with Pq factory method`() {
      val originalParam = Pq[5, 50]

      // 序列化
      val json = mapper.writeValueAsString(originalParam)
      log.info("Serialized Pq: {}", json)

      // 反序列化
      val deserializedParam = mapper.readValue(json, IPageParam::class.java)
      log.info("Deserialized Pq: {}", deserializedParam)

      // 验证
      assertEquals(originalParam.o, deserializedParam.o)
      assertEquals(originalParam.s, deserializedParam.s)
    }

    @Test
    fun `round trip with unpage param`() {
      val originalParam = Pq.unPage()

      // 序列化
      val json = mapper.writeValueAsString(originalParam)
      log.info("Serialized unpage Pq: {}", json)

      // 反序列化
      val deserializedParam = mapper.readValue(json, IPageParam::class.java)
      log.info("Deserialized unpage Pq: {}", deserializedParam)

      // 验证unPage行为 - 通过pageSize验证而不是直接访问u属性
      assertEquals(Int.MAX_VALUE, deserializedParam.s)
    }
  }

  @Nested
  inner class CompatibilityWithTimestampSerialization {

    @Test
    fun `page param serialization does not interfere with time types`() {
      // 创建包含时间字段和分页参数的复合对象
      val testData = mapOf("pageParam" to Pq[1, 20], "timestamp" to System.currentTimeMillis(), "dateTime" to java.time.LocalDateTime.now())

      // 序列化
      val json = mapper.writeValueAsString(testData)
      log.info("Serialized composite data: {}", json)

      // 反序列化
      val deserializedData = mapper.readValue(json, Map::class.java)
      log.info("Deserialized composite data: {}", deserializedData)

      // 验证分页参数部分
      assertNotNull(deserializedData["pageParam"])
    }
  }
}
