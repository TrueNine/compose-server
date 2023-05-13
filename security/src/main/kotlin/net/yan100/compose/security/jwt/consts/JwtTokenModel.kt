package net.yan100.compose.security.jwt.consts

import java.time.LocalDateTime

data class JwtTokenModel<S : Any, E : Any>(
  var subject: S? = null,
  var decryptedData: E? = null,
  var expireDateTime: LocalDateTime? = null,
  var id: String? = null,
  var signatureAlgName: String? = null
)
