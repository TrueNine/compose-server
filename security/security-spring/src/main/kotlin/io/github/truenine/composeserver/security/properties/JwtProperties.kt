package io.github.truenine.composeserver.security.properties

import io.github.truenine.composeserver.consts.SpringBootConfigurationPropertiesPrefixes
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding

@ConfigurationPropertiesBinding
@ConfigurationProperties(prefix = SpringBootConfigurationPropertiesPrefixes.SECURITY_JWT)
data class JwtProperties(
  var publicKeyClassPath: String = "security/pub.key",
  var privateKeyClassPath: String = "security/pri.key",
  var encryptDataKeyName: String = "edt",
  var issuer: String = "T-SERVER",
  var expiredDuration: Long = (2 * 60 * 60 * 60 * 1000).toLong(),
)
