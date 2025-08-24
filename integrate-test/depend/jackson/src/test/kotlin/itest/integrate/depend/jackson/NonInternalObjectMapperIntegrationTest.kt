package itest.integrate.depend.jackson

import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import jakarta.annotation.Resource
import org.springframework.boot.test.context.SpringBootTest
import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest
class NonInternalObjectMapperIntegrationTest {
  @Resource(name = JacksonAutoConfiguration.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME)
  private lateinit var mapper: ObjectMapper

  @BeforeTest
  fun setup() {
    assertNotNull(mapper)
  }

  @Test
  fun check_with_injection() {
    assertNotNull(mapper)
  }
}
