package com.truenine.component.security.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "component.jwt")
data class JwtProperties(
  var publicKeyClassPath: String = "security/pub.key",
  var privateKeyClassPath: String = "security/pri.key",
  var issuer: String = "T-SERVER",
  var expiredDuration: Long = 2 * 60 * 60 * 60 * 1000
)