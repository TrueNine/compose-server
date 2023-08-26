package net.yan100.compose.core.typing

import com.fasterxml.jackson.annotation.JsonValue
import net.yan100.compose.core.lang.StringTyping

/**
 * 算法
 *
 * @author TrueNine
 * @since 2022-10-28
 */
enum class EncryptAlgorithmTyping(private val alg: String) : StringTyping {
  /**
   * ecc
   */
  ECC("EC"),

  /**
   * ecc padding
   */
  ECC_PADDING("SHA256withECDSA"),

  /**
   * rsa
   */
  RSA("RSA"),

  /**
   * SHA256withRSA
   */
  SHA256_WITH_RSA("SHA256withRSA"),

  /**
   * rsa padding
   */
  RSA_PADDING("RSA/ECB/PKCS1Padding"),

  /**
   * aes
   */
  AES("AES");

  @JsonValue
  override fun getValue(): String? {
    return this.alg
  }

  companion object {
    @JvmStatic
    fun findVal(v: String?) = entries.find { it.alg == v }
  }
}
