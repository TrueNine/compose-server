package io.github.truenine.composeserver.security.autoconfig

import io.github.truenine.composeserver.security.annotations.EnableRestSecurity
import io.github.truenine.composeserver.slf4j
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
 * Spring Security fallback policy registrar when no SecurityPolicyBean is defined.
 *
 * @author TrueNine
 * @since 2022-12-14
 */
@Configuration
@ConditionalOnMissingBean(SecurityPolicyBean::class)
class DisableSecurityPolicyBean {
  companion object {
    @JvmStatic private val log = slf4j(DisableSecurityPolicyBean::class)
  }

  @Bean
  @Throws(Exception::class)
  fun disableSecurityFilterChain(security: HttpSecurity): SecurityFilterChain {
    log.warn("In production, please enable WebSecurity using {} and configure {}", EnableRestSecurity::class.java.name, SecurityPolicyBean::class.java.name)
    return security
      .csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
      .authorizeHttpRequests { a -> a.anyRequest().permitAll() }
      .logout { obj: LogoutConfigurer<HttpSecurity?> -> obj.permitAll() }
      .build()
  }

  @Bean
  fun ssr(): UserDetailsService {
    log.warn("Registering a temporary InMemoryUserDetailsManager")
    return InMemoryUserDetailsManager()
  }
}
