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
package net.yan100.compose.security.jwt.consts


import net.yan100.compose.security.crypto.Keys
import java.security.PrivateKey
import java.security.interfaces.RSAPublicKey

data class VerifierParam<S : Any, E : Any>(
  val token: String,
  val subjectTargetType: Class<S>? = null,
  var encryptDataTargetType: Class<E>? = null,
  var signatureKey: RSAPublicKey? = null,
  var contentEncryptEccKey: PrivateKey? = null,
  var id: String? = null,
  var issuer: String? = null,
) {
  fun isRequireDecrypted(): Boolean = this.encryptDataTargetType != null

  fun contentEncryptEccKeyFromBase64(base64Key: String) {
    this.contentEncryptEccKey = Keys.readEccPrivateKeyByBase64(base64Key)
  }

  fun signatureKeyFromBase64(base64Key: String) {
    this.signatureKey = Keys.readRsaPublicKeyByBase64(base64Key)!!
  }
}
