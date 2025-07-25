package io.github.truenine.composeserver.security.crypto.domain

import io.github.truenine.composeserver.domain.IEccKeyPair
import io.github.truenine.composeserver.domain.enc.EccKeyPair
import io.github.truenine.composeserver.typing.EncryptAlgorithm
import java.security.PrivateKey
import java.security.PublicKey

class EccExtKeyPair(override val publicKey: PublicKey, override val privateKey: PrivateKey, override val algorithm: EncryptAlgorithm = EncryptAlgorithm.ECC) :
  IEccKeyPair by EccKeyPair(publicKey, privateKey, algorithm), IEccExtKeyPair
