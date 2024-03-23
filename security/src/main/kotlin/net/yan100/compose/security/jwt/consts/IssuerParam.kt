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

import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.time.Duration
import net.yan100.compose.core.util.encrypt.Keys

data class IssuerParam<S : Any?, E : Any?>(
  var encryptedDataObj: E? = null,
  var id: String? = null,
  var issuer: String? = null,
  var duration: Duration? = null,
  var signatureKey: RSAPrivateKey? = null,
  var contentEncryptEccKey: PublicKey? = null,
  var subjectObj: S? = null,
) {
  fun containSubject(): Boolean = this.subjectObj != null

  fun containEncryptContent(): Boolean = null != this.encryptedDataObj

  fun contentEncryptEccKeyFromBase64(base64Key: String) {
    this.contentEncryptEccKey = Keys.readRsaPublicKeyByBase64(base64Key)
  }

  fun signatureKeyFromBase64(base64Key: String) {
    this.signatureKey = Keys.readRsaPrivateKeyByBase64(base64Key)!!
  }
}
