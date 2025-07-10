package net.yan100.compose.security.jwt

import io.github.truenine.composeserver.testtoolkit.info
import io.github.truenine.composeserver.testtoolkit.log
import jakarta.annotation.Resource
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest
class BootLaunchTest {
  lateinit var ctx: ApplicationContext
    @Resource set

  @Test
  fun `test launch`() {
    log.info(::ctx)
  }
}
