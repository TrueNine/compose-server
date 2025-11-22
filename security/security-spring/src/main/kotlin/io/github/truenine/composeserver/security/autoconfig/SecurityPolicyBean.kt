package io.github.truenine.composeserver.security.autoconfig

import io.github.truenine.composeserver.security.SecurityPolicyDefine
import io.github.truenine.composeserver.security.annotations.EnableRestSecurity
import io.github.truenine.composeserver.security.spring.security.SecurityExceptionAdware
import io.github.truenine.composeserver.security.spring.security.SecurityPreflightValidFilter
import io.github.truenine.composeserver.security.spring.security.SecurityUserDetailsService
import io.github.truenine.composeserver.slf4j
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration

private val log = slf4j(SecurityPolicyBean::class)

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@ConditionalOnBean(SecurityPolicyDefine::class)
class SecurityPolicyBean {

  @Bean
  @Primary
  @ConditionalOnBean(SecurityPolicyDefine::class)
  fun securityDetailsService(desc: SecurityPolicyDefine): SecurityUserDetailsService {
    log.debug("Register UserDetailsService")
    return desc.service ?: error("not register UserDetailsService")
  }

  @Bean
  @Primary
  @ConditionalOnBean(SecurityPolicyDefine::class)
  fun securityExceptionAdware(policyDefine: SecurityPolicyDefine): SecurityExceptionAdware {
    log.debug("Register SecurityExceptionAdware")
    return policyDefine.exceptionAdware ?: error("not register ExceptionAdware")
  }

  @Bean
  @Primary
  @ConditionalOnBean(SecurityPolicyDefine::class)
  fun securityFilterChain(
    httpSecurity: HttpSecurity,
    cors: CorsConfiguration,
    policyDefine: SecurityPolicyDefine,
    applicationContext: ApplicationContext,
  ): SecurityFilterChain {
    val enableAnnotation = getAnno(applicationContext)
    if (enableAnnotation == null) log.warn("Security annotation EnableRestSecurity is not configured")
    val mergedConfigAnnotation = enableAnnotation ?: EnableRestSecurity()

    val allowPatterns = policyDefine.anonymousPatterns
    allowPatterns += listOf(*mergedConfigAnnotation.loginUrl)
    allowPatterns += listOf(*mergedConfigAnnotation.logoutUrl)
    allowPatterns += listOf(*mergedConfigAnnotation.allowPatterns)

    if (mergedConfigAnnotation.allowSwagger) allowPatterns += policyDefine.swaggerPatterns
    if (mergedConfigAnnotation.allowWebJars) allowPatterns += "/webjars/**"

    if (policyDefine.preValidFilter != null) {
      httpSecurity.addFilterBefore(policyDefine.preValidFilter, UsernamePasswordAuthenticationFilter::class.java)
    } else log.warn("Validation filter is not configured: {}", SecurityPreflightValidFilter::class.java)

    // Log potential misconfiguration
    if (allowPatterns.contains("/**")) log.error("Configuration contains '/**', all paths will be permitted")

    httpSecurity
      // Disable CSRF
      .csrf { it.disable() }
      // Disable HTTP session (stateless)
      .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

    httpSecurity.cors { it.configurationSource { cors } }
    httpSecurity.authorizeHttpRequests {
      it.requestMatchers(*allowPatterns.toTypedArray()).permitAll()

      log.debug("Any request requires authentication = {}", mergedConfigAnnotation.anyRequestAuthed)
      if (mergedConfigAnnotation.anyRequestAuthed) it.anyRequest().denyAll()
      else {
        if (policyDefine.accessor != null) {
          log.debug("Set access manager = {}", policyDefine.accessor)
          it.anyRequest().access(policyDefine.accessor)
        } else {
          it.anyRequest().permitAll()
        }
      }
    }
    httpSecurity.userDetailsService(policyDefine.service ?: error(("not register UserDetailsService")))

    // Configure exception handlers
    if (policyDefine.exceptionAdware != null) {
      httpSecurity.exceptionHandling { it.authenticationEntryPoint(policyDefine.exceptionAdware).accessDeniedHandler(policyDefine.exceptionAdware) }
    } else log.warn("Security exception filter is not registered: {}", SecurityExceptionAdware::class.java)

    log.debug("Register Security filter chain, httpSecurity = {}", httpSecurity)
    log.debug("Allow patterns = {}", allowPatterns)
    return httpSecurity.build()
  }

  @Bean
  @Primary
  fun authenticationManager(ac: AuthenticationConfiguration): AuthenticationManager? {
    log.debug("Register AuthenticationManager config = {}", ac)
    val manager = ac.authenticationManager
    log.debug("Obtained AuthenticationManager = {}", manager != null)
    return manager
  }

  companion object {

    @JvmStatic
    private fun getAnno(ctx: ApplicationContext): EnableRestSecurity? {
      return ctx
        .getBeansWithAnnotation(EnableRestSecurity::class.java)
        .map { it.value }
        .map { it.javaClass.getAnnotation(EnableRestSecurity::class.java) }
        .firstOrNull()
    }
  }
}
