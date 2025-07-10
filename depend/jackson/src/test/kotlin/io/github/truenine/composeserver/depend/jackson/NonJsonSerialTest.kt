package io.github.truenine.composeserver.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import jakarta.annotation.Resource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import net.yan100.compose.testtoolkit.log
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class NonJsonSerialTest {
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

  @Test
  fun `确保 long 序列化为简单值`() {
    val testData = LongData(123)
    val json = mapper.writeValueAsString(testData)
    val deserialized = mapper.readValue(json, Any::class.java)
    assertEquals(testData, deserialized)
    assertEquals("{\"@class\":\"io.github.truenine.composeserver.depend.jackson.NonJsonSerialTest\$LongData\",\"longData\":123}", json)
    println(json)

    val testData2 = LongData(123, null)
    val json2 = mapper.writeValueAsString(testData2)
    val deserialized2 = mapper.readValue(json2, Any::class.java)
    assertEquals(testData2, deserialized2)
    assertEquals("{\"@class\":\"io.github.truenine.composeserver.depend.jackson.NonJsonSerialTest\$LongData\",\"longData\":123}", json2)

    val testData3 = LongData(123, 3L)
    val json3 = mapper.writeValueAsString(testData3)
    val deserialized3 = mapper.readValue(json3, Any::class.java)
    assertEquals(testData3, deserialized3)
    assertEquals(
      "{\"@class\":\"io.github.truenine.composeserver.depend.jackson.NonJsonSerialTest\$LongData\",\"longData\":123,\"nullableLong\":[\"java.lang.Long\",3]}",
      json3,
    )
  }

  @Test
  fun `ensure non-json serialization java lang long`() {
    val jsonObj = IdJson()
    val json = mapper.writeValueAsString(jsonObj)
    assertEquals("{\"@class\":\"io.github.truenine.composeserver.depend.jackson.NonJsonSerialTest\$IdJson\"}", json)
    log.info("json: {}", json)
    val readValue = mapper.readValue<IdJson>(json)
    log.info("readValue: {}", readValue)
    assertNotNull(readValue)
    assertFailsWith<IllegalStateException>("必须不能获取 id") { readValue.id }
  }
}
