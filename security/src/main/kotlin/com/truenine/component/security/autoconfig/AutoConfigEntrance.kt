package com.truenine.component.security.autoconfig

import com.truenine.component.security.properties.JwtProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import

/**
 * 自动配置入口
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@EnableConfigurationProperties(JwtProperties::class)
@Import(DisableSecurityPolicyBean::class, CaptchaAutoConfiguration::class, CorsConfiguration::class)
class AutoConfigEntrance

