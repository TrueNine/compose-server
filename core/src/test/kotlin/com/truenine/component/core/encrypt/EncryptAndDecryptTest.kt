package com.truenine.component.core.encrypt

import org.testng.annotations.Test
import kotlin.test.assertEquals

class EncryptAndDecryptTest {
  @Test
  fun testAesEncryptAndDecrypt() {
    val key = Keys.generateAesKey()!!
    val bKey = Keys.writeAesKeyByBase64(key)!!
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
    val cipher =
      Encryptors.encryptByRsaPublicKey(pair!!.rsaPublicKey, text)
    val plain =
      Encryptors.decryptByRsaPrivateKey(pair!!.rsaPrivateKey, cipher!!)

    assertEquals(text, plain)
  }
}
