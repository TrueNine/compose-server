package io.github.truenine.composeserver.depend.servlet.annotations

import jakarta.annotation.Resource
import kotlin.test.Test
import net.yan100.compose.testtoolkit.annotations.SpringServletTest
import org.springframework.context.annotation.Import
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.head
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringServletTest
@Import(HeadMappingTest.HeadController::class)
class HeadMappingTest {
  lateinit var mock: MockMvc
    @Resource set

  @Test
  fun `test found action`() {
    mock.head("/test/head/a").andExpect {
      status { isOk() }
      content { string("") }
    }
  }

  // 内嵌 Controller
  @RestController
  @RequestMapping("test/head")
  class HeadController {
    @HeadMapping("a")
    fun a(): ResponseEntity<Unit> {
      return ResponseEntity.status(200).build()
    }
  }
}
