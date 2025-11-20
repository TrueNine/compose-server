package io.github.truenine.composeserver.depend.servlet.autoconfig

import io.github.truenine.composeserver.slf4j
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import tools.jackson.databind.ObjectMapper

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
  fun defaultObjectMapper(): ObjectMapper {
    log.debug("Registering default object mapper - delegating to depend-jackson auto-configuration")
    // This bean is now a placeholder that will be overridden by depend-jackson's JacksonAutoConfiguration
    // If depend-jackson is not present, Spring Boot's default Jackson auto-configuration will provide the ObjectMapper
    throw IllegalStateException("This bean should be overridden by depend-jackson's JacksonAutoConfiguration")
  }
}
