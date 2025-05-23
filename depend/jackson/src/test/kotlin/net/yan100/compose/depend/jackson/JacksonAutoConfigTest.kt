package net.yan100.compose.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Resource
import net.yan100.compose.testtoolkit.log
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import kotlin.test.Test

@WebMvcTest
class JacksonAutoConfigTest {
  @Resource
  lateinit var mappers: List<ObjectMapper>

  @Test
  fun `test mappers registers`() {
    log.info("mappers: {}", mappers)
  }
}
