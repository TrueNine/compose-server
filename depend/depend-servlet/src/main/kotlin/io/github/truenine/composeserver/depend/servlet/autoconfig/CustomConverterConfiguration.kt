package io.github.truenine.composeserver.depend.servlet.autoconfig

import io.github.truenine.composeserver.depend.servlet.converters.*
import io.github.truenine.composeserver.slf4j
import org.springframework.context.annotation.*

private val log = slf4j(CustomConverterConfiguration::class)

@Configuration
class CustomConverterConfiguration {
  @Bean
  @Primary
  fun timestampToLocalDateTimeConverter(): JavaLocalDateTimeConverter {
    log.debug("register spring local datetime converter")
    return JavaLocalDateTimeConverter()
  }

  @Bean
  @Primary
  fun timestampToLocalDateConverter(): JavaLocalDateConverter {
    log.debug("register spring local date converter")
    return JavaLocalDateConverter()
  }

  @Bean
  @Primary
  fun timestampToLocalTimeConverter(): JavaLocalTimeConverter {
    log.debug("register spring parameter local time converter")
    return JavaLocalTimeConverter()
  }
}
