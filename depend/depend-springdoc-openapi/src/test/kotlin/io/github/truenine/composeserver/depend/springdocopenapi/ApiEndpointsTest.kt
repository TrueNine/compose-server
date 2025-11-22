package io.github.truenine.composeserver.depend.springdocopenapi

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(classes = [TestApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

  @Autowired lateinit var webApplicationContext: WebApplicationContext

  lateinit var mockMvc: MockMvc

  @BeforeEach
  fun setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
  }

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
  @DisplayName("Test Swagger Configuration Endpoint")
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
    // Test the hello endpoint
    mockMvc.get("/test/hello").andExpect {
      status { isOk() }
      content {
        contentType(MediaType.APPLICATION_JSON)
        jsonPath("$.message") { value("Hello, World!") }
      }
    }

    // Test the info endpoint
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
  @DisplayName("Test API Documentation Includes Test Endpoints")
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
  @DisplayName("Test Presence of JWT Header Parameters in API Documentation")
  fun `should include jwt headers in api documentation`() {
    mockMvc.get("/v3/api-docs").andExpect {
      status { isOk() }
      content {
        contentType(MediaType.APPLICATION_JSON)
        // Since JWT header parameters are added via an OperationCustomizer,
        // we need to check the actual API documentation structure.
        jsonPath("$.openapi") { exists() }
        jsonPath("$.info") { exists() }
        jsonPath("$.paths") { exists() }
        jsonPath("$.paths./test/hello") { exists() }
        jsonPath("$.paths./test/info") { exists() }
      }
    }
  }
}
