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
package net.yan100.compose.core.encrypt

import javax.crypto.spec.SecretKeySpec
import net.yan100.compose.core.lang.slf4j

class FileKeysRepo(
  private val keyDest: String = "security",
  eccKeyPairPaths: Pair<String, String> = "ecc_public.key" to "ecc_private.key",
  rsaKeyPairPaths: Pair<String, String> = "rsa_public.key" to "rsa_private.key",
  aesPaths: String = "aes.key"
) : IKeysRepo {

  companion object {
    @JvmStatic private val log = slf4j(FileKeysRepo::class)
  }

  private var rsaKeyPair: RsaKeyPair? = null
  private var eccKeyPair: EccKeyPair? = null
  private var aesKey: SecretKeySpec? = null

  init {
    aesKey = Keys.readAesKeyByBase64(read(aesPaths))
    rsaKeyPair = Keys.readRsaKeyPair(read(rsaKeyPairPaths.first), read(rsaKeyPairPaths.second))!!
    eccKeyPair = Keys.readEccKeyPair(read(eccKeyPairPaths.first), read(eccKeyPairPaths.second))!!
  }

  override fun basicEccKeyPair(): EccKeyPair? {
    return eccKeyPair
  }

  override fun basicRsaKeyPair(): RsaKeyPair? {
    return rsaKeyPair
  }

  override fun basicAesKey(): SecretKeySpec? {
    return this.aesKey
  }

  private fun read(name: String): String {
    val text = javaClass.classLoader.getResource("${this.keyDest}/$name")!!.readText()
    log.trace("text = {}", text)
    return text
  }
}
