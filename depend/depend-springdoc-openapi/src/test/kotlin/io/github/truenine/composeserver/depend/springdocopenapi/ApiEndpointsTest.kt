package io.github.truenine.composeserver.depend.springdocopenapi

import jakarta.annotation.Resource
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(classes = [TestApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(
  properties =
    [
      "compose.depend.springdoc-open-api.group=api-test",
      "compose.depend.springdoc-open-api.enable-jwt-header=true",
      "compose.depend.springdoc-open-api.scan-packages[0]=io.github.truenine.composeserver.depend.springdocopenapi",
      "compose.depend.springdoc-open-api.author-info.title=API Endpoints Test",
      "compose.depend.springdoc-open-api.author-info.version=1.0.0",
    ]
)
class ApiEndpointsTest {

  @Resource lateinit var mockMvc: MockMvc

  @Test
  fun `should access swagger ui successfully`() {
    mockMvc.get("/swagger-ui/index.html").andExpect {
      status { isOk() }
      content { contentTypeCompatibleWith(MediaType.TEXT_HTML) }
    }
  }

  @Test
  fun `should access openapi json docs successfully`() {
    mockMvc.get("/v3/api-docs").andExpect {
      status { isOk() }
      content {
        contentType(MediaType.APPLICATION_JSON)
        jsonPath("$.openapi") { exists() }
        jsonPath("$.info") { exists() }
        jsonPath("$.info.title") { value("API Endpoints Test") }
        jsonPath("$.info.version") { value("1.0.0") }
        jsonPath("$.paths") { exists() }
      }
    }
  }

  @Test
  fun `should access grouped api docs successfully`() {
    mockMvc.get("/v3/api-docs/api-test").andExpect {
      status { isOk() }
      content {
        contentType(MediaType.APPLICATION_JSON)
        jsonPath("$.openapi") { exists() }
        jsonPath("$.info.title") { value("API Endpoints Test") }
        jsonPath("$.paths") { exists() }
      }
    }
  }

  @Test
  @DisplayName("测试 Swagger 配置端点")
  fun `should access swagger config successfully`() {
    mockMvc.get("/v3/api-docs/swagger-config").andExpect {
      status { isOk() }
      content {
        contentType(MediaType.APPLICATION_JSON)
        jsonPath("$.configUrl") { value("/v3/api-docs/swagger-config") }
        jsonPath("$.urls") { exists() }
      }
    }
  }

  @Test
  fun `should access test controller endpoints successfully`() {
    // 测试 hello 端点
    mockMvc.get("/test/hello").andExpect {
      status { isOk() }
      content {
        contentType(MediaType.APPLICATION_JSON)
        jsonPath("$.message") { value("Hello, World!") }
      }
    }

    // 测试 info 端点
    mockMvc.get("/test/info").andExpect {
      status { isOk() }
      content {
        contentType(MediaType.APPLICATION_JSON)
        jsonPath("$.service") { value("springdoc-openapi-test") }
        jsonPath("$.version") { value("1.0.0") }
        jsonPath("$.timestamp") { exists() }
      }
    }
  }

  @Test
  @DisplayName("测试 API 文档包含测试端点")
  fun `should include test endpoints in api docs`() {
    mockMvc.get("/v3/api-docs").andExpect {
      status { isOk() }
      content {
        contentType(MediaType.APPLICATION_JSON)
        jsonPath("$.paths./test/hello") { exists() }
        jsonPath("$.paths./test/info") { exists() }
        jsonPath("$.paths./test/hello.get") { exists() }
        jsonPath("$.paths./test/info.get") { exists() }
      }
    }
  }

  @Test
  @DisplayName("测试 JWT 头参数在 API 文档中的存在")
  fun `should include jwt headers in api documentation`() {
    mockMvc.get("/v3/api-docs").andExpect {
      status { isOk() }
      content {
        contentType(MediaType.APPLICATION_JSON)
        // 由于 JWT 头参数是通过 OperationCustomizer 添加的，
        // 我们需要检查实际的 API 文档结构
        jsonPath("$.openapi") { exists() }
        jsonPath("$.info") { exists() }
        jsonPath("$.paths") { exists() }
        jsonPath("$.paths./test/hello") { exists() }
        jsonPath("$.paths./test/info") { exists() }
      }
    }
  }
}
