package net.yan100.compose.security.autoconfig

import net.yan100.compose.core.http.Methods
import net.yan100.compose.core.lang.slf4j
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * spring mvc 跨域请求统一配置
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@Configuration
class CorsConfiguration : WebMvcConfigurer {
  override fun addCorsMappings(registry: CorsRegistry) {
    log.debug("注册跨域组件为允许全部跨域通行")
    registry
      .addMapping("/**")
      .allowedOriginPatterns("*")
      .allowedMethods(*Methods.all())
      .allowedHeaders("*")
      .exposedHeaders("*")
      .allowCredentials(true)
      .maxAge(3600)
  }

  @Bean
  fun corsConfigurationSource(): CorsConfiguration {
    log.debug("注册 spring security 的跨域全局配置")
    val all = CorsConfiguration.ALL
    val c = CorsConfiguration()
    c.addAllowedOriginPattern(all)
    Methods.all().toList().forEach(c::addAllowedMethod)
    c.addExposedHeader(all)
    c.allowCredentials = true
    c.maxAge = 3600
    c.addAllowedHeader(all)

    return c
  }

  companion object {
    private val log = slf4j(CorsConfiguration::class)
  }
}
