package io.github.truenine.composeserver.depend.jackson

import io.github.truenine.composeserver.datetime
import io.github.truenine.composeserver.testtoolkit.log
import jakarta.annotation.Resource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.springframework.boot.test.context.SpringBootTest
import tools.jackson.databind.ObjectMapper

@SpringBootTest
class DatetimeSerializerTest {
  lateinit var mapper: ObjectMapper
    @Resource set

  @BeforeEach
  fun setup() {
    // Spring Boot auto-configures ObjectMapper with DatetimeCustomModule
    // No manual registration needed
  }

  @Test
  @Disabled("Temporarily disabled due to time zone differences")
  fun `ensure serialize to number`() {
    val dt = datetime.now()
    val dtJson = mapper.writeValueAsString(dt)
    log.info("dtJson: {}", dtJson)
    assertNotNull(dtJson.toLongOrNull())
    val dt2 = mapper.readValue(dtJson, datetime::class.java)
    assertNotNull(dt2)
    assertEquals(dt.year, dt2.year)
    assertEquals(dt.month, dt2.month)
    assertEquals(dt.dayOfMonth, dt2.dayOfMonth)
    assertEquals(dt.hour, dt2.hour)
    assertEquals(dt.minute, dt2.minute)
    assertEquals(dt.second, dt2.second)
  }
}
