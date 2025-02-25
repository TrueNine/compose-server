package net.yan100.compose.core.domain.enc

import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import net.yan100.compose.core.domain.IRsaKeyPair
import net.yan100.compose.core.typing.EncryptAlgorithmTyping

/**
 * rsa密钥对
 *
 * @author TrueNine
 * @since 2022-12-09
 */
class RsaKeyPair(
  override val publicKey: RSAPublicKey,
  override val privateKey: RSAPrivateKey,
  override val algorithm: EncryptAlgorithmTyping = EncryptAlgorithmTyping.RSA,
) : IRsaKeyPair
