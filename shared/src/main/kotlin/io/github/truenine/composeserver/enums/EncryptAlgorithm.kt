package io.github.truenine.composeserver.enums

import com.fasterxml.jackson.annotation.JsonValue
import io.github.truenine.composeserver.IStringEnum

/**
 * 算法
 *
 * @author TrueNine
 * @since 2022-10-28
 */
enum class EncryptAlgorithm(private val alg: String, val padding: String) : IStringEnum {
  /** ecc */
  ECC("EC", "SHA256withECDSA"),

  /** rsa */
  RSA("RSA", "RSA/ECB/PKCS1Padding"),

  /** SHA256withRSA */
  SHA256_WITH_RSA("SHA256withRSA", ""),

  /** aes */
  AES("AES", "AES/ECB/PKCS5Padding");

  @JsonValue override val value: String = this.alg

  companion object {
    @JvmStatic operator fun get(alg: String?): EncryptAlgorithm? = entries.find { it.alg == alg }
  }
}
