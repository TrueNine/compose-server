/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.core.util.encrypt

import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import javax.crypto.spec.SecretKeySpec
import net.yan100.compose.core.models.EccKeyPair
import net.yan100.compose.core.models.RsaKeyPair

interface IKeysRepo {
  fun basicRsaKeyPair(): RsaKeyPair? = null

  fun basicEccKeyPair(): EccKeyPair? = null

  fun basicAesKey(): SecretKeySpec? = null

  fun findRsaKeyPairByName(publicKeyName: String, privateKeyName: String): RsaKeyPair? {
    return RsaKeyPair().also {
      it.rsaPublicKey = findRsaPublicKeyByName(publicKeyName)!!
      it.rsaPrivateKey = findRsaPrivetKeyByName(privateKeyName)!!
    }
  }

  fun findEccKeyPairByName(publicKeyName: String, privateKeyName: String): EccKeyPair? {
    return EccKeyPair().also {
      it.eccPublicKey = findEccPublicKeyByName(publicKeyName)!!
      it.eccPrivateKey = findEccPrivateKeyByName(privateKeyName)!!
    }
  }

  fun findAesSecretByName(name: String): SecretKeySpec? = null

  fun findEccPublicKeyByName(name: String): PublicKey? = null

  fun findRsaPublicKeyByName(name: String): RSAPublicKey? = null

  fun findEccPrivateKeyByName(name: String): PrivateKey? = null

  fun findRsaPrivetKeyByName(name: String): RSAPrivateKey? = null

  fun jwtSignatureIssuerRsaKeyPair(): RsaKeyPair? {
    return basicRsaKeyPair()
  }

  fun jwtSignatureVerifierRsaPublicKey(): RSAPublicKey? {
    return basicRsaKeyPair()?.rsaPublicKey
  }

  fun jwtEncryptDataIssuerEccKeyPair(): EccKeyPair? {
    return basicEccKeyPair()
  }

  fun jwtEncryptDataVerifierKey(): PrivateKey? {
    return basicEccKeyPair()?.eccPrivateKey
  }

  fun databaseEncryptAesSecret(): SecretKeySpec? {
    return basicAesKey()
  }
}
