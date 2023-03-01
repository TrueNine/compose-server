package io.tn.core.spring.autoconfig

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * spring security 安全策略配置器
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@Configuration
open class SecurityAutoConfig {

  /**
   * 配置主要的密码加密策略
   * 目前使用 BCrypt 2a 14 策略
   *
   * @param ctx spring 容器上下文
   */
  @Bean
  @Primary
  @ConditionalOnMissingBean
  open fun bCryptPasswordEncoder(ctx: ApplicationContext?): PasswordEncoder {
    val bCryptPasswordEncoder = BCryptPasswordEncoder(
      BCryptPasswordEncoder
        .BCryptVersion.`$2A`,
      14
    )
    log.info("注册 bCryptPasswordEncoder = {}", bCryptPasswordEncoder)
    return bCryptPasswordEncoder
  }

  companion object {
    @JvmStatic
    private val log: Logger =
      LoggerFactory.getLogger(SecurityAutoConfig::class.java)
  }
}
