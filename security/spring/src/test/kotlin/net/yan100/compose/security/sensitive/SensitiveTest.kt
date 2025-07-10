package net.yan100.compose.security.sensitive

import io.github.truenine.composeserver.testtoolkit.annotations.SpringServletTest
import jakarta.annotation.Resource
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import net.yan100.compose.security.autoconfig.SensitiveResultResponseBodyAdvice
import net.yan100.compose.security.controller.SensitiveController
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@SpringServletTest
class SensitiveTest {
  lateinit var mockMvc: MockMvc
    @Resource set

  lateinit var controller: SensitiveController
    @Resource set

  lateinit var sensitive: SensitiveResultResponseBodyAdvice
    @Resource set

  @BeforeTest
  fun setup() {
    assertNotNull(mockMvc)
    assertNotNull(controller)
    assertNotNull(sensitive)
    mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(sensitive).build()
  }

  // TODO 补齐测试用例
  @Test
  fun `test query param split`() {
    mockMvc.get("/test/sensitive/get").andExpect {
      status { isOk() }
      content { contentType(MediaType.APPLICATION_JSON) }
    }
  }
}
