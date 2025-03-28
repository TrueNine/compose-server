package net.yan100.compose.rds.core.converters.jimmer

import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import net.yan100.compose.testtookit.log
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DurationScalarProviderTest {
  private val durationStr = "PT336H"
  private val duration = Duration.parse(durationStr)

  @Test
  fun `测试 duration 的可转换性`() {
    assertEquals(duration.toDays(), 14)
    assertEquals(duration.toHours(), 14 * 24)
    val result = duration.toString()
    log.info(result)
  }

  @Test
  fun toScalar() {
    val provider = DurationScalarProvider()
    val result = provider.toScalar(durationStr)
    assertNotNull(result)
    assertEquals(result, duration)
  }

  @Test
  fun toSql() {
    val provider = DurationScalarProvider()
    val result = provider.toSql(duration)
    assertNotNull(result)
    assertEquals(result, durationStr)
  }
}
