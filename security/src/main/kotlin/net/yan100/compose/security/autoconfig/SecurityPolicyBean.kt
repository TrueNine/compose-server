package net.yan100.compose.security.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.security.defaults.EmptySecurityDetailsService
import net.yan100.compose.security.defaults.EmptySecurityExceptionAdware
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
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*

@Configuration
@EnableWebSecurity
class SecurityPolicyBean {
  @Bean
  @Primary
  @ConditionalOnBean(net.yan100.compose.security.models.SecurityPolicyDefineModel::class)
  fun securityDetailsService(desc: net.yan100.compose.security.models.SecurityPolicyDefineModel): SecurityUserDetailsService {
    return desc.service ?: EmptySecurityDetailsService()
  }

  @Bean
  @Primary
  @ConditionalOnBean(net.yan100.compose.security.models.SecurityPolicyDefineModel::class)
  fun securityExceptionAdware(policyDefine: net.yan100.compose.security.models.SecurityPolicyDefineModel, manager: ObjectMapper): SecurityExceptionAdware {
    return policyDefine.exceptionAdware ?: EmptySecurityExceptionAdware(manager)
  }

  @Bean
  @Primary
  fun securityFilterChain(
    httpSecurity: HttpSecurity,
    policyDefine: net.yan100.compose.security.models.SecurityPolicyDefineModel,
    applicationContext: ApplicationContext
  ): SecurityFilterChain {
    val enableAnnotation = getAnno(applicationContext)
    require(enableAnnotation != null) { "无法正常获取到注解 ${net.yan100.compose.security.annotations.EnableRestSecurity::class}" }
    val anonymousPatterns = policyDefine.anonymousPatterns

    anonymousPatterns += listOf(*enableAnnotation.loginUrl)
    anonymousPatterns += listOf(*enableAnnotation.allowPatterns)

    // 是否放行swagger
    if (enableAnnotation.allowSwagger) {
      anonymousPatterns += policyDefine.swaggerPatterns
    }

    if (enableAnnotation.allowWebJars) {
      anonymousPatterns += "/webjars/**"
    }

    if (policyDefine.preValidFilter != null) {
      httpSecurity.addFilterBefore(
        policyDefine.preValidFilter,
        UsernamePasswordAuthenticationFilter::class.java
      )
    } else {
      log.warn("未配置验证过滤器 {}", SecurityPreflightValidFilter::class.java)
    }

    // 打印错误日志
    if (anonymousPatterns.contains("/**")) {
      log.error("配置上下文内包含 /** ，将会放行所有域")
    }

    httpSecurity
      // 关闭 csrf
      .csrf().disable()
      // 关闭 session
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
      .authorizeHttpRequests()
      // 放行链接
      .requestMatchers(*anonymousPatterns.toTypedArray())
      // 其他链接一律放行
      .anonymous().anyRequest().authenticated().and()
      .userDetailsService(policyDefine.service ?: EmptySecurityDetailsService())

    // 配置异常处理器
    if (policyDefine.exceptionAdware != null) {
      httpSecurity.exceptionHandling()
        .authenticationEntryPoint(policyDefine.exceptionAdware)
        .accessDeniedHandler(policyDefine.exceptionAdware)
    } else {
      log.warn("未注册安全异常过滤器 {}", SecurityExceptionAdware::class.java)
    }

    log.debug("注册 Security 过滤器链 httpSecurity = {}", httpSecurity)
    return httpSecurity.build()
  }

  @Bean
  @Primary
  fun authenticationManager(ac: AuthenticationConfiguration): AuthenticationManager? {
    log.debug("注册 AuthenticationManager config = {}", ac)
    val manager = ac.authenticationManager
    log.debug("获取到 AuthManager = {}", manager != null)
    return manager
  }

  companion object {
    @JvmStatic
    private val log = slf4j(this::class)

    @JvmStatic
    private fun getAnno(ctx: ApplicationContext): net.yan100.compose.security.annotations.EnableRestSecurity? {
      return ctx
        .getBeansWithAnnotation(net.yan100.compose.security.annotations.EnableRestSecurity::class.java)
        .map { it.value }
        .map { it.javaClass.getAnnotation(net.yan100.compose.security.annotations.EnableRestSecurity::class.java) }
        .first()
    }
  }
}
