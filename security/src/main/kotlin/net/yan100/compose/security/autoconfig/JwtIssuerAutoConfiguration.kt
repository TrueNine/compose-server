package net.yan100.compose.security.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.encrypt.KeysRepository
import net.yan100.compose.security.jwt.JwtIssuer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.time.Duration
import java.time.temporal.ChronoUnit

@Configuration
@EnableConfigurationProperties(net.yan100.compose.security.properties.JwtProperties::class)
class JwtIssuerAutoConfiguration(
  private val jp: net.yan100.compose.security.properties.JwtProperties,
  private val keysRepository: KeysRepository
) {
  @Bean
  @Primary
  fun jwtIssuer(mapper: ObjectMapper): JwtIssuer {
    // TODO 完成此类
    val sig = keysRepository.jwtSignatureIssuerRsaKeyPair()!!
    val enc = keysRepository.jwtEncryptDataIssuerEccKeyPair()!!
    return JwtIssuer.createIssuer()
      .signatureIssuerKey(sig.rsaPrivateKey)
      .signatureVerifyKey(sig.rsaPublicKey)
      .contentEncryptKey(enc.eccPublicKey)
      .contentDecryptKey(enc.eccPrivateKey)
      .expireFromDuration(Duration.of(7, ChronoUnit.DAYS))
      .serializer(mapper)
      .encryptDataKeyName(jp.encryptDataKeyName)
      .issuer(jp.issuer)
      .expire(jp.expiredDuration)
      .build()
  }
}