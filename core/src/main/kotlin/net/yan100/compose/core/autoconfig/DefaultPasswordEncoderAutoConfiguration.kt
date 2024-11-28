@file:Suppress("DEPRECATION")

package net.yan100.compose.core.autoconfig

import net.yan100.compose.core.slf4j
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

private val log = slf4j<DefaultPasswordEncoderAutoConfiguration>()

@Configuration
class DefaultPasswordEncoderAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(PasswordEncoder::class)
  @Suppress("DEPRECATION")
  fun messageDigestPasswordEncoder(ctx: ApplicationContext?): MessageDigestPasswordEncoder {
    val encoder = MessageDigestPasswordEncoder("MD5")
    log.error("默认在使用不安全的 PasswordEncoder MD5 加密算法，这仅用于测试或启动项目使用，请尽快更换其他可用的加密算法，passwordEncoder: {}", encoder)
    return encoder
  }
}
