package com.truenine.component.security.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.security.jwt.JwtIssuer
import com.truenine.component.security.jwt.JwtVerifier
import com.truenine.component.security.properties.JwtProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Files
import java.nio.file.Paths

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
open class JwtVerifierAutoConfiguration(
  private val jp: JwtProperties
) {
  @Bean
  @ConditionalOnMissingBean(value = [JwtVerifier::class, JwtIssuer::class])
  open fun authJwtClient(mapper: ObjectMapper): JwtVerifier {
    val pubKey = Files.readString(Paths.get(jp.publicKeyClassPath))
    val priKey = Files.readString(Paths.get(jp.privateKeyClassPath))
    return JwtVerifier.createVerifier()
      .build()
  }
}
