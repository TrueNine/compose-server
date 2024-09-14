package net.yan100.compose.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Resource
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import kotlin.test.Test

@WebMvcTest
class JacksonAutoConfigTest {
  @Resource
  lateinit var mappers: List<ObjectMapper>

  @Test
  fun `test mappers registers`() {
    println(mappers)
  }
}
