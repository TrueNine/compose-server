package com.truenine.component.security.jwt.consts

import com.truenine.component.core.encrypt.Keys
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.time.Duration


data class IssuerParams<S : Any?, E : Any?>(
  var encryptedDataObj: E? = null,
  var id: String? = null,
  var issuer: String? = null,
  var duration: Duration? = null,
  var signatureKey: RSAPrivateKey? = null,
  var contentEncryptEccKey: PublicKey? = null,
  var subjectObj: S? = null
) {
  fun containSubject(): Boolean =
    this.subjectObj != null

  fun containEncryptContent(): Boolean = null != this.encryptedDataObj

  fun contentEncryptEccKeyFromBase64(base64Key: String) {
    this.contentEncryptEccKey = Keys.readRsaPublicKeyByBase64(base64Key)
  }

  fun signatureKeyFromBase64(base64Key: String) {
    this.signatureKey = Keys.readRsaPrivateKeyByBase64(base64Key)!!
  }
}
