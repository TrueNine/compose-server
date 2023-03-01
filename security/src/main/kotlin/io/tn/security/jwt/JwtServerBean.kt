package io.tn.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import io.tn.core.encrypt.Keys
import io.tn.security.properties.JwtProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
open class JwtServerBean(
  private val jp: JwtProperties
) {
  @Bean
  @Primary
  open fun jwtServer(mapper: ObjectMapper): JwtServer {
    return JwtServer.creator(
      Keys.fromRsa(jp.privateKeyClassPath, jp.publicKeyClassPath),
      jp.expiredDuration,
      jp.issuer
    ).setMapper(mapper)
  }
}
