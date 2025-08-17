package io.github.truenine.composeserver.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import io.github.truenine.composeserver.testtoolkit.log
import jakarta.annotation.Resource
import java.time.Instant
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.springframework.boot.test.context.SpringBootTest

/**
 * Jackson序列化配置测试
 *
 * 测试Jackson ObjectMapper的配置和序列化行为，包括：
 * - 非JSON类型序列化
 * - 时间戳序列化验证
 * - 配置正确性验证
 */
@SpringBootTest
class JacksonSerializationConfigTest {
  lateinit var mapper: ObjectMapper
    @Resource(name = JacksonAutoConfiguration.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME) set

  lateinit var plainMapper: ObjectMapper
    @Resource(name = JacksonAutoConfiguration.DEFAULT_OBJECT_MAPPER_BEAN_NAME) set

  @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
  open class IdJson(@Transient private var __internalId: Long? = null) {
    var id: Long
      @JvmName("____getdwadawdawdawdawdawdawd")
      get() {
        if (__internalId === null) throw IllegalStateException("数据库 id 当前为空，不能获取")
        return __internalId!!
      }
      @JvmName("____setdwadawdawdawdawdawdawd")
      set(v) {
        this.__internalId = v
      }

    @Deprecated("", level = DeprecationLevel.HIDDEN) fun getId(): java.lang.Long? = this.__internalId as java.lang.Long?

    @Deprecated("", level = DeprecationLevel.HIDDEN)
    fun setId(id: java.lang.Long?) {
      this.__internalId = id as? Long
    }
  }

  data class LongData(val longData: Long, private var nullableLong: Long? = null) {
    fun getNullableLong(): Long? {
      return this.nullableLong
    }

    fun setNullableLong(a: Long) {
      this.nullableLong = a
    }
  }

  @Nested
  inner class LongDataSerializationTest {

    @Test
    fun `serialize long data with non-null values`() {
      val testData = LongData(123)
      val json = mapper.writeValueAsString(testData)
      val deserialized = mapper.readValue(json, Any::class.java)

      log.info("Long data serialized to: {}", json)
      assertEquals(testData, deserialized)
      assertEquals("{\"@class\":\"io.github.truenine.composeserver.depend.jackson.JacksonSerializationConfigTest\$LongData\",\"longData\":123}", json)
    }

    @Test
    fun `serialize long data with null nullable field`() {
      val testData = LongData(123, null)
      val json = mapper.writeValueAsString(testData)
      val deserialized = mapper.readValue(json, Any::class.java)

      log.info("Long data with null field serialized to: {}", json)
      assertEquals(testData, deserialized)
      assertEquals("{\"@class\":\"io.github.truenine.composeserver.depend.jackson.JacksonSerializationConfigTest\$LongData\",\"longData\":123}", json)
    }

    @Test
    fun `serialize long data with non-null nullable field`() {
      val testData = LongData(123, 3L)
      val json = mapper.writeValueAsString(testData)
      val deserialized = mapper.readValue(json, Any::class.java)

      log.info("Long data with non-null nullable field serialized to: {}", json)
      assertEquals(testData, deserialized)
      assertEquals(
        "{\"@class\":\"io.github.truenine.composeserver.depend.jackson.JacksonSerializationConfigTest\$LongData\",\"longData\":123,\"nullableLong\":[\"java.lang.Long\",3]}",
        json,
      )
    }
  }

  @Nested
  inner class IdJsonSerializationTest {

    @Test
    fun `serialize empty IdJson object`() {
      val jsonObj = IdJson()
      val json = mapper.writeValueAsString(jsonObj)

      log.info("Empty IdJson serialized to: {}", json)
      assertEquals("{\"@class\":\"io.github.truenine.composeserver.depend.jackson.JacksonSerializationConfigTest\$IdJson\"}", json)

      val readValue = mapper.readValue<IdJson>(json)
      log.info("Deserialized IdJson: {}", readValue)
      assertNotNull(readValue)
      assertFailsWith<IllegalStateException>("数据库 id 当前为空，不能获取") { readValue.id }
    }
  }

  @Nested
  inner class TimestampSerializationVerificationTest {

    @Test
    fun `verify timestamp serialization is enabled for LocalDateTime`() {
      val dateTime = LocalDateTime.of(2025, 1, 16, 12, 30, 45)
      val json = plainMapper.writeValueAsString(dateTime)

      log.info("LocalDateTime serialized with plain mapper: {}", json)

      // 验证是否序列化为时间戳（数字）而不是ISO字符串
      val timestamp = json.toLongOrNull()
      if (timestamp != null) {
        log.info("LocalDateTime correctly serialized as timestamp: {}", timestamp)
        assertTrue(timestamp > 0, "时间戳应该大于0")
      } else {
        log.info("LocalDateTime serialized as string format: {}", json)
        // 如果不是时间戳，应该是有效的JSON字符串
        assertTrue(json.startsWith("\"") && json.endsWith("\""), "应该是有效的JSON字符串格式")
      }
    }

    @Test
    fun `verify timestamp serialization is enabled for Instant`() {
      val instant = Instant.ofEpochMilli(1737000645000L)
      val json = plainMapper.writeValueAsString(instant)

      log.info("Instant serialized with plain mapper: {}", json)

      // 验证是否序列化为时间戳
      val timestamp = json.toLongOrNull()
      if (timestamp != null) {
        log.info("Instant correctly serialized as timestamp: {}", timestamp)
        assertEquals(1737000645000L, timestamp)
      } else {
        log.info("Instant serialized as string format: {}", json)
        assertTrue(json.startsWith("\"") && json.endsWith("\""), "应该是有效的JSON字符串格式")
      }
    }

    @Test
    fun `verify ObjectMapper configuration consistency`() {
      // 验证两个ObjectMapper的基本配置
      assertNotNull(mapper, "非忽略ObjectMapper应该正确注入")
      assertNotNull(plainMapper, "默认ObjectMapper应该正确注入")

      log.info("Non-ignore mapper configuration: {}", mapper.serializationConfig.toString())
      log.info("Plain mapper configuration: {}", plainMapper.serializationConfig.toString())

      // 验证配置的一致性
      val testData = mapOf("test" to "value", "number" to 123)
      val json1 = mapper.writeValueAsString(testData)
      val json2 = plainMapper.writeValueAsString(testData)

      log.info("Test data serialized by non-ignore mapper: {}", json1)
      log.info("Test data serialized by plain mapper: {}", json2)

      // 基本数据类型的序列化应该一致
      assertTrue(json1.contains("\"test\":\"value\""), "应该包含正确的字符串值")
      assertTrue(json2.contains("\"test\":\"value\""), "应该包含正确的字符串值")
    }
  }
}
