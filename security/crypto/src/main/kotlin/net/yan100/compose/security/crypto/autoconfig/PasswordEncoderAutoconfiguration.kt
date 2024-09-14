package net.yan100.compose.security.crypto.autoconfig


import net.yan100.compose.core.slf4j
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

private val log = slf4j<PasswordEncoderAutoconfiguration>()

/**
 * spring security 安全策略配置器
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@Configuration
class PasswordEncoderAutoconfiguration {


  /**
   * 配置主要的密码加密策略 目前使用 BCrypt 2a 14 策略
   *
   * @param ctx spring 容器上下文
   */
  @Bean
  @Primary
  fun bCryptPasswordEncoder(ctx: ApplicationContext?): PasswordEncoder {
    val bCryptPasswordEncoder = BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.`$2A`, 14)
    log.trace("注册 bCryptPasswordEncoder = {}", bCryptPasswordEncoder)
    return bCryptPasswordEncoder
  }
}
