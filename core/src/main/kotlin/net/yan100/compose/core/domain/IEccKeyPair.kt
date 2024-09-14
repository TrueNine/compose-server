package net.yan100.compose.core.domain

import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

interface IEccKeyPair : IKeyPair {
  override val publicKey: PublicKey
  override val privateKey: PrivateKey
  override fun component1(): PublicKey = publicKey
  override fun component2(): PrivateKey = privateKey
}
