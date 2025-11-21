package io.github.truenine.composeserver.depend.servlet.autoconfig

import io.github.truenine.composeserver.logger
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import tools.jackson.databind.ObjectMapper

private val log = logger<DefaultJacksonObjectMapperConfiguration>()

@Configuration
class DefaultJacksonObjectMapperConfiguration {
  companion object {
    /** ## This default bean name may change */
    const val SPRING_DEFAULT_OBJECT_MAPPER_BEAN_NAME = "jacksonJsonObjectMapper"
    const val DEFAULT_OBJECT_MAPPER_BEAN_NAME = "defaultObjectMapper"
  }

  @Primary
  @Order(Ordered.HIGHEST_PRECEDENCE)
  @ConditionalOnMissingBean(ObjectMapper::class, name = [SPRING_DEFAULT_OBJECT_MAPPER_BEAN_NAME])
  @Bean(name = [DEFAULT_OBJECT_MAPPER_BEAN_NAME])
  fun defaultObjectMapper(): ObjectMapper {
    return ObjectMapper()
  }
}
