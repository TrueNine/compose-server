package net.yan100.compose.security.crypto.domain

import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import net.yan100.compose.core.domain.IRsaKeyPair
import net.yan100.compose.core.domain.enc.RsaKeyPair
import net.yan100.compose.core.typing.EncryptAlgorithmTyping

class RsaExtKeyPair(
  override val publicKey: RSAPublicKey,
  override val privateKey: RSAPrivateKey,
  override val algorithm: EncryptAlgorithmTyping = EncryptAlgorithmTyping.RSA,
) : IRsaKeyPair by RsaKeyPair(publicKey, privateKey, algorithm), IRsaExtKeyPair
