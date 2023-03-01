package io.tn.core.spring.autoconfig

import io.tn.core.api.http.Methods
import io.tn.core.lang.KtLogBridge
import lombok.extern.slf4j.Slf4j
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * spring mvc 跨域请求统一配置
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@Slf4j
@Configuration
open class CorsConfiguration : WebMvcConfigurer {
  override fun addCorsMappings(registry: CorsRegistry) {
    log.info("注册跨域组件为允许全部跨域通行")
    registry
      .addMapping("/**")
      .allowedOriginPatterns("*")
      .allowedMethods(*Methods.all())
      .allowedHeaders("*")
      .exposedHeaders("*")
      .allowCredentials(true)
      .maxAge(3600)
  }

  companion object {
    private val log = KtLogBridge.getLog(CorsConfiguration::class.java)
  }
}
