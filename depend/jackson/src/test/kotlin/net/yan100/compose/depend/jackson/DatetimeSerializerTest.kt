package net.yan100.compose.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Resource
import net.yan100.compose.core.datetime
import net.yan100.compose.core.toLong
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
class DatetimeSerializerTest {
  lateinit var mapper: ObjectMapper @Resource set

  @Test
  fun `ensure serialize to number`() {
    val dt = datetime.now()
    val dtJson = mapper.writeValueAsString(
      dt
    )
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
    assertEquals(dt.toLong(), dt2.toLong())
  }
}
