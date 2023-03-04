package com.truenine.component.security.jwt

import com.truenine.component.core.encrypt.Keys
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Duration


data class IssuerParams<S : Any, E : Any>(
  var encryptedDataObj: E? = null,
  var id: String? = null,
  var issuer: String? = null,
  var duration: Duration? = null,
  var signatureKey: RSAPrivateKey,
  var contentEncryptEccKey: PublicKey? = null
) {
  @Suppress("UNCHECKED_CAST")
  var subjectObj: S? = null
    get() = if (null == field) "none" as S else field

  fun contentEncryptEccKeyFromBase64(base64Key: String) {
    this.contentEncryptEccKey = Keys.readRsaPublicKeyByBase64(base64Key)
  }

  fun signatureKeyFromBase64(base64Key: String) {
    this.signatureKey = Keys.readRsaPrivateKeyByBase64(base64Key)!!
  }
}
