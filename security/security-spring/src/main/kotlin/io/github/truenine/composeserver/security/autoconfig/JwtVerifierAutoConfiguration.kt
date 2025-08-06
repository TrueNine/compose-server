package io.github.truenine.composeserver.security.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.security.crypto.domain.IKeysRepo
import io.github.truenine.composeserver.security.jwt.JwtIssuer
import io.github.truenine.composeserver.security.jwt.JwtVerifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(io.github.truenine.composeserver.security.properties.JwtProperties::class)
class JwtVerifierAutoConfiguration(private val jp: io.github.truenine.composeserver.security.properties.JwtProperties, private val kr: IKeysRepo) {
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
