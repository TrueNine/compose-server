package net.yan100.compose.domain

import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

interface IRsaKeyPair : IKeyPair {
  override val publicKey: RSAPublicKey
  override val privateKey: RSAPrivateKey

  override fun component1(): RSAPublicKey = publicKey

  override fun component2(): RSAPrivateKey = privateKey
}
