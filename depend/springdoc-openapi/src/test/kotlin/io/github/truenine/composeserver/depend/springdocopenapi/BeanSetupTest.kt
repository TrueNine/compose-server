package io.github.truenine.composeserver.depend.springdocopenapi

import io.github.truenine.composeserver.testtoolkit.annotations.SpringServletTest
import io.github.truenine.composeserver.testtoolkit.log
import jakarta.annotation.Resource
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringServletTest
class BeanSetupTest {
  lateinit var mock: MockMvc
    @Resource set

  @BeforeTest
  fun setup() {
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
    log.info(jsonStr)
  }
}
