package net.yan100.compose.rds.converters

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import kotlin.test.Test
import kotlin.test.assertNotNull
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class IntTypingJimmerProviderTest : IDatabasePostgresqlContainer {

  @Test
  fun `ensure input and output convert not exception`() {
    val provider = IntTypingJimmerProvider()
    assertNotNull(provider)
  }
}
