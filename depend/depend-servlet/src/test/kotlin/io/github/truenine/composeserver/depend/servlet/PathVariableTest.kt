package io.github.truenine.composeserver.depend.servlet

import jakarta.annotation.Resource
import kotlin.test.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** # Ensure the parsing nature of pathVariable */
@SpringBootTest(classes = [TestApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(PathVariableTest.TestPathVariableController::class)
class PathVariableTest {
  lateinit var mockMvc: MockMvc
    @Resource set

  @Test
  fun `ensure urlencoded parameter to path variable`() {
    // Direct URL is not acceptable
    mockMvc.get("/test/pathVariable/urlencoded//api/path").andExpect { status { isNotFound() } }

    // Will not escape URL encoded
    mockMvc.get("/test/pathVariable/urlencoded/%2Fapi%2Fpath").andExpect {
      status { isOk() }
      content { string("%2Fapi%2Fpath") }
    }
  }

  // Embedded Controller
  @RestController
  @RequestMapping("test/pathVariable")
  class TestPathVariableController {
    @GetMapping("urlencoded/{enc}")
    fun urlencoded(@PathVariable enc: String): String {
      return enc
    }
  }
}
