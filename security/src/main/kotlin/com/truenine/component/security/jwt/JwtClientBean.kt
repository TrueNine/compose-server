package com.truenine.component.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.core.encrypt.Keys
import com.truenine.component.security.properties.JwtProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
open class JwtClientBean(
  private val jp: JwtProperties
) {
  @Bean
  open fun authJwtClient(mapper: ObjectMapper): JwtClient {
    val pair = Keys.fromRsa(jp.privateKeyClassPath, jp.publicKeyClassPath)
    return JwtClient.creator(pair, jp.issuer).setMapper(mapper)
  }
}
