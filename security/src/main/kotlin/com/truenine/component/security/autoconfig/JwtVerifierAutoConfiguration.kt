package com.truenine.component.security.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.core.encrypt.KeysRepository
import com.truenine.component.security.jwt.JwtIssuer
import com.truenine.component.security.jwt.JwtVerifier
import com.truenine.component.security.properties.JwtProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class JwtVerifierAutoConfiguration(
  private val jp: JwtProperties,
  private val kr: KeysRepository
) {
  @Bean
  @ConditionalOnMissingBean(value = [JwtVerifier::class, JwtIssuer::class])
  fun jwtVerifier(mapper: ObjectMapper): JwtVerifier {
    // TODO 完成此类

    return JwtVerifier.createVerifier()
      .issuer(jp.issuer)
      .contentDecryptKey(kr.jwtEncryptDataVerifierKey()!!)
      .signatureVerifyKey(kr.jwtSignatureVerifierRsaPublicKey()!!)
      .encryptDataKeyName(jp.encryptDataKeyName)
      .serializer(mapper)
      .build()
  }
}
