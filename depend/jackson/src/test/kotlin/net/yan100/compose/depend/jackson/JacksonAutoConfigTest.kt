package net.yan100.compose.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Resource
import kotlin.test.Test
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest
class JacksonAutoConfigTest {
  @Resource lateinit var mappers: List<ObjectMapper>

  @Test
  fun `test mappers registers`() {
    log.info("mappers: {}", mappers)
  }
}
