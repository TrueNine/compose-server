package com.truenine.component.core.encrypt

/**
 * 算法
 *
 * @author TrueNine
 * @since 2022-10-28
 */
enum class EncryptAlgorithm(private val alg: String) {
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
   * rsa padding
   */
  RSA_PADDING("RSA/ECB/PKCS1Padding"),

  /**
   * aes
   */
  AES("AES");

  fun str(): String {
    return alg
  }
}
