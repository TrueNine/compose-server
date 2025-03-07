package net.yan100.compose.core.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import java.security.PrivateKey
import java.security.PublicKey
import net.yan100.compose.core.typing.EncryptAlgorithmTyping

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
