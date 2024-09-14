package net.yan100.compose.security.crypto.domain

import net.yan100.compose.core.domain.IRsaKeyPair
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

interface IRsaExtKeyPair : IRsaKeyPair, IExtKeyPair {
  override fun component1(): RSAPublicKey
  override fun component2(): RSAPrivateKey
}
