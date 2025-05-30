package net.yan100.compose.domain.enc

import net.yan100.compose.domain.IEccKeyPair
import net.yan100.compose.typing.EncryptAlgorithmTyping
import java.security.PrivateKey
import java.security.PublicKey

/**
 * ecc 密钥对
 *
 * @author TrueNine
 * @since 2022-12-15
 */
class EccKeyPair(
  override val publicKey: PublicKey,
  override val privateKey: PrivateKey,
  override val algorithm: EncryptAlgorithmTyping = EncryptAlgorithmTyping.ECC,
) : IEccKeyPair
