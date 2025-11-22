package io.github.truenine.composeserver.domain.enc

import io.github.truenine.composeserver.domain.IRsaKeyPair
import io.github.truenine.composeserver.enums.EncryptAlgorithm
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

/**
 * RSA key pair wrapper.
 *
 * @author TrueNine
 * @since 2022-12-09
 */
class RsaKeyPair(
  override val publicKey: RSAPublicKey,
  override val privateKey: RSAPrivateKey,
  override val algorithm: EncryptAlgorithm = EncryptAlgorithm.RSA,
) : IRsaKeyPair
