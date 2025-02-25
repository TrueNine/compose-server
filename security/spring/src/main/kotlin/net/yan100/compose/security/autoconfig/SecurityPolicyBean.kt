package net.yan100.compose.security.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.slf4j
import net.yan100.compose.security.EmptySecurityDetailsService
import net.yan100.compose.security.EmptySecurityExceptionAdware
import net.yan100.compose.security.SecurityPolicyDefine
import net.yan100.compose.security.annotations.EnableRestSecurity
import net.yan100.compose.security.spring.security.SecurityExceptionAdware
import net.yan100.compose.security.spring.security.SecurityPreflightValidFilter
import net.yan100.compose.security.spring.security.SecurityUserDetailsService
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
  fun securityDetailsService(
    desc: SecurityPolicyDefine
  ): SecurityUserDetailsService {
    log.debug("注册 UserDetailsService")
    return desc.service ?: EmptySecurityDetailsService()
  }

  @Bean
  @Primary
  @ConditionalOnBean(SecurityPolicyDefine::class)
  fun securityExceptionAdware(
    policyDefine: SecurityPolicyDefine,
    manager: ObjectMapper,
  ): SecurityExceptionAdware {
    log.debug("注册 ExceptionAdware")
    return policyDefine.exceptionAdware ?: EmptySecurityExceptionAdware(manager)
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
    if (enableAnnotation == null) log.warn("未配置 安全注解 注解")
    val mergedConfigAnnotation = enableAnnotation ?: EnableRestSecurity()

    val allowPatterns = policyDefine.anonymousPatterns
    allowPatterns += listOf(*mergedConfigAnnotation.loginUrl)
    allowPatterns += listOf(*mergedConfigAnnotation.logoutUrl)
    allowPatterns += listOf(*mergedConfigAnnotation.allowPatterns)

    if (mergedConfigAnnotation.allowSwagger)
      allowPatterns += policyDefine.swaggerPatterns
    if (mergedConfigAnnotation.allowWebJars) allowPatterns += "/webjars/**"

    if (policyDefine.preValidFilter != null) {
      httpSecurity.addFilterBefore(
        policyDefine.preValidFilter,
        UsernamePasswordAuthenticationFilter::class.java,
      )
    } else log.warn("未配置验证过滤器 {}", SecurityPreflightValidFilter::class.java)

    // 打印错误日志
    if (allowPatterns.contains("/**")) log.error("配置上下文内包含 /** ，将会放行所有域")

    httpSecurity
      // 关闭 csrf
      .csrf { it.disable() }
      // 关闭 session
      .sessionManagement {
        it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      }

    httpSecurity.cors { it.configurationSource { cors } }
    httpSecurity.authorizeHttpRequests {
      it.requestMatchers(*allowPatterns.toTypedArray()).permitAll()

      log.debug("任意请求是否需要认证 = {}", mergedConfigAnnotation.anyRequestAuthed)
      if (mergedConfigAnnotation.anyRequestAuthed) it.anyRequest().denyAll()
      else {
        if (policyDefine.accessor != null) {
          log.debug("设定 access = {}", policyDefine.accessor)
          it.anyRequest().access(policyDefine.accessor)
        } else {
          it.anyRequest().permitAll()
        }
      }
    }
    httpSecurity.userDetailsService(
      policyDefine.service ?: EmptySecurityDetailsService()
    )

    // 配置异常处理器
    if (policyDefine.exceptionAdware != null) {
      httpSecurity.exceptionHandling {
        it
          .authenticationEntryPoint(policyDefine.exceptionAdware)
          .accessDeniedHandler(policyDefine.exceptionAdware)
      }
    } else log.warn("未注册安全异常过滤器 {}", SecurityExceptionAdware::class.java)

    log.debug("注册 Security 过滤器链 httpSecurity = {}", httpSecurity)
    log.debug("allow Patterns = {}", allowPatterns)
    return httpSecurity.build()
  }

  @Bean
  @Primary
  fun authenticationManager(
    ac: AuthenticationConfiguration
  ): AuthenticationManager? {
    log.debug("注册 AuthenticationManager config = {}", ac)
    val manager = ac.authenticationManager
    log.debug("获取到 AuthManager = {}", manager != null)
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
