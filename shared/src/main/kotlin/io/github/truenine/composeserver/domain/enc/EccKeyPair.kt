package io.github.truenine.composeserver.domain.enc

import io.github.truenine.composeserver.domain.IEccKeyPair
import io.github.truenine.composeserver.typing.EncryptAlgorithm
import java.security.PrivateKey
import java.security.PublicKey

/**
 * ecc 密钥对
 *
 * @author TrueNine
 * @since 2022-12-15
 */
class EccKeyPair(override val publicKey: PublicKey, override val privateKey: PrivateKey, override val algorithm: EncryptAlgorithm = EncryptAlgorithm.ECC) :
  IEccKeyPair
