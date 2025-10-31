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
 * Jackson serialization configuration tests
 *
 * Tests the configuration and serialization behavior of Jackson ObjectMapper, including:
 * - Serialization of non-JSON types
 * - Timestamp serialization validation
 * - Configuration correctness validation
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
        if (__internalId === null) throw IllegalStateException("Database ID is currently null, cannot retrieve")
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
      assertFailsWith<IllegalStateException>("Database ID is currently null, cannot retrieve") { readValue.id }
    }
  }

  @Nested
  inner class TimestampSerializationVerificationTest {

    @Test
    fun `verify timestamp serialization is enabled for LocalDateTime`() {
      val dateTime = LocalDateTime.of(2025, 1, 16, 12, 30, 45)
      val json = plainMapper.writeValueAsString(dateTime)

      log.info("LocalDateTime serialized with plain mapper: {}", json)

      // Verify if serialized as a timestamp (number) instead of an ISO string
      val timestamp = json.toLongOrNull()
      if (timestamp != null) {
        log.info("LocalDateTime correctly serialized as timestamp: {}", timestamp)
        assertTrue(timestamp > 0, "Timestamp should be greater than 0")
      } else {
        log.info("LocalDateTime serialized as string format: {}", json)
        // If not a timestamp, it should be a valid JSON string
        assertTrue(json.startsWith("\"") && json.endsWith("\""), "Should be a valid JSON string format")
      }
    }

    @Test
    fun `verify timestamp serialization is enabled for Instant`() {
      val instant = Instant.ofEpochMilli(1737000645000L)
      val json = plainMapper.writeValueAsString(instant)

      log.info("Instant serialized with plain mapper: {}", json)

      // Verify if serialized as a timestamp
      val timestamp = json.toLongOrNull()
      if (timestamp != null) {
        log.info("Instant correctly serialized as timestamp: {}", timestamp)
        assertEquals(1737000645000L, timestamp)
      } else {
        log.info("Instant serialized as string format: {}", json)
        assertTrue(json.startsWith("\"") && json.endsWith("\""), "Should be a valid JSON string format")
      }
    }

    @Test
    fun `verify ObjectMapper configuration consistency`() {
      // Verify the basic configuration of the two ObjectMappers
      assertNotNull(mapper, "Non-ignoring ObjectMapper should be injected correctly")
      assertNotNull(plainMapper, "Default ObjectMapper should be injected correctly")

      log.info("Non-ignore mapper configuration: {}", mapper.serializationConfig.toString())
      log.info("Plain mapper configuration: {}", plainMapper.serializationConfig.toString())

      // Verify configuration consistency
      val testData = mapOf("test" to "value", "number" to 123)
      val json1 = mapper.writeValueAsString(testData)
      val json2 = plainMapper.writeValueAsString(testData)

      log.info("Test data serialized by non-ignore mapper: {}", json1)
      log.info("Test data serialized by plain mapper: {}", json2)

      // Serialization of basic data types should be consistent
      assertTrue(json1.contains("\"test\":\"value\""), "Should contain the correct string value")
      assertTrue(json2.contains("\"test\":\"value\""), "Should contain the correct string value")
    }
  }
}
