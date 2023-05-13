package net.yan100.compose.core.autoconfig

import net.yan100.compose.core.lang.AnyTypingConverterFactory
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebAutoConfiguration : WebMvcConfigurer {
  override fun addFormatters(registry: FormatterRegistry) {
    registry.addConverterFactory(AnyTypingConverterFactory())
  }
}
