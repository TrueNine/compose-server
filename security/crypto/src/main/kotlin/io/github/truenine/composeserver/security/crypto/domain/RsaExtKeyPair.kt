package io.github.truenine.composeserver.security.crypto.domain

import io.github.truenine.composeserver.domain.IRsaKeyPair
import io.github.truenine.composeserver.domain.enc.RsaKeyPair
import io.github.truenine.composeserver.typing.EncryptAlgorithmTyping
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

class RsaExtKeyPair(
  override val publicKey: RSAPublicKey,
  override val privateKey: RSAPrivateKey,
  override val algorithm: EncryptAlgorithmTyping = EncryptAlgorithmTyping.RSA,
) : IRsaKeyPair by RsaKeyPair(publicKey, privateKey, algorithm), IRsaExtKeyPair
