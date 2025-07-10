package io.github.truenine.composeserver.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.truenine.composeserver.typing.EncryptAlgorithmTyping
import java.io.Serializable
import java.security.PrivateKey
import java.security.PublicKey

interface IKeyPair : Serializable {
  val publicKey: PublicKey
  val privateKey: PrivateKey
  val algorithm: EncryptAlgorithmTyping

  fun component1(): PublicKey = publicKey

  fun component2(): PrivateKey = privateKey

  fun component3(): EncryptAlgorithmTyping = algorithm

  @get:JsonIgnore
  val publicKeyBytes: ByteArray
    get() = publicKey.encoded

  @get:JsonIgnore
  val privateKeyBytes: ByteArray
    get() = privateKey.encoded
}
