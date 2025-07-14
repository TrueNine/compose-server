package io.github.truenine.composeserver.security.jwt.consts

import io.github.truenine.composeserver.security.crypto.CryptographicKeyManager
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.time.Duration

data class IssuerParam<S : Any?, E : Any?>(
  var encryptedDataObj: E? = null,
  var id: String? = null,
  var issuer: String? = null,
  var duration: Duration? = null,
  var signatureKey: RSAPrivateKey? = null,
  var contentEncryptEccKey: PublicKey? = null,
  var subjectObj: S? = null,
) {
  fun containSubject(): Boolean = this.subjectObj != null

  fun containEncryptContent(): Boolean = null != this.encryptedDataObj

  fun contentEncryptEccKeyFromBase64(base64Key: String) {
    this.contentEncryptEccKey = CryptographicKeyManager.readRsaPublicKeyByBase64(base64Key)
  }

  fun signatureKeyFromBase64(base64Key: String) {
    this.signatureKey = CryptographicKeyManager.readRsaPrivateKeyByBase64(base64Key)!!
  }
}
