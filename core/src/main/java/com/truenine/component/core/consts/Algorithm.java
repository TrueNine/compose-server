package com.truenine.component.core.consts;

/**
 * 算法
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public enum Algorithm {
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
  private final String alg;

  Algorithm(String alg) {
    this.alg = alg;
  }

  public String str() {
    return this.alg;
  }
}
