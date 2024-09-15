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
package net.yan100.compose.security.crpyot

import io.mockk.InternalPlatformDsl.toStr
import net.yan100.compose.core.consts.IRegexes
import net.yan100.compose.security.crypto.Encryptors
import net.yan100.compose.security.crypto.IBase64
import net.yan100.compose.security.crypto.Keys
import net.yan100.compose.testtookit.log
import kotlin.test.*

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
    val pri = Keys.readEccPrivateKeyByBase64(eccPair.privateKeyBase64)!!
    val pub = Keys.readEccPublicKeyByBase64(eccPair.publicKeyBase64)!!

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
    val cipher = Encryptors.encryptByRsaPublicKey(pair!!.publicKey, text)
    val plain = Encryptors.decryptByRsaPrivateKey(pair.privateKey, cipher!!)

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
  fun `ensure rsa key pair can be serialized and deserialized`() {
    val serKeyPair = Keys.generateRsaKeyPair()!!
    val metaCode = serKeyPair.publicKey.encoded
    val base64Byte = IBase64.decodeToByte(String(serKeyPair.publicKeyBase64ByteArray))
    val strBase64 = IBase64.decodeToByte(serKeyPair.publicKeyBase64)
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

    val desKeyPair = Keys.readRsaKeyPair(serKeyPair.publicKeyBase64, serKeyPair.privateKeyBase64)
    assertEquals(serKeyPair.publicKeyBase64, desKeyPair.publicKeyBase64)
    assertEquals(serKeyPair.privateKeyBase64, desKeyPair.privateKeyBase64)
    assertContentEquals(serKeyPair.publicKeyBase64ByteArray, desKeyPair.publicKeyBase64ByteArray)
    assertContentEquals(serKeyPair.privateKeyBase64ByteArray, desKeyPair.privateKeyBase64ByteArray)
  }

  @Test
  fun `sha1 encrypt`() {
    val plainText = "我的"
    val sha1 = Encryptors.signatureBySha1(plainText)
    assertNotEquals(plainText, sha1)
    log.info(sha1)
    assertTrue("生成不符合 sh1 标准") { IRegexes.SH1.toRegex().matches(sha1) }

    repeat(1000) {
      val plan = Keys.generateRandomAsciiString(32)
      val sha2 = Encryptors.signatureBySha1(plan)
      assertNotEquals(plan, sha2)
      log.info("plan: {}, sha1: {}", plan, sha2)
      assertTrue("生成不符合 sh1 标准") { IRegexes.SH1.toRegex().matches(sha2) }
    }
  }
}
