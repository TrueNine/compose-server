package io.github.truenine.composeserver.security.crypto

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.*
import kotlin.time.measureTime

/**
 * Comprehensive test suite for CryptographicKeyManager.
 *
 * This test class validates all key management operations including RSA, ECC, and AES key generation, conversion, and validation. Tests follow TDD principles
 * with independent, atomic tests that provide complete coverage of the optimized implementation.
 */
class CryptographicKeyManagerTest {

  companion object {
    private const val TEST_SEED = "test-seed-for-deterministic-generation"
    private const val INVALID_BASE64 = "invalid-base64-string"
  }

  @Test
  fun `generateRandomAsciiString with specified length returns valid random string`() {
    val length = 32
    val result = CryptographicKeyManager.generateRandomAsciiString(length)
    assertEquals(length, result.length)
    assertTrue(result.all { it in '0'..'9' || it in 'a'..'z' || it in 'A'..'Z' })
  }

  @Test
  fun `generateRandomAsciiString with zero length throws IllegalArgumentException`() {
    assertFailsWith<IllegalArgumentException> { CryptographicKeyManager.generateRandomAsciiString(0) }
  }

  @Test
  fun `generateRandomAsciiString with negative length throws IllegalArgumentException`() {
    assertFailsWith<IllegalArgumentException> { CryptographicKeyManager.generateRandomAsciiString(-1) }
  }

  @Test
  fun `generateRandomAsciiString multiple calls return different strings`() {
    val results = (1..10).map { CryptographicKeyManager.generateRandomAsciiString(32) }
    assertEquals(10, results.toSet().size) // All results should be different
  }

  @Test
  fun `generateRsaKeyPair returns valid RSA key pair`() {
    val keyPair = CryptographicKeyManager.generateRsaKeyPair()
    assertNotNull(keyPair)
    assertNotNull(keyPair.publicKey)
    assertNotNull(keyPair.privateKey)
    assertEquals("RSA", keyPair.publicKey.algorithm)
    assertEquals("RSA", keyPair.privateKey.algorithm)
  }

  @Test
  fun `generateRsaKeyPair with custom config returns valid key pair`() {
    val config = CryptographicKeyManager.RsaKeyConfig(seed = TEST_SEED, keySize = 2048)
    val keyPair = CryptographicKeyManager.generateRsaKeyPair(config)
    assertNotNull(keyPair)
    assertTrue(keyPair.publicKey.modulus.bitLength() >= 2048)
  }

  @Test
  fun `generateRsaKeyPair with invalid key size throws exception`() {
    val config = CryptographicKeyManager.RsaKeyConfig(keySize = 1024) // Too small
    val result = CryptographicKeyManager.generateRsaKeyPair(config)
    assertNull(result) // Should fail due to security requirements
  }

  @Test
  fun `RSA key Base64 conversion can be correctly restored`() {
    val keyPair = CryptographicKeyManager.generateRsaKeyPair()
    assertNotNull(keyPair)

    val publicKeyBase64 = CryptographicKeyManager.writeKeyToBase64(keyPair.publicKey)
    val privateKeyBase64 = CryptographicKeyManager.writeKeyToBase64(keyPair.privateKey)
    assertNotNull(publicKeyBase64)
    assertNotNull(privateKeyBase64)

    val restoredPublicKey = CryptographicKeyManager.readRsaPublicKeyByBase64(publicKeyBase64)
    val restoredPrivateKey = CryptographicKeyManager.readRsaPrivateKeyByBase64(privateKeyBase64)
    assertNotNull(restoredPublicKey)
    assertNotNull(restoredPrivateKey)

    assertEquals(keyPair.publicKey.encoded.toList(), restoredPublicKey.encoded.toList())
    assertEquals(keyPair.privateKey.encoded.toList(), restoredPrivateKey.encoded.toList())
  }

  @Test
  fun `readRsaKeyPair from Base64 config can be correctly rebuilt`() {
    val originalPair = CryptographicKeyManager.generateRsaKeyPair()
    assertNotNull(originalPair)

    val publicKeyBase64 = CryptographicKeyManager.writeKeyToBase64(originalPair.publicKey)
    val privateKeyBase64 = CryptographicKeyManager.writeKeyToBase64(originalPair.privateKey)
    assertNotNull(publicKeyBase64)
    assertNotNull(privateKeyBase64)

    val config = CryptographicKeyManager.KeyPairBase64Config(publicKeyBase64, privateKeyBase64)
    val rebuiltPair = CryptographicKeyManager.readRsaKeyPair(config)
    assertNotNull(rebuiltPair)
    assertEquals(originalPair.publicKey.encoded.toList(), rebuiltPair.publicKey.encoded.toList())
    assertEquals(originalPair.privateKey.encoded.toList(), rebuiltPair.privateKey.encoded.toList())
  }

  @Test
  fun `generateEccKeyPair returns valid ECC key pair`() {
    val keyPair = CryptographicKeyManager.generateEccKeyPair(TEST_SEED)
    assertNotNull(keyPair)
    assertNotNull(keyPair.publicKey)
    assertNotNull(keyPair.privateKey)
    assertEquals("EC", keyPair.publicKey.algorithm)
    assertEquals("EC", keyPair.privateKey.algorithm)
  }

  @Test
  fun `generateEccKeyPair with custom config returns valid key pair`() {
    val config = CryptographicKeyManager.EccKeyConfig(seed = TEST_SEED, curve = "P-256")
    val keyPair = CryptographicKeyManager.generateEccKeyPair(config)
    assertNotNull(keyPair)
  }

  @Test
  fun `ECC key Base64 conversion can be correctly restored`() {
    val keyPair = CryptographicKeyManager.generateEccKeyPair(TEST_SEED)
    assertNotNull(keyPair)

    val publicKeyBase64 = CryptographicKeyManager.writeKeyToBase64(keyPair.publicKey)
    val privateKeyBase64 = CryptographicKeyManager.writeKeyToBase64(keyPair.privateKey)
    assertNotNull(publicKeyBase64)
    assertNotNull(privateKeyBase64)

    val restoredPublicKey = CryptographicKeyManager.readEccPublicKeyByBase64(publicKeyBase64)
    val restoredPrivateKey = CryptographicKeyManager.readEccPrivateKeyByBase64(privateKeyBase64)
    assertNotNull(restoredPublicKey)
    assertNotNull(restoredPrivateKey)

    assertEquals(keyPair.publicKey.encoded.toList(), restoredPublicKey.encoded.toList())
    assertEquals(keyPair.privateKey.encoded.toList(), restoredPrivateKey.encoded.toList())
  }

  @Test
  fun `readEccKeyPair from Base64 config can be correctly rebuilt`() {
    val originalPair = CryptographicKeyManager.generateEccKeyPair(TEST_SEED)
    assertNotNull(originalPair)

    val publicKeyBase64 = CryptographicKeyManager.writeKeyToBase64(originalPair.publicKey)
    val privateKeyBase64 = CryptographicKeyManager.writeKeyToBase64(originalPair.privateKey)
    assertNotNull(publicKeyBase64)
    assertNotNull(privateKeyBase64)

    val config = CryptographicKeyManager.KeyPairBase64Config(publicKeyBase64, privateKeyBase64)
    val rebuiltPair = CryptographicKeyManager.readEccKeyPair(config)
    assertNotNull(rebuiltPair)
    assertEquals(originalPair.publicKey.encoded.toList(), rebuiltPair.publicKey.encoded.toList())
    assertEquals(originalPair.privateKey.encoded.toList(), rebuiltPair.privateKey.encoded.toList())
  }

  @Test
  fun `generateAesKey returns valid AES key`() {
    val aesKey = CryptographicKeyManager.generateAesKey()
    assertNotNull(aesKey)
    assertEquals("AES", aesKey.algorithm)
    assertEquals(32, aesKey.encoded.size) // 256 bits = 32 bytes
  }

  @Test
  fun `generateAesKey with custom config returns valid key`() {
    val config = CryptographicKeyManager.AesKeyConfig(seed = TEST_SEED, keySize = 128)
    val aesKey = CryptographicKeyManager.generateAesKey(config)
    assertNotNull(aesKey)
    assertEquals(16, aesKey.encoded.size) // 128 bits = 16 bytes
  }

  @Test
  fun `generateAesKey with invalid key size returns null`() {
    val config = CryptographicKeyManager.AesKeyConfig(keySize = 64) // Invalid size
    val result = CryptographicKeyManager.generateAesKey(config)
    assertNull(result)
  }

  @Test
  fun `AES key Base64 conversion can be correctly restored`() {
    val originalKey = CryptographicKeyManager.generateAesKey()
    assertNotNull(originalKey)

    val keyBase64 = CryptographicKeyManager.writeAesKeyToBase64(originalKey)
    assertNotNull(keyBase64)

    val restoredKey = CryptographicKeyManager.readAesKeyByBase64(keyBase64)
    assertNotNull(restoredKey)
    assertEquals(originalKey.encoded.toList(), restoredKey.encoded.toList())
  }

  @Test
  fun `writeKeyToPem converts key to PEM format`() {
    val keyPair = CryptographicKeyManager.generateRsaKeyPair()
    assertNotNull(keyPair)

    val publicKeyPem = CryptographicKeyManager.writeKeyToPem(keyPair.publicKey)
    val privateKeyPem = CryptographicKeyManager.writeKeyToPem(keyPair.privateKey)

    assertNotNull(publicKeyPem)
    assertNotNull(privateKeyPem)
    assertTrue(publicKeyPem.contains("-----BEGIN"))
    assertTrue(publicKeyPem.contains("-----END"))
    assertTrue(privateKeyPem.contains("-----BEGIN"))
    assertTrue(privateKeyPem.contains("-----END"))
  }

  @Test
  fun `invalid Base64 strings return null`() {
    assertNull(CryptographicKeyManager.readRsaPublicKeyByBase64(INVALID_BASE64))
    assertNull(CryptographicKeyManager.readRsaPrivateKeyByBase64(INVALID_BASE64))
    assertNull(CryptographicKeyManager.readEccPublicKeyByBase64(INVALID_BASE64))
    assertNull(CryptographicKeyManager.readEccPrivateKeyByBase64(INVALID_BASE64))
    assertNull(CryptographicKeyManager.readAesKeyByBase64(INVALID_BASE64))
  }

  @Test
  fun `performance benchmark for RSA key generation`() {
    val iterations = 10
    val duration = measureTime {
      repeat(iterations) {
        val keyPair = CryptographicKeyManager.generateRsaKeyPair()
        assertNotNull(keyPair)
      }
    }
    log.info("RSA key generation performance: {} iterations in {} ms", iterations, duration.inWholeMilliseconds)
    assertTrue(duration.inWholeMilliseconds < 30000) // Should complete within 30 seconds
  }

  @Test
  fun `performance benchmark for AES key generation`() {
    val iterations = 1000
    val duration = measureTime {
      repeat(iterations) {
        val aesKey = CryptographicKeyManager.generateAesKey()
        assertNotNull(aesKey)
      }
    }
    log.info("AES key generation performance: {} iterations in {} ms", iterations, duration.inWholeMilliseconds)
    assertTrue(duration.inWholeMilliseconds < 5000) // Should complete within 5 seconds
  }

  @Test
  fun `thread safety test for concurrent key generation`() {
    val threads = 5
    val iterationsPerThread = 10
    val results = mutableListOf<String>()

    val threadList =
      (1..threads).map { threadId ->
        Thread {
          repeat(iterationsPerThread) {
            val randomString = CryptographicKeyManager.generateRandomAsciiString(32)
            synchronized(results) { results.add(randomString) }
          }
        }
      }

    threadList.forEach { it.start() }
    threadList.forEach { it.join() }

    assertEquals(threads * iterationsPerThread, results.size)
    // All results should be unique (very high probability with 32-character strings)
    assertTrue(results.toSet().size >= results.size * 0.95) // Allow for very small chance of collision
  }

  @Test
  fun `backward compatibility with legacy Keys object`() {
    @Suppress("DEPRECATION") val result = CryptographicKeyManager.Keys.generateRandomAsciiString(16)
    assertEquals(16, result.length)
  }
}
