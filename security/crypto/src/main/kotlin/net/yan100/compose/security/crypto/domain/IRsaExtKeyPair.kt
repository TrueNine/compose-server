package net.yan100.compose.security.crypto.domain

import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import net.yan100.compose.core.domain.IRsaKeyPair

interface IRsaExtKeyPair : IRsaKeyPair, IExtKeyPair {
  override fun component1(): RSAPublicKey

  override fun component2(): RSAPrivateKey
}
