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
package net.yan100.compose.security.autoconfig

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import net.yan100.compose.core.extensionfunctions.base64Decode
import net.yan100.compose.core.models.EccKeyPair
import net.yan100.compose.core.models.PemFormat
import net.yan100.compose.core.models.RsaKeyPair
import net.yan100.compose.core.util.encrypt.IKeysRepo
import net.yan100.compose.core.util.encrypt.Keys
import org.springframework.core.io.ClassPathResource

class FileKeyRepo(private val baseDir: String = "keys") : IKeysRepo {
  private fun isPem(content: String): Boolean {
    return content.startsWith(PemFormat.BEGIN_START)
  }

  private fun load(path: String): String {
    return BufferedReader(
        InputStreamReader(BufferedInputStream(ClassPathResource("$baseDir/$path").inputStream))
      )
      .use { it.readText() }
  }

  private fun readBase64(name: String): String {
    val a = load(name)
    return if (isPem(a)) PemFormat(a).content else a.base64Decode()
  }

  override fun findRsaPrivetKeyByName(name: String): RSAPrivateKey? {
    return Keys.readRsaPrivateKeyByBase64(readBase64(name))
  }

  override fun findRsaPublicKeyByName(name: String): RSAPublicKey? {
    return Keys.readRsaPublicKeyByBase64(readBase64(name))
  }

  override fun findEccPrivateKeyByName(name: String): PrivateKey? {
    return Keys.readEccPrivateKeyByBase64(readBase64(name))
  }

  override fun findEccPublicKeyByName(name: String): PublicKey? {
    return Keys.readEccPublicKeyByBase64(readBase64(name))
  }

  override fun jwtEncryptDataIssuerEccKeyPair(): EccKeyPair? {
    return findEccKeyPairByName("jwt_issuer_enc.key", "jwt_verifier_enc.pem")
  }

  override fun jwtEncryptDataVerifierKey(): PrivateKey? {
    return findEccPrivateKeyByName("jwt_verifier_enc.pem")
  }

  override fun jwtSignatureIssuerRsaKeyPair(): RsaKeyPair? {
    return findRsaKeyPairByName("jwt_verifier.key", "jwt_issuer.pem")
  }

  override fun jwtSignatureVerifierRsaPublicKey(): RSAPublicKey? {
    return findRsaPublicKeyByName("jwt_verifier.key")
  }
}