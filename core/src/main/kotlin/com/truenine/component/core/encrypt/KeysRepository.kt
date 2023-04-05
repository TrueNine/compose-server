package com.truenine.component.core.encrypt

import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import javax.crypto.spec.SecretKeySpec

@JvmDefaultWithCompatibility
interface KeysRepository {
  fun basicRsaKeyPair(): RsaKeyPair? = null
  fun basicEccKeyPair(): EccKeyPair? = null
  fun basicAesKey(): SecretKeySpec? = null

  fun jwtSignatureIssuerRsaKeyPair(): RsaKeyPair? = basicRsaKeyPair()

  fun jwtSignatureVerifierRsaPublicKey(): RSAPublicKey? =
    basicRsaKeyPair()?.rsaPublicKey

  fun jwtEncryptDataIssuerEccKeyPair(): EccKeyPair? =
    basicEccKeyPair()

  fun jwtEncryptDataIssuedKey(): PublicKey? =
    basicEccKeyPair()?.eccPublicKey

  fun jwtEncryptDataVerifierKey(): PrivateKey? =
    basicEccKeyPair()?.eccPrivateKey

  fun databaseEncryptAesSecret(): SecretKeySpec? = basicAesKey()
}
