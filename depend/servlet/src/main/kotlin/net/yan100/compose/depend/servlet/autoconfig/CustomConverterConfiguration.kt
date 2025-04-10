package net.yan100.compose.depend.servlet.autoconfig

import net.yan100.compose.depend.servlet.converters.JavaLocalDateConverter
import net.yan100.compose.depend.servlet.converters.JavaLocalDateTimeConverter
import net.yan100.compose.depend.servlet.converters.JavaLocalTimeConverter
import net.yan100.compose.slf4j
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

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
