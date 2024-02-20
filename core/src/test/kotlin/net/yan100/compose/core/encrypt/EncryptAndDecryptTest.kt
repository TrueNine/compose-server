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

import io.mockk.InternalPlatformDsl.toStr
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class EncryptAndDecryptTest {
  @Test
  fun testAesEncryptAndDecrypt() {
    val key = Keys.generateAesKey()!!
    val bKey = Keys.writeAesKeyToBase64(key)!!
    val base64Key = Keys.readAesKeyByBase64(bKey)!!
    println(base64Key)
    val text = "我日你妈"
    val cipher = Encryptors.encryptByAesKey(base64Key, text)!!
    println(cipher)
    val plain = Encryptors.decryptByAesKey(base64Key, cipher)
    println(plain)
    assertEquals(plain, text)
  }

  @Test
  fun testEccEncryptAndDecrypt() {
    val eccPair = Keys.generateEccKeyPair()!!
    val pri = Keys.readEccPrivateKeyByBase64(eccPair.eccPrivateKeyBase64)!!
    val pub = Keys.readEccPublicKeyByBase64(eccPair.eccPublicKeyBase64)!!

    val sb = StringBuilder()
    for (i in 0..1000) {
      sb.append("我艹我艹我艹我艹我艹我艹我艹我艹")
    }
    val str = sb.toString()
    val cipher = Encryptors.encryptByEccPublicKey(pub, str)!!
    val text = Encryptors.decryptByEccPrivateKey(pri, cipher)
    assertEquals(str, text)
  }

  @Test
  fun testRsaEncAndDec() {
    val pair = Keys.generateRsaKeyPair()
    val text = "测试数据测试数据测试数据测试数据"
    val cipher = Encryptors.encryptByRsaPublicKey(pair!!.rsaPublicKey!!, text)
    val plain = Encryptors.decryptByRsaPrivateKey(pair.rsaPrivateKey!!, cipher!!)

    assertEquals(text, plain)
  }

  @Test
  fun testAes() {
    val key = Keys.generateAesKey()!!
    val data = "我是你爹"
    val enc = Encryptors.encryptByAesKey(key, data)!!
    val dec = Encryptors.decryptByAesKey(key, enc)
    println(enc)
    println(dec)
    assertNotEquals(data, enc)
    assertEquals(data, dec)
  }

  @Test
  fun testGenerateKeyBase64() {
    val ab = Keys.generateRsaKeyPair()!!
    val metaCode = ab.rsaPublicKey!!.encoded
    val base64Byte = Base64Helper.decodeToByte(String(ab.rsaPublicKeyBase64Byte))
    val strBase64 = Base64Helper.decodeToByte(ab.rsaPublicKeyBase64)
    assertTrue(
      """
      metaCode = ${metaCode.toStr()}
      base64de = ${base64Byte.toStr()}
      strBas64 = ${strBase64.toStr()}
    """
        .trimIndent()
    ) {
      metaCode.contentEquals(base64Byte)
      base64Byte.contentEquals(strBase64)
    }

    val cd = Keys.readRsaKeyPair(ab.rsaPublicKeyBase64, ab.rsaPrivateKeyBase64)
    assertEquals(ab, cd)
  }

  @Test
  fun `test sha1 encrypt`() {
    val sha1 = Encryptors.signatureBySha1("我的")
    println(sha1.sha1)
  }
}
