package com.truenine.component.core.encrypt

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import java.nio.charset.StandardCharsets
import java.util.StringJoiner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EncryptorsTest : AbstractTestNGSpringContextTests() {

  @Test
  fun testGetCharset() {
    assertTrue("charset 从 utf8 改变 ${Encryptors.charset}") {
      Encryptors.charset == StandardCharsets.UTF_8
    }
  }

  private fun getRsaKeyPair() = Keys.generateRsaKeyPair()

  @Test
  fun testEncryptByRsaPublicKeyBase64() {
    val rsa = getRsaKeyPair()!!
    val data = "我日你娘"
    val cipher = Encryptors.encryptByRsaPublicKeyBase64(rsa.rsaPublicKeyBase64, data)!!
    assertFalse("$data $cipher") { data == cipher }
    println(cipher)
    val plain = Encryptors.decryptByRsaPrivateKeyBase64(rsa.rsaPrivateKeyBase64, cipher)!!
    assertEquals(plain, data)
    println(plain)
  }

  @Test
  fun testEncryptByRsaPublicKey() {
    error("没有测试")
  }

  @Test
  fun testEncryptByRsaPrivateKey() {
  }

  @Test
  fun testEncryptByEccPublicKey() {
  }

  @Test
  fun testDecryptByEccPrivateKey() {
  }

  @Test
  fun testDecryptByRsaPrivateKeyBase64() {
  }

  @Test
  fun testDecryptByRsaPrivateKey() {
  }

  @Test
  fun testEncryptByAesKeyBase64() {
  }

  @Test
  fun testEncryptByAesKey() {
  }

  @Test
  fun testDecryptByAesKeyBase64() {
  }

  @Test
  fun testDecryptByAesKey() {
  }

  @Test
  fun `testSharding$core`() {
  }
}
