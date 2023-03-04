package com.truenine.component.security.jwt

import com.truenine.component.core.encrypt.Keys
import java.security.PrivateKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import kotlin.reflect.KClass

data class VerifierParams<S : Any, E : Any>(
  val token: String,
  val subjectTargetType: KClass<S>,
  var encryptDataTargetType: KClass<E>? = null,
  var signatureKey: RSAPublicKey,
  var contentEncryptEccKey: PrivateKey? = null,
  var id: String? = null,
  var issuer: String? = null
) {
  fun contentEncryptEccKeyFromBase64(base64Key: String) {
    this.contentEncryptEccKey = Keys.readEccPrivateKeyByBase64(base64Key)
  }

  fun signatureKeyFromBase64(base64Key: String) {
    this.signatureKey = Keys.readRsaPublicKeyByBase64(base64Key)!!
  }
}
