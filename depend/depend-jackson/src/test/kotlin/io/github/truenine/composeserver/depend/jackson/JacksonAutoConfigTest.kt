package io.github.truenine.composeserver.depend.jackson

import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.Test
import kotlin.test.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.ApplicationContext
import tools.jackson.databind.ObjectMapper

@WebMvcTest
@ImportAutoConfiguration(JacksonAutoConfiguration::class)
class JacksonAutoConfigTest {
  @Autowired lateinit var applicationContext: ApplicationContext

  @Test
  fun mappers_should_be_registered() {
    val objectMappers = applicationContext.getBeansOfType(ObjectMapper::class.java).values
    log.info("mappers: {}", objectMappers)
    assertTrue(objectMappers.isNotEmpty(), "ObjectMapper beans should be registered")
  }
}
