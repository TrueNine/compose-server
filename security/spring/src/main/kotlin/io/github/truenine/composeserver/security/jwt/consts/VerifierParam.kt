package io.github.truenine.composeserver.security.jwt.consts

import io.github.truenine.composeserver.security.crypto.CryptographicKeyManager
import java.security.PrivateKey
import java.security.interfaces.RSAPublicKey

data class VerifierParam<S : Any, E : Any>(
  val token: String,
  val subjectTargetType: Class<S>? = null,
  var encryptDataTargetType: Class<E>? = null,
  var signatureKey: RSAPublicKey? = null,
  var contentEncryptEccKey: PrivateKey? = null,
  var id: String? = null,
  var issuer: String? = null,
) {
  fun isRequireDecrypted(): Boolean = this.encryptDataTargetType != null

  fun contentEncryptEccKeyFromBase64(base64Key: String) {
    this.contentEncryptEccKey = CryptographicKeyManager.readEccPrivateKeyByBase64(base64Key)
  }

  fun signatureKeyFromBase64(base64Key: String) {
    this.signatureKey = CryptographicKeyManager.readRsaPublicKeyByBase64(base64Key)!!
  }
}
