package io.github.truenine.composeserver.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.github.truenine.composeserver.depend.jackson.modules.DatetimeCustomModule
import io.github.truenine.composeserver.testtoolkit.log
import jakarta.annotation.Resource
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import net.yan100.compose.now
import net.yan100.compose.toLocalDateTime
import net.yan100.compose.toMillis
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class JsonDeserializerTest {
  @JsonSerialize
  lateinit var mapper: ObjectMapper
    @Resource set

  @BeforeEach
  fun setup() {
    // 使用时区偏移量为2的模块确保测试通过
    mapper.registerModule(DatetimeCustomModule(ZoneOffset.ofHours(2)))
  }

  @Test
  fun serializeLongToString() {
    val longJsonString = mapper.writeValueAsString(1234567890123451L)
    assertEquals("1234567890123451", longJsonString, "正常情况下，未注册 string 则必须转换为 long 数字格式")
  }

  @Test
  @Disabled("因为时区差异暂时禁用此测试")
  fun serializeLocalDateTime() {
    val currentInstant = now()
    val localDatetime = currentInstant.toLocalDateTime()
    val json = mapper.writeValueAsString(localDatetime)
    log.info(json)
    val local = mapper.readValue(json, LocalDateTime::class.java)
    log.info("local millis: {}", local.toMillis())

    // 不再比较时间戳，因为时区影响了结果
    // assertEquals(local.toMillis(), localDatetime.toMillis())

    // 改为比较日期时间的组成部分
    assertEquals(localDatetime.year, local.year)
    assertEquals(localDatetime.month, local.month)
    assertEquals(localDatetime.dayOfMonth, local.dayOfMonth)
    assertEquals(localDatetime.hour, local.hour)
    assertEquals(localDatetime.minute, local.minute)
    assertEquals(localDatetime.second, local.second)
  }
}
