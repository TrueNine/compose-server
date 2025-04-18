package net.yan100.compose.security.crypto

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Keys工具类的单元测试
 *
 * 测试覆盖：
 * - 随机字符串生成
 * - RSA密钥对生成和转换
 * - ECC密钥对生成和转换
 * - AES密钥生成和转换
 * - 错误处理和边界条件
 * - 并发安全性
 */
class KeysTest {

  // region 随机字符串测试
  @Test
  fun `generateRandomAsciiString 指定32位长度 返回符合要求的随机字符串`() {
    val length = 32
    val result = Keys.generateRandomAsciiString(length)
    assertEquals(length, result.length)
    assertTrue(result.all { it in '0' .. '9' || it in 'a' .. 'z' || it in 'A' .. 'Z' })
  }

  @Test
  fun `generateRandomAsciiString 长度为0 抛出IllegalArgumentException`() {
    assertFailsWith<IllegalArgumentException> {
      Keys.generateRandomAsciiString(0)
    }
  }

  @Test
  fun `generateRandomAsciiString 长度为负数 抛出IllegalArgumentException`() {
    assertFailsWith<IllegalArgumentException> {
      Keys.generateRandomAsciiString(-1)
    }
  }
  // endregion

  // region RSA密钥测试
  @Test
  fun `generateRsaKeyPair 生成密钥对 返回有效的RSA密钥对`() {
    val keyPair = Keys.generateRsaKeyPair()
    assertNotNull(keyPair?.publicKey)
    assertNotNull(keyPair.privateKey)
  }

  @Test
  fun `RSA密钥 Base64转换 可以正确还原`() {
    val keyPair = Keys.generateRsaKeyPair()
    assertNotNull(keyPair)
    val publicKeyBase64 = Keys.writeKeyToBase64(keyPair.publicKey)
    val privateKeyBase64 = Keys.writeKeyToBase64(keyPair.privateKey)
    assertNotNull(publicKeyBase64)
    assertNotNull(privateKeyBase64)

    val restoredPublicKey = Keys.readRsaPublicKeyByBase64(publicKeyBase64)
    val restoredPrivateKey = Keys.readRsaPrivateKeyByBase64(privateKeyBase64)
    assertNotNull(restoredPublicKey)
    assertNotNull(restoredPrivateKey)

    assertEquals(
      keyPair.publicKey.encoded.toList(), restoredPublicKey.encoded.toList()
    )
    assertEquals(
      keyPair.privateKey.encoded?.toList(), restoredPrivateKey.encoded.toList()
    )
  }

  @Test
  fun `readRsaKeyPair Base64密钥对 可以正确重建`() {
    val originalPair = Keys.generateRsaKeyPair()
    assertNotNull(originalPair)
    val publicKeyBase64 = Keys.writeKeyToBase64(originalPair.publicKey)
    val privateKeyBase64 = Keys.writeKeyToBase64(originalPair.privateKey)

    assertNotNull(publicKeyBase64)
    assertNotNull(privateKeyBase64)

    val rebuiltPair = Keys.readRsaKeyPair(publicKeyBase64, privateKeyBase64)
    assertNotNull(rebuiltPair)
    assertEquals(
      originalPair.publicKey.encoded.toList(), rebuiltPair.publicKey.encoded.toList()
    )
    assertEquals(
      originalPair.privateKey.encoded.toList(), rebuiltPair.privateKey.encoded.toList()
    )
  }
  // endregion

  // region ECC密钥测试
  @Test
  fun `generateEccKeyPair 生成密钥对 返回有效的ECC密钥对`() {
    val keyPair = Keys.generateEccKeyPair()
    assertNotNull(keyPair)
    assertNotNull(keyPair.publicKey)
    assertNotNull(keyPair.privateKey)
  }

  @Test
  fun `ECC密钥 Base64转换 可以正确还原`() {
    val keyPair = Keys.generateEccKeyPair()
    assertNotNull(keyPair)

    val publicKeyBase64 = Keys.writeKeyToBase64(keyPair.publicKey)
    val privateKeyBase64 = Keys.writeKeyToBase64(keyPair.privateKey)
    assertNotNull(publicKeyBase64)
    assertNotNull(privateKeyBase64)

    val restoredPublicKey = Keys.readEccPublicKeyByBase64(publicKeyBase64)
    val restoredPrivateKey = Keys.readEccPrivateKeyByBase64(privateKeyBase64)
    assertNotNull(restoredPublicKey)
    assertNotNull(restoredPrivateKey)

    assertEquals(
      keyPair.publicKey.encoded.toList(), restoredPublicKey.encoded.toList()
    )
    assertEquals(
      keyPair.privateKey.encoded.toList(), restoredPrivateKey.encoded.toList()
    )
  }

  @Test
  fun `readEccKeyPair Base64密钥对 可以正确重建`() {
    val originalPair = Keys.generateEccKeyPair()
    assertNotNull(originalPair)

    val publicKeyBase64 = Keys.writeKeyToBase64(originalPair.publicKey)
    val privateKeyBase64 = Keys.writeKeyToBase64(originalPair.privateKey)

    assertNotNull(publicKeyBase64)
    assertNotNull(privateKeyBase64)
    val rebuiltPair = Keys.readEccKeyPair(publicKeyBase64, privateKeyBase64)
    assertNotNull(rebuiltPair)
    assertEquals(
      originalPair.publicKey.encoded.toList(), rebuiltPair.publicKey.encoded.toList()
    )
    assertEquals(
      originalPair.privateKey.encoded.toList(), rebuiltPair.privateKey.encoded.toList()
    )
  }
  // endregion

  // region AES密钥测试
  @Test
  fun `generateAesKey 默认参数 返回有效的AES密钥`() {
    val key = Keys.generateAesKey()
    assertNotNull(key)
    assertEquals("AES", key.algorithm)
  }

  @Test
  fun `AES密钥 Base64转换 可以正确还原`() {
    val originalKey = Keys.generateAesKey()
    assertNotNull(originalKey)
    val keyBase64 = Keys.writeKeyToBase64(originalKey)
    assertNotNull(keyBase64)

    val restoredKey = Keys.readAesKeyByBase64(keyBase64)
    assertNotNull(restoredKey)

    assertEquals(
      originalKey.encoded.toList(), restoredKey.encoded.toList()
    )
  }

  @ParameterizedTest
  @ValueSource(ints = [128, 192, 256])
  fun `generateAesKey 不同密钥长度 生成对应长度的密钥`(keySize: Int) {
    val key = Keys.generateAesKey(keySize = keySize)
    assertNotNull(key)
    assertEquals(keySize / 8, key.encoded.size)
  }
  // endregion

  // region 错误处理测试
  @Test
  fun `readRsaPublicKeyByBase64 无效Base64 返回null`() {
    val result = Keys.readRsaPublicKeyByBase64("invalid-base64")
    assertNull(result)
  }

  @Test
  fun `readRsaPrivateKeyByBase64 无效Base64 返回null`() {
    val result = Keys.readRsaPrivateKeyByBase64("invalid-base64")
    assertNull(result)
  }

  @Test
  fun `readEccPublicKeyByBase64 无效Base64 返回null`() {
    val result = Keys.readEccPublicKeyByBase64("invalid-base64")
    assertNull(result)
  }

  @Test
  fun `readEccPrivateKeyByBase64 无效Base64 返回null`() {
    val result = Keys.readEccPrivateKeyByBase64("invalid-base64")
    assertNull(result)
  }

  @Test
  fun `readAesKeyByBase64 无效Base64 返回null`() {
    val result = Keys.readAesKeyByBase64("invalid-base64")
    assertNull(result)
  }
  // endregion

  // region 并发测试
  @Test
  fun `generateRandomAsciiString 并发调用 返回正确长度的字符串`() {
    val threadCount = 10
    val iterationsPerThread = 1000

    val threads = List(threadCount) {
      Thread {
        repeat(iterationsPerThread) {
          val result = Keys.generateRandomAsciiString()
          assertEquals(32, result.length)
        }
      }
    }

    threads.forEach { it.start() }
    threads.forEach { it.join() }
  }

  @Test
  fun `generateRsaKeyPair 并发调用 返回有效的密钥对`() {
    val threadCount = 5
    val iterationsPerThread = 10

    val threads = List(threadCount) {
      Thread {
        repeat(iterationsPerThread) {
          val keyPair = Keys.generateRsaKeyPair()
          assertNotNull(keyPair)
        }
      }
    }

    threads.forEach { it.start() }
    threads.forEach { it.join() }
  }

  @Test
  fun `generateAesKey 并发调用 返回有效的密钥`() {
    val threadCount = 5
    val iterationsPerThread = 100

    val threads = List(threadCount) {
      Thread {
        repeat(iterationsPerThread) {
          val key = Keys.generateAesKey()
          assertNotNull(key)
        }
      }
    }

    threads.forEach { it.start() }
    threads.forEach { it.join() }
  }
  // endregion
}
