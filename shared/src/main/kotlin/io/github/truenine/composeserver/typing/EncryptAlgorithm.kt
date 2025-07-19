package io.github.truenine.composeserver.typing

import io.github.truenine.composeserver.IStringTyping

/**
 * 算法
 *
 * @author TrueNine
 * @since 2022-10-28
 */
enum class EncryptAlgorithmTyping(private val alg: String, val padding: String) : IStringTyping {
  /** ecc */
  ECC("EC", "SHA256withECDSA"),

  /** rsa */
  RSA("RSA", "RSA/ECB/PKCS1Padding"),

  /** SHA256withRSA */
  SHA256_WITH_RSA("SHA256withRSA", ""),

  /** aes */
  AES("AES", "AES/ECB/PKCS5Padding");

  override val value: String = this.alg

  companion object {
    @JvmStatic fun findVal(v: String?) = entries.find { it.alg == v }
  }
}
