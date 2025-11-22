package io.github.truenine.composeserver.security.autoconfig

import io.github.truenine.composeserver.consts.IMethods
import io.github.truenine.composeserver.slf4j
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Global Spring MVC CORS configuration.
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@Configuration
class CorsConfiguration : WebMvcConfigurer {
  override fun addCorsMappings(registry: CorsRegistry) {
    log.debug("Register CORS component to allow all cross-origin requests")
    registry
      .addMapping("/**")
      .allowedOriginPatterns("*")
      .allowedMethods(*IMethods.all())
      .allowedHeaders("*")
      .exposedHeaders("*")
      .allowCredentials(true)
      .maxAge(3600)
  }

  @Bean(name = ["org.springframework.web.cors.CorsConfiguration"])
  fun corsConfiguration(): CorsConfiguration {
    log.debug("Register Spring Security global CORS configuration")
    val all = CorsConfiguration.ALL
    val c = CorsConfiguration()
    c.addAllowedOriginPattern(all)
    IMethods.all().toList().forEach(c::addAllowedMethod)
    c.addExposedHeader(all)
    c.allowCredentials = true
    c.maxAge = 3600
    c.addAllowedHeader(all)
    c.exposedHeaders = listOf("*")
    return c
  }

  companion object {
    private val log = slf4j(CorsConfiguration::class)
  }
}
