package net.yan100.compose.depend.servlet.annotations

import jakarta.annotation.Resource
import net.yan100.compose.depend.servlet.controller.HeadController
import net.yan100.compose.testtookit.annotations.SpringServletTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.head
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringServletTest
class HeadMappingTest {
  lateinit var mock: MockMvc @Resource set
  lateinit var controller: HeadController @Resource set

  @BeforeTest
  fun setup() {
    assertNotNull(controller)
  }

  @Test
  fun `test found action`() {
    mock.head("/test/head/a")
      .andExpect {
        status { isOk() }
        content {
          string("true")
        }
      }
  }
}
