package io.github.truenine.composeserver.security.crypto.autoconfig

import io.github.truenine.composeserver.slf4j
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

private val log = slf4j<PasswordEncoderAutoconfiguration>()

/**
 * Spring Security password encoder configuration.
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@Configuration
class PasswordEncoderAutoconfiguration {

  /**
   * Configure the primary password encoding strategy.
   *
   * Currently uses BCrypt 2a with strength 14.
   *
   * @param ctx Spring application context
   */
  @Bean
  @Primary
  fun bCryptPasswordEncoder(ctx: ApplicationContext?): PasswordEncoder {
    val bCryptPasswordEncoder = BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.`$2A`, 14)
    log.trace("register bCryptPasswordEncoder: {}", bCryptPasswordEncoder)
    return bCryptPasswordEncoder
  }
}
