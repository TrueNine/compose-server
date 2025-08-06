package io.github.truenine.composeserver.security.crypto.domain

import io.github.truenine.composeserver.domain.IKeyPair
import io.github.truenine.composeserver.security.crypto.IBase64

interface IExtKeyPair : IKeyPair {
  val publicKeyBase64ByteArray: ByteArray
    get() = IBase64.encodeToByte(publicKeyBytes)

  val publicKeyBase64: String
    get() = IBase64.encode(publicKeyBytes)

  val privateKeyBase64ByteArray: ByteArray
    get() = IBase64.encodeToByte(privateKeyBytes)

  val privateKeyBase64: String
    get() = IBase64.encode(privateKeyBytes)
}
