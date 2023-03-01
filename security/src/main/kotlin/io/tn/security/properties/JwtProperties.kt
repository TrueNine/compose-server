package io.tn.security.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "tserver.jwt")
data class JwtProperties(
  var publicKeyClassPath: String = "security/pub.key",
  var privateKeyClassPath: String = "security/pri.key",
  var issuer: String = "T-SERVER",
  var expiredDuration: Long = 2 * 60 * 60 * 60 * 1000
)
