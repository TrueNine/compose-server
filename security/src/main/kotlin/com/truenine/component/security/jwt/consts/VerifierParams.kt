package com.truenine.component.security.jwt.consts

import com.truenine.component.core.encrypt.Keys
import java.security.PrivateKey
import java.security.interfaces.RSAPublicKey
import kotlin.reflect.KClass

data class VerifierParams<S : Any, E : Any>(
  val token: String,
  val subjectTargetType: KClass<S>? = null,
  var encryptDataTargetType: KClass<E>? = null,
  var signatureKey: RSAPublicKey? = null,
  var contentEncryptEccKey: PrivateKey? = null,
  var id: String? = null,
  var issuer: String? = null
) {
  fun isRequireDecrypted(): Boolean =
    this.encryptDataTargetType != null


  fun contentEncryptEccKeyFromBase64(base64Key: String) {
    this.contentEncryptEccKey = Keys.readEccPrivateKeyByBase64(base64Key)
  }

  fun signatureKeyFromBase64(base64Key: String) {
    this.signatureKey = Keys.readRsaPublicKeyByBase64(base64Key)!!
  }
}
