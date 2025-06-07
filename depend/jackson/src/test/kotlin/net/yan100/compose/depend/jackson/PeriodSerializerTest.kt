package net.yan100.compose.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.annotation.Resource
import java.time.Period
import kotlin.test.Test
import kotlin.test.assertEquals
import net.yan100.compose.testtoolkit.log
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PeriodSerializerTest {
  @Resource lateinit var mapper: ObjectMapper

  @Test
  fun `test serialize period`() {
    val period = Period.parse("P1Y03M04D")
    assertEquals(period.years, 1)

    val value = mapper.writeValueAsString(period)
    log.info(value)

    val a = mapper.readValue<Period>(value)
    assertEquals(a, period)
  }
}
