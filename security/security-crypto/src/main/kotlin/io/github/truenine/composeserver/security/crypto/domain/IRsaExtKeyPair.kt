package io.github.truenine.composeserver.security.crypto.domain

import io.github.truenine.composeserver.domain.IRsaKeyPair
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

interface IRsaExtKeyPair : IRsaKeyPair, IExtKeyPair {
  override fun component1(): RSAPublicKey

  override fun component2(): RSAPrivateKey
}
