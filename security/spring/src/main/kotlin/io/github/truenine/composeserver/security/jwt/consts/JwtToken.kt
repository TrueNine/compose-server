package io.github.truenine.composeserver.security.jwt.consts

import java.time.LocalDateTime

data class JwtToken<S : Any, E : Any>(
  var subject: S? = null,
  var decryptedData: E? = null,
  var expireDateTime: LocalDateTime? = null,
  var id: String? = null,
  var signatureAlgName: String? = null,
  var isExpired: Boolean = false,
)
