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

import net.yan100.compose.core.slf4j
import net.yan100.compose.security.annotations.EnableRestSecurity
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

/**
 * spring security 策略注册器
 *
 * @author TrueNine
 * @since 2022-12-14
 */
@Configuration
@ConditionalOnMissingBean(SecurityPolicyBean::class)
class DisableSecurityPolicyBean {
  companion object {
    @JvmStatic
    private val log = slf4j(DisableSecurityPolicyBean::class)
  }

  @Bean
  @Throws(Exception::class)
  fun disableSecurityFilterChain(security: HttpSecurity): SecurityFilterChain {
    log.warn("生产环境请启用 WebSecurity, 使用 {} 来启用并配置完成 {}", EnableRestSecurity::class.java.name, SecurityPolicyBean::class.java.name)
    return security
      .csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
      .authorizeHttpRequests { a -> a.anyRequest().permitAll() }
      .logout { obj: LogoutConfigurer<HttpSecurity?> -> obj.permitAll() }
      .build()
  }

  @Bean
  fun ssr(): UserDetailsService {
    log.warn("当前注册了一个临时的 InMemoryUserDetailsManager")
    return InMemoryUserDetailsManager()
  }
}
