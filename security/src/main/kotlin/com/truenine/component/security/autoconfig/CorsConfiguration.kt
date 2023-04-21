package com.truenine.component.security.autoconfig

import com.truenine.component.core.http.Methods
import com.truenine.component.core.lang.LogKt
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

  companion object {
    private val log = LogKt.getLog(this::class)
  }
}
