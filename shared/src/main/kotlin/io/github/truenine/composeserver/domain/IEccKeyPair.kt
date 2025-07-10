package io.github.truenine.composeserver.domain

import java.security.PrivateKey
import java.security.PublicKey

interface IEccKeyPair : IKeyPair {
  override val publicKey: PublicKey
  override val privateKey: PrivateKey

  override fun component1(): PublicKey = publicKey

  override fun component2(): PrivateKey = privateKey
}
