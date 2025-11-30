package io.github.truenine.composeserver.security.autoconfig

import io.github.truenine.composeserver.security.crypto.domain.IKeysRepo
import io.github.truenine.composeserver.security.jwt.JwtIssuer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.*
import tools.jackson.databind.ObjectMapper
import java.time.Duration
import java.time.temporal.ChronoUnit

@Configuration
@EnableConfigurationProperties(io.github.truenine.composeserver.security.properties.JwtProperties::class)
class JwtIssuerAutoConfiguration(private val jp: io.github.truenine.composeserver.security.properties.JwtProperties, private val keysRepository: IKeysRepo) {
  @Bean
  @Primary
  fun jwtIssuer(mapper: ObjectMapper): JwtIssuer {
    // TODO complete this configuration class
    val sig = keysRepository.jwtSignatureIssuerRsaKeyPair()!!
    val enc = keysRepository.jwtEncryptDataIssuerEccKeyPair()!!
    return JwtIssuer.createIssuer()
      .signatureIssuerKey(sig.privateKey)
      .signatureVerifyKey(sig.publicKey)
      .contentEncryptKey(enc.publicKey)
      .contentDecryptKey(enc.privateKey)
      .expireFromDuration(Duration.of(7, ChronoUnit.DAYS))
      .serializer(mapper)
      .encryptDataKeyName(jp.encryptDataKeyName)
      .issuer(jp.issuer)
      .expire(jp.expiredDuration)
      .build()
  }
}
