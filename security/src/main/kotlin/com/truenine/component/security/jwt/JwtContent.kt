package com.truenine.component.security.jwt

data class JwtContent<S : Any, E : Any>(
  var subject: S,
  var encryptedData: E? = null
) {

}
