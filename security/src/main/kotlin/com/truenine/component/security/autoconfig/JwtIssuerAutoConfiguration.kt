package com.truenine.component.security.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.security.jwt.JwtIssuer
import com.truenine.component.security.properties.JwtProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.nio.file.Files
import java.nio.file.Paths

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
open class JwtIssuerAutoConfiguration(
  private val jp: JwtProperties
) {
  @Bean
  @Primary
  open fun jwtIssuer(mapper: ObjectMapper): JwtIssuer {
    return JwtIssuer.createIssuer().build()
  }
}
