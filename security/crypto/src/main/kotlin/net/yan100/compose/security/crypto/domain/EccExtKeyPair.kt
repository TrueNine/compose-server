package net.yan100.compose.security.crypto.domain

import net.yan100.compose.domain.IEccKeyPair
import net.yan100.compose.domain.enc.EccKeyPair
import net.yan100.compose.typing.EncryptAlgorithmTyping
import java.security.PrivateKey
import java.security.PublicKey

class EccExtKeyPair(
  override val publicKey: PublicKey,
  override val privateKey: PrivateKey,
  override val algorithm: EncryptAlgorithmTyping = EncryptAlgorithmTyping.ECC,
) : IEccKeyPair by EccKeyPair(publicKey, privateKey, algorithm), IEccExtKeyPair
