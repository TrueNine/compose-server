package net.yan100.compose.security.autoconfig

import net.yan100.compose.core.lang.slf4j
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
        log.warn(
            "生产环境请启用 WebSecurity, 使用 {} 来启用并配置完成 {}",
            EnableRestSecurity::class.java.name,
            SecurityPolicyBean::class.java.name
        )
        return security
            .csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
            .authorizeHttpRequests { a ->
                a.anyRequest().permitAll()
            }
            .logout { obj: LogoutConfigurer<HttpSecurity?> -> obj.permitAll() }
            .build()
    }

    @Bean
    fun ssr(): UserDetailsService {
        log.warn("当前注册了一个临时的 InMemoryUserDetailsManager")
        return InMemoryUserDetailsManager()
    }
}
