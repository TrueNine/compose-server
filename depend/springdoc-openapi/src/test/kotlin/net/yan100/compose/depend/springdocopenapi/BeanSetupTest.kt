package net.yan100.compose.depend.springdocopenapi

import jakarta.annotation.Resource
import net.yan100.compose.testtoolkit.annotations.SpringServletTest
import net.yan100.compose.testtoolkit.log
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

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
