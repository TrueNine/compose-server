package io.github.truenine.composeserver.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.testtoolkit.log
import jakarta.annotation.Resource
import kotlin.test.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest
class JacksonAutoConfigTest {
  @Resource lateinit var mappers: List<ObjectMapper>

  @Test
  fun `test mappers registers`() {
    log.info("mappers: {}", mappers)
  }
}
