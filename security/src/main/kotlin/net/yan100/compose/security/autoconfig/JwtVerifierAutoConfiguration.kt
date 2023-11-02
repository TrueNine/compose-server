package net.yan100.compose.security.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.encrypt.IKeysRepository
import net.yan100.compose.security.jwt.JwtIssuer
import net.yan100.compose.security.jwt.JwtVerifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(net.yan100.compose.security.properties.JwtProperties::class)
class JwtVerifierAutoConfiguration(
  private val jp: net.yan100.compose.security.properties.JwtProperties,
  private val kr: IKeysRepository
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
