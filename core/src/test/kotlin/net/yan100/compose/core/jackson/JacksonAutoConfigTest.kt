package net.yan100.compose.core.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
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
