package io.github.truenine.composeserver.depend.servlet.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.slf4j
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

private val log = slf4j<DefaultJacksonObjectMapperConfiguration>()

@Configuration
class DefaultJacksonObjectMapperConfiguration {
  companion object {
    /** ## This default bean name may change */
    const val SPRING_DEFAULT_OBJECT_MAPPER_BEAN_NAME = "jacksonObjectMapper"
    const val DEFAULT_OBJECT_MAPPER_BEAN_NAME = "defaultObjectMapper"
  }

  @Primary
  @Order(Ordered.HIGHEST_PRECEDENCE)
  @ConditionalOnMissingBean(ObjectMapper::class, name = [SPRING_DEFAULT_OBJECT_MAPPER_BEAN_NAME])
  @Bean(name = [DEFAULT_OBJECT_MAPPER_BEAN_NAME])
  fun defaultObjectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper {
    log.debug("Registering default object mapper")
    return builder.createXmlMapper(false).build()
  }
}
