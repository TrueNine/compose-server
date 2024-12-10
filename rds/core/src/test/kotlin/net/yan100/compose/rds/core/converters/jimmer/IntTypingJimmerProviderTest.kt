package net.yan100.compose.rds.core.converters.jimmer

import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest
class IntTypingJimmerProviderTest {

  @Test
  fun `ensure input and output convert not exception`() {
    val provider = IntTypingJimmerProvider()
    assertNotNull(provider)
  }
}
