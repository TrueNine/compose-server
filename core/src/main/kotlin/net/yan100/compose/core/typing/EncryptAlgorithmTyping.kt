/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.core.typing

import net.yan100.compose.core.typing.EncryptAlgorithmTyping.entries

/**
 * 算法
 *
 * @author TrueNine
 * @since 2022-10-28
 */
enum class EncryptAlgorithmTyping(
  private val alg: String,
  val padding: String
) : StringTyping {
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
    @JvmStatic
    fun findVal(v: String?) = entries.find { it.alg == v }
  }
}
