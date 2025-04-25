package net.yan100.compose.security.jwt

import jakarta.annotation.Resource
import net.yan100.compose.testtoolkit.info
import net.yan100.compose.testtoolkit.log
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
