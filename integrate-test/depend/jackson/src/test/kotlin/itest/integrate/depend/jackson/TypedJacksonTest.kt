package itest.integrate.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import io.github.truenine.composeserver.testtoolkit.log
import jakarta.annotation.Resource
import kotlin.test.Test
import kotlin.test.assertEquals
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [TestEntrance::class])
class TypedJacksonTest {
  @Resource(name = JacksonAutoConfiguration.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME) lateinit var mapper: ObjectMapper

  @Test
  fun inject_test() {
    val a = mapOf("a" to "b")
    val json = mapper.writeValueAsString(a)
    log.info("json: {}", json)
    assertEquals($$"""{"@class":"java.util.Collections$SingletonMap","a":"b"}""", json)
  }
}
