package io.github.truenine.composeserver.rds.converters

import io.github.truenine.composeserver.testtoolkit.log
import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DurationScalarProviderTest : IDatabasePostgresqlContainer {
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
