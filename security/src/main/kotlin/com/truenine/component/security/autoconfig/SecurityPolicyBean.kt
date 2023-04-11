package com.truenine.component.security.autoconfig

import com.truenine.component.core.lang.LogKt
import com.truenine.component.security.annotations.EnableRestSecurity
import com.truenine.component.security.models.SecurityPolicyDefineModel
import com.truenine.component.security.spring.security.SecurityExceptionAdware
import com.truenine.component.security.spring.security.SecurityUserDetailsService
import lombok.extern.slf4j.Slf4j
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.collections.component1
import kotlin.collections.component2

@Slf4j
@Configuration
@EnableWebSecurity
open class SecurityPolicyBean {
  @Bean
  @Primary
  @ConditionalOnBean(SecurityPolicyDefineModel::class)
  open fun securityDetailsService(desc: SecurityPolicyDefineModel): SecurityUserDetailsService {
    require(null != desc.service) {
      "注册的模型 desc 为空 $desc"
    }
    return desc.service
  }

  @Bean
  @Primary
  @ConditionalOnBean(SecurityPolicyDefineModel::class)
  open fun securityExceptionAdware(desc: SecurityPolicyDefineModel): SecurityExceptionAdware {
    require(desc.exceptionAdware != null) {
      "注册的模型 异常过滤器为空 $desc"
    }
    return desc.exceptionAdware
  }

  @Bean
  @Primary
  open fun securityFilterChain(
    httpSecurity: HttpSecurity,
    desc: SecurityPolicyDefineModel,
    ctx: ApplicationContext
  ): SecurityFilterChain {
    val anno = getAnno(ctx)
    val anonymous = desc.anonymousPatterns
    anonymous += listOf(*anno.loginUrl)

    if (anno.allowSwagger) {
      anonymous += arrayOf(
        "/v3/api-docs/**",
        "/v3/api-docs.yaml",
        "/doc.html**",
        "/swagger-ui/**"
      )
    }
    if (anno.allowWebJars) {
      anonymous +=
        arrayOf(
          "/webjars/**",
          "/errors/**",
          "/error/**",
          "/favicon.ico"
        )
    }
    httpSecurity.addFilterBefore(
      desc.preValidFilter,
      UsernamePasswordAuthenticationFilter::class.java
    )
    httpSecurity
      .csrf().disable()
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .authorizeHttpRequests()
      .requestMatchers(*anonymous.toTypedArray())
      .anonymous()
      .anyRequest().authenticated()
      .and()
      .userDetailsService(desc.service)
    httpSecurity.exceptionHandling()
      .authenticationEntryPoint(desc.exceptionAdware)
      .accessDeniedHandler(desc.exceptionAdware)
    log.trace("注册 Security 过滤器链 httpSecurity = {}", httpSecurity)
    return httpSecurity.build()
  }

  @Bean
  @Primary
  open fun authenticationManager(ac: AuthenticationConfiguration): AuthenticationManager? {
    log.debug("注册 AuthenticationManager config = {}", ac)
    val manager = ac.authenticationManager
    log.debug("获取到 AuthManager = {}", manager != null)
    return manager
  }

  companion object {
    @JvmStatic
    private val log = LogKt.getLog(this::class)

    @JvmStatic
    private fun getAnno(ctx: ApplicationContext): EnableRestSecurity {
      val a = ctx.getBeansWithAnnotation(EnableRestSecurity::class.java)
      val s = AtomicReference<EnableRestSecurity>()
      a.forEach { (_: String?, v: Any) ->
        s.set(
          v.javaClass.getAnnotation(
            EnableRestSecurity::class.java
          )
        )
        log.trace(
          "获取到：{}，注解于：{}",
          s.get(),
          v.javaClass.name
        )
      }
      return s.get()
    }
  }
}
