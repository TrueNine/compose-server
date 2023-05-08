package com.truenine.component.core.autoconfig

import com.truenine.component.core.lang.AnyTypingConverterFactory
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebAutoConfiguration : WebMvcConfigurer {
  override fun addFormatters(registry: FormatterRegistry) {
    registry.addConverterFactory(AnyTypingConverterFactory())
  }
}
