package net.yan100.compose.core.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import kotlin.test.Test

@WebMvcTest
class JacksonAutoConfigTest {
  @Autowired
  lateinit var mappers: List<ObjectMapper>

  @Test
  fun `test mappers registers`() {
    println(mappers)
  }
}
