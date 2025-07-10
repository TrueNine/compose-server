package io.github.truenine.composeserver.security.crypto.domain

import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import javax.crypto.spec.SecretKeySpec

interface IKeysRepo {
  fun basicRsaKeyPair(): IRsaExtKeyPair? = null

  fun basicEccKeyPair(): IEccExtKeyPair? = null

  fun basicAesKey(): SecretKeySpec? = null

  fun findRsaKeyPairByName(publicKeyName: String, privateKeyName: String): IRsaExtKeyPair? {
    return RsaExtKeyPair(findRsaPublicKeyByName(publicKeyName)!!, findRsaPrivetKeyByName(privateKeyName)!!)
  }

  fun findEccKeyPairByName(publicKeyName: String, privateKeyName: String): IEccExtKeyPair? {
    return EccExtKeyPair(findEccPublicKeyByName(publicKeyName)!!, findEccPrivateKeyByName(privateKeyName)!!)
  }

  fun findAesSecretByName(name: String): SecretKeySpec? = null

  fun findEccPublicKeyByName(name: String): PublicKey? = null

  fun findRsaPublicKeyByName(name: String): RSAPublicKey? = null

  fun findEccPrivateKeyByName(name: String): PrivateKey? = null

  fun findRsaPrivetKeyByName(name: String): RSAPrivateKey? = null

  fun jwtSignatureIssuerRsaKeyPair(): IRsaExtKeyPair? {
    return basicRsaKeyPair()
  }

  fun jwtSignatureVerifierRsaPublicKey(): RSAPublicKey? {
    return basicRsaKeyPair()?.publicKey
  }

  fun jwtEncryptDataIssuerEccKeyPair(): IEccExtKeyPair? {
    return basicEccKeyPair()
  }

  fun jwtEncryptDataVerifierKey(): PrivateKey? {
    return basicEccKeyPair()?.privateKey
  }

  fun databaseEncryptAesSecret(): SecretKeySpec? {
    return basicAesKey()
  }
}
