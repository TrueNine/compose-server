package io.github.truenine.composeserver.depend.servlet.annotations

import io.github.truenine.composeserver.depend.servlet.TestApplication
import jakarta.annotation.Resource
import kotlin.test.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.head
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootTest(classes = [TestApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

  // Embedded Controller
  @RestController
  @RequestMapping("test/head")
  class HeadController {
    @HeadMapping("a")
    fun a(): ResponseEntity<Unit> {
      return ResponseEntity.status(200).build()
    }
  }
}
