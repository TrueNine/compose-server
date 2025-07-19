package io.github.truenine.composeserver.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.truenine.composeserver.typing.EncryptAlgorithm
import java.io.Serializable
import java.security.PrivateKey
import java.security.PublicKey

interface IKeyPair : Serializable {
  val publicKey: PublicKey
  val privateKey: PrivateKey
  val algorithm: EncryptAlgorithm

  operator fun component1(): PublicKey = publicKey

  operator fun component2(): PrivateKey = privateKey

  operator fun component3(): EncryptAlgorithm = algorithm

  @get:JsonIgnore
  val publicKeyBytes: ByteArray
    get() = publicKey.encoded

  @get:JsonIgnore
  val privateKeyBytes: ByteArray
    get() = privateKey.encoded
}
