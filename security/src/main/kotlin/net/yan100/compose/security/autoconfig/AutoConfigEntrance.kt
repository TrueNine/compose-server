package net.yan100.compose.security.autoconfig

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import

/**
 * 自动配置入口
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@EnableConfigurationProperties(net.yan100.compose.security.properties.JwtProperties::class)
@Import(
  net.yan100.compose.security.autoconfig.DisableSecurityPolicyBean::class,
  CaptchaAutoConfiguration::class,
  CorsConfiguration::class
)
class AutoConfigEntrance

