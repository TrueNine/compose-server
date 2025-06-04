package net.yan100.compose.rds.converters

import net.yan100.compose.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest
class IntTypingJimmerProviderTest : IDatabasePostgresqlContainer {

  @Test
  fun `ensure input and output convert not exception`() {
    val provider = IntTypingJimmerProvider()
    assertNotNull(provider)
  }
}
