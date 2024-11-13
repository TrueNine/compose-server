/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.security.autoconfig

import net.yan100.compose.core.consts.IMethods
import net.yan100.compose.core.slf4j
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
      .allowedMethods(*IMethods.all())
      .allowedHeaders("*")
      .exposedHeaders("*")
      .allowCredentials(true)
      .maxAge(3600)
  }

  @Bean(name = ["org.springframework.web.cors.CorsConfiguration"])
  fun corsConfiguration(): CorsConfiguration {
    log.debug("注册 spring security 的跨域全局配置")
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
