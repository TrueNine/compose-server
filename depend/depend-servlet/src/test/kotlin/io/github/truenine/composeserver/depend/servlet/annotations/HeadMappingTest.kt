package io.github.truenine.composeserver.depend.servlet.annotations

import io.github.truenine.composeserver.depend.servlet.TestApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.head
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.WebApplicationContext
import kotlin.test.BeforeTest
import kotlin.test.Test

@SpringBootTest(classes = [TestApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(HeadMappingTest.HeadController::class)
class HeadMappingTest {
  @Autowired lateinit var webApplicationContext: WebApplicationContext

  lateinit var mock: MockMvc

  @BeforeTest
  fun setup() {
    mock = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
  }

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
