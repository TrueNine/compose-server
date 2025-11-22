package io.github.truenine.composeserver.depend.springdocopenapi

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
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
      "compose.depend.springdoc-open-api.group=test-group",
      "compose.depend.springdoc-open-api.enable-jwt-header=true",
      "compose.depend.springdoc-open-api.scan-packages[0]=io.github.truenine.composeserver.depend.springdocopenapi",
      "compose.depend.springdoc-open-api.author-info.title=Test API",
      "compose.depend.springdoc-open-api.author-info.version=1.0.0",
      "compose.depend.springdoc-open-api.author-info.description=Test API Description",
    ]
)
class BeanSetupTest {
  @Autowired lateinit var webApplicationContext: WebApplicationContext

  lateinit var mock: MockMvc

  @BeforeTest
  fun setup() {
    mock = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    assertNotNull(mock)
  }

  @Test
  fun `auto loaded swagger config`() {
    val jsonStr =
      mock
        .get("/v3/api-docs/swagger-config")
        .andExpect {
          content {
            contentType(MediaType.APPLICATION_JSON)
            jsonPath("$.configUrl") {
              isString()
              value("/v3/api-docs/swagger-config")
            }
          }
          status { isOk() }
        }
        .andReturn()
        .response
        .contentAsString
    log.info("Swagger config response: $jsonStr")
  }
}
