package net.yan100.compose.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.annotation.Resource
import net.yan100.compose.depend.jackson.autoconfig.JacksonAutoConfiguration
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

@SpringBootTest
class NonJsonSerialTest {
  lateinit var mapper: ObjectMapper @Resource(name = JacksonAutoConfiguration.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME) set
  lateinit var plainMapper: ObjectMapper @Resource(name = JacksonAutoConfiguration.DEFAULT_OBJECT_MAPPER_BEAN_NAME) set

  @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
  open class IdJson(
    @Transient
    private var __internalId: Long? = null
  ) {
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

    @Deprecated("", level = DeprecationLevel.HIDDEN)
    fun getId(): java.lang.Long? = this.__internalId as java.lang.Long?

    @Deprecated("", level = DeprecationLevel.HIDDEN)
    fun setId(id: java.lang.Long?) {
      this.__internalId = id as? Long
    }
  }

  @Test
  fun `ensure non-json serialization java lang long`() {
    val jsonObj = IdJson()
    val json = mapper.writeValueAsString(jsonObj)
    assertEquals("{\"net.yan100.compose.depend.jackson.NonJsonSerialTest\$IdJson\":{}}", json)
    log.info("json: {}", json)
    val readValue = mapper.readValue<IdJson>(json)
    log.info("readValue: {}", readValue)
    assertNotNull(readValue)
    assertFailsWith<IllegalStateException>("必须不能获取 id") { readValue.id }
  }
}
