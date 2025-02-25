package net.yan100.compose.depend.servlet

import jakarta.annotation.Resource
import kotlin.test.Test
import net.yan100.compose.testtookit.annotations.SpringServletTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

/** # 确保 pathVariable 的解析性质 */
@SpringServletTest
class PathVariableTest {
  lateinit var mockMvc: MockMvc
    @Resource set

  @Test
  fun `ensure urlencoded parameter to path variable`() {
    // 直接 url 是不行的
    mockMvc.get("/test/pathVariable/urlencoded//api/path").andExpect {
      status { isNotFound() }
    }

    // 不会转义 url encoded
    mockMvc.get("/test/pathVariable/urlencoded/%2Fapi%2Fpath").andExpect {
      status { isOk() }
      content { string("%2Fapi%2Fpath") }
    }
  }
}
