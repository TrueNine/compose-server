package io.github.truenine.composeserver.security.crypto

import io.github.truenine.composeserver.enums.EncryptAlgorithm
import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.*
import kotlin.time.measureTime

/**
 * Comprehensive test suite for CryptographicOperations.
 *
 * This test class validates all cryptographic operations including RSA, ECC, and AES encryption/decryption, digital signatures, and secure hashing. Tests
 * follow TDD principles with independent, atomic tests that provide complete coverage of the optimized implementation.
 */
class CryptographicOperationsTest {

  companion object {
    // Test data constants
    private const val TEST_TEXT = "Hello, World!"
    private const val TEST_CHINESE_TEXT = "你好世界 🌟"
    private const val EMPTY_STRING = ""
    private val LARGE_TEXT = "A".repeat(1000) // Larger than RSA shard size
    private const val SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;':\",./<>?"

    // Expected hash values for test data
    private const val EXPECTED_SHA1_HELLO = "0a0a9f2a6772942557ab5355d76af442f8f65e01"
    private const val EXPECTED_SHA256_HELLO = "dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f"

    // Test keys (generated once for all tests)
    private val rsaKeyPair = CryptographicKeyManager.generateRsaKeyPair()!!
    private val rsaPublicKey = rsaKeyPair.publicKey
    private val rsaPrivateKey = rsaKeyPair.privateKey

    private val eccKeyPair = CryptographicKeyManager.generateEccKeyPair("test-seed")!!
    private val eccPublicKey = eccKeyPair.publicKey
    private val eccPrivateKey = eccKeyPair.privateKey

    private val aesKey = CryptographicKeyManager.generateAesKey()!!
    private val aesKeyBase64 = CryptographicKeyManager.writeKeyToBase64(aesKey)!!

    private val rsaPublicKeyBase64 = CryptographicKeyManager.writeKeyToBase64(rsaPublicKey)!!

    // Invalid test data
    private const val INVALID_BASE64 = "invalid-base64-string!"
    private const val MALFORMED_CIPHERTEXT = "malformed.ciphertext.data"
  }

  // ================================================================================================
  // Data Class Tests
  // ================================================================================================

  @Test
  fun `EncryptionConfig with default values creates correct configuration`() {
    val config = CryptographicOperations.EncryptionConfig(TEST_TEXT)

    assertEquals(TEST_TEXT, config.data)
    assertEquals(245, config.shardingSize) // SHARDING_SIZE constant
    assertEquals(Charsets.UTF_8, config.charset)
    assertEquals(EncryptAlgorithm.RSA, config.algorithm)
  }

  @Test
  fun `EncryptionConfig with custom values creates correct configuration`() {
    val customCharset = Charsets.UTF_16
    val customShardSize = 100
    val customAlgorithm = EncryptAlgorithm.AES

    val config =
      CryptographicOperations.EncryptionConfig(data = TEST_CHINESE_TEXT, shardingSize = customShardSize, charset = customCharset, algorithm = customAlgorithm)

    assertEquals(TEST_CHINESE_TEXT, config.data)
    assertEquals(customShardSize, config.shardingSize)
    assertEquals(customCharset, config.charset)
    assertEquals(customAlgorithm, config.algorithm)
  }

  @Test
  fun `DecryptionConfig with default values creates correct configuration`() {
    val ciphertext = "test-ciphertext"
    val config = CryptographicOperations.DecryptionConfig(ciphertext)

    assertEquals(ciphertext, config.ciphertext)
    assertEquals(Charsets.UTF_8, config.charset)
    assertEquals(EncryptAlgorithm.RSA, config.algorithm)
  }

  @Test
  fun `DecryptionConfig with custom values creates correct configuration`() {
    val ciphertext = "test-ciphertext"
    val customCharset = Charsets.ISO_8859_1
    val customAlgorithm = EncryptAlgorithm.ECC

    val config = CryptographicOperations.DecryptionConfig(ciphertext = ciphertext, charset = customCharset, algorithm = customAlgorithm)

    assertEquals(ciphertext, config.ciphertext)
    assertEquals(customCharset, config.charset)
    assertEquals(customAlgorithm, config.algorithm)
  }

  // ================================================================================================
  // RSA Encryption/Decryption Tests
  // ================================================================================================

  @Test
  fun `encryptByRsaPublicKey with valid key and data returns encrypted string`() {
    val encrypted = CryptographicOperations.encryptByRsaPublicKey(rsaPublicKey, TEST_TEXT)

    assertNotNull(encrypted)
    assertNotEquals(TEST_TEXT, encrypted)
    assertTrue(encrypted.isNotEmpty())
    // Verify it's Base64 encoded
    assertTrue(encrypted.matches(Regex("^[A-Za-z0-9+/]*={0,2}$")))
  }

  @Test
  fun `encryptByRsaPublicKey and decryptByRsaPrivateKey round trip preserves data`() {
    val encrypted = CryptographicOperations.encryptByRsaPublicKey(rsaPublicKey, TEST_TEXT)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByRsaPrivateKey(rsaPrivateKey, encrypted)
    assertEquals(TEST_TEXT, decrypted)
  }

  @Test
  fun `encryptByRsaPublicKey with empty string returns empty string`() {
    val encrypted = CryptographicOperations.encryptByRsaPublicKey(rsaPublicKey, EMPTY_STRING)
    assertEquals(EMPTY_STRING, encrypted)
  }

  @Test
  fun `encryptByRsaPublicKey with large data uses sharding correctly`() {
    val encrypted = CryptographicOperations.encryptByRsaPublicKey(rsaPublicKey, LARGE_TEXT)
    assertNotNull(encrypted)

    // Should contain sharding separator for large data
    assertTrue(encrypted.contains("."))

    // Verify round trip
    val decrypted = CryptographicOperations.decryptByRsaPrivateKey(rsaPrivateKey, encrypted)
    assertEquals(LARGE_TEXT, decrypted)
  }

  @Test
  fun `encryptByRsaPublicKey with Chinese text preserves encoding`() {
    val encrypted = CryptographicOperations.encryptByRsaPublicKey(rsaPublicKey, TEST_CHINESE_TEXT)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByRsaPrivateKey(rsaPrivateKey, encrypted)
    assertEquals(TEST_CHINESE_TEXT, decrypted)
  }

  @Test
  fun `encryptByRsaPublicKey with custom charset works correctly`() {
    // Use ISO_8859_1 which is more compatible with Base64 encoding
    val customCharset = Charsets.ISO_8859_1
    val encrypted = CryptographicOperations.encryptByRsaPublicKey(rsaPublicKey, TEST_TEXT, charset = customCharset)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByRsaPrivateKey(rsaPrivateKey, encrypted!!, customCharset)
    assertEquals(TEST_TEXT, decrypted)
  }

  @Test
  fun `encryptByRsaPublicKey with custom sharding size works correctly`() {
    val customShardSize = 100
    val encrypted = CryptographicOperations.encryptByRsaPublicKey(rsaPublicKey, LARGE_TEXT, customShardSize)
    assertNotNull(encrypted)

    // Should have more shards with smaller shard size
    val shardCount = encrypted.split(".").size
    assertTrue(shardCount > 1)

    val decrypted = CryptographicOperations.decryptByRsaPrivateKey(rsaPrivateKey, encrypted)
    assertEquals(LARGE_TEXT, decrypted)
  }

  @Test
  fun `encryptByRsaPrivateKey and decryptByRsaPublicKey round trip preserves data`() {
    val encrypted = CryptographicOperations.encryptByRsaPrivateKey(rsaPrivateKey, TEST_TEXT)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByRsaPublicKey(rsaPublicKey, encrypted)
    assertEquals(TEST_TEXT, decrypted)
  }

  @Test
  fun `encryptByRsaPublicKeyBase64 with valid key string works correctly`() {
    val encrypted = CryptographicOperations.encryptByRsaPublicKeyBase64(rsaPublicKeyBase64, TEST_TEXT)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByRsaPrivateKey(rsaPrivateKey, encrypted)
    assertEquals(TEST_TEXT, decrypted)
  }

  @Test
  fun `encryptByRsaPublicKeyBase64 with invalid key string returns null`() {
    val encrypted = CryptographicOperations.encryptByRsaPublicKeyBase64(INVALID_BASE64, TEST_TEXT)
    assertNull(encrypted)
  }

  @Test
  fun `decryptByRsaPublicKeyBase64 with valid key string works correctly`() {
    val encrypted = CryptographicOperations.encryptByRsaPrivateKey(rsaPrivateKey, TEST_TEXT)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByRsaPublicKeyBase64(rsaPublicKeyBase64, encrypted)
    assertEquals(TEST_TEXT, decrypted)
  }

  @Test
  fun `decryptByRsaPublicKeyBase64 with invalid key string returns null`() {
    val decrypted = CryptographicOperations.decryptByRsaPublicKeyBase64(INVALID_BASE64, "test")
    assertNull(decrypted)
  }

  @Test
  fun `decryptByRsaPrivateKey with empty ciphertext returns empty string`() {
    val decrypted = CryptographicOperations.decryptByRsaPrivateKey(rsaPrivateKey, EMPTY_STRING)
    assertEquals(EMPTY_STRING, decrypted)
  }

  @Test
  fun `decryptByRsaPublicKey with empty ciphertext returns empty string`() {
    val decrypted = CryptographicOperations.decryptByRsaPublicKey(rsaPublicKey, EMPTY_STRING)
    assertEquals(EMPTY_STRING, decrypted)
  }

  @Test
  fun `decryptByRsaPrivateKey with malformed ciphertext returns null`() {
    val decrypted = CryptographicOperations.decryptByRsaPrivateKey(rsaPrivateKey, MALFORMED_CIPHERTEXT)
    assertNull(decrypted)
  }

  @Test
  fun `decryptByRsaPublicKey with malformed ciphertext returns null`() {
    val decrypted = CryptographicOperations.decryptByRsaPublicKey(rsaPublicKey, MALFORMED_CIPHERTEXT)
    assertNull(decrypted)
  }

  // ================================================================================================
  // ECC Encryption/Decryption Tests
  // ================================================================================================

  @Test
  fun `encryptByEccPublicKey with valid key and data returns encrypted string`() {
    val encrypted = CryptographicOperations.encryptByEccPublicKey(eccPublicKey, TEST_TEXT)

    assertNotNull(encrypted)
    assertNotEquals(TEST_TEXT, encrypted)
    assertTrue(encrypted.isNotEmpty())
    // Verify it's Base64 encoded
    assertTrue(encrypted.matches(Regex("^[A-Za-z0-9+/]*={0,2}$")))
  }

  @Test
  fun `encryptByEccPublicKey and decryptByEccPrivateKey round trip preserves data`() {
    val encrypted = CryptographicOperations.encryptByEccPublicKey(eccPublicKey, TEST_TEXT)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByEccPrivateKey(eccPrivateKey, encrypted)
    assertEquals(TEST_TEXT, decrypted)
  }

  @Test
  fun `encryptByEccPublicKey with Chinese text preserves encoding`() {
    val encrypted = CryptographicOperations.encryptByEccPublicKey(eccPublicKey, TEST_CHINESE_TEXT)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByEccPrivateKey(eccPrivateKey, encrypted)
    assertEquals(TEST_CHINESE_TEXT, decrypted)
  }

  @Test
  fun `encryptByEccPublicKey with custom charset works correctly`() {
    val customCharset = Charsets.UTF_16
    val encrypted = CryptographicOperations.encryptByEccPublicKey(eccPublicKey, TEST_TEXT, customCharset)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByEccPrivateKey(eccPrivateKey, encrypted, customCharset)
    assertEquals(TEST_TEXT, decrypted)
  }

  @Test
  fun `encryptByEccPublicKey with empty string works correctly`() {
    val encrypted = CryptographicOperations.encryptByEccPublicKey(eccPublicKey, EMPTY_STRING)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByEccPrivateKey(eccPrivateKey, encrypted)
    assertEquals(EMPTY_STRING, decrypted)
  }

  @Test
  fun `encryptByEccPublicKey with special characters preserves data`() {
    val encrypted = CryptographicOperations.encryptByEccPublicKey(eccPublicKey, SPECIAL_CHARS)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByEccPrivateKey(eccPrivateKey, encrypted)
    assertEquals(SPECIAL_CHARS, decrypted)
  }

  @Test
  fun `decryptByEccPrivateKey with malformed ciphertext returns null`() {
    val decrypted = CryptographicOperations.decryptByEccPrivateKey(eccPrivateKey, MALFORMED_CIPHERTEXT)
    assertNull(decrypted)
  }

  // ================================================================================================
  // AES Encryption/Decryption Tests
  // ================================================================================================

  @Test
  fun `encryptByAesKey with valid key and data returns encrypted string`() {
    val encrypted = CryptographicOperations.encryptByAesKey(aesKey, TEST_TEXT)

    assertNotNull(encrypted)
    assertNotEquals(TEST_TEXT, encrypted)
    assertTrue(encrypted.isNotEmpty())
    // Verify it's Base64 encoded
    assertTrue(encrypted.matches(Regex("^[A-Za-z0-9+/]*={0,2}$")))
  }

  @Test
  fun `encryptByAesKey and decryptByAesKey round trip preserves data`() {
    val encrypted = CryptographicOperations.encryptByAesKey(aesKey, TEST_TEXT)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByAesKey(aesKey, encrypted!!)
    assertEquals(TEST_TEXT, decrypted)
  }

  @Test
  fun `encryptByAesKey with Chinese text preserves encoding`() {
    val encrypted = CryptographicOperations.encryptByAesKey(aesKey, TEST_CHINESE_TEXT)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByAesKey(aesKey, encrypted!!)
    assertEquals(TEST_CHINESE_TEXT, decrypted)
  }

  @Test
  fun `encryptByAesKey with large text works correctly`() {
    val encrypted = CryptographicOperations.encryptByAesKey(aesKey, LARGE_TEXT)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByAesKey(aesKey, encrypted!!)
    assertEquals(LARGE_TEXT, decrypted)
  }

  @Test
  fun `encryptByAesKey with custom charset works correctly`() {
    val customCharset = Charsets.UTF_16
    val encrypted = CryptographicOperations.encryptByAesKey(aesKey, TEST_TEXT, customCharset)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByAesKey(aesKey, encrypted!!, customCharset)
    assertEquals(TEST_TEXT, decrypted)
  }

  @Test
  fun `encryptByAesKey with empty string works correctly`() {
    val encrypted = CryptographicOperations.encryptByAesKey(aesKey, EMPTY_STRING)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByAesKey(aesKey, encrypted!!)
    assertEquals(EMPTY_STRING, decrypted)
  }

  @Test
  fun `encryptByAesKeyBase64 with valid key string works correctly`() {
    val encrypted = CryptographicOperations.encryptByAesKeyBase64(aesKeyBase64, TEST_TEXT)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByAesKeyBase64(aesKeyBase64, encrypted!!)
    assertEquals(TEST_TEXT, decrypted)
  }

  @Test
  fun `encryptByAesKeyBase64 with invalid key string returns null`() {
    val encrypted = CryptographicOperations.encryptByAesKeyBase64(INVALID_BASE64, TEST_TEXT)
    assertNull(encrypted)
  }

  @Test
  fun `decryptByAesKeyBase64 with valid key string works correctly`() {
    val encrypted = CryptographicOperations.encryptByAesKey(aesKey, TEST_TEXT)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByAesKeyBase64(aesKeyBase64, encrypted!!)
    assertEquals(TEST_TEXT, decrypted)
  }

  @Test
  fun `decryptByAesKeyBase64 with invalid key string returns null`() {
    val decrypted = CryptographicOperations.decryptByAesKeyBase64(INVALID_BASE64, "test")
    assertNull(decrypted)
  }

  @Test
  fun `decryptByAesKey with malformed ciphertext returns null`() {
    val decrypted = CryptographicOperations.decryptByAesKey(aesKey, MALFORMED_CIPHERTEXT)
    assertNull(decrypted)
  }

  // ================================================================================================
  // Hash Function Tests
  // ================================================================================================

  @Test
  fun `signatureBySha1 with test text returns expected hash`() {
    val hash = CryptographicOperations.signatureBySha1(TEST_TEXT)
    assertEquals(EXPECTED_SHA1_HELLO, hash)
    assertEquals(40, hash.length) // SHA-1 produces 40 character hex string
    assertTrue(hash.matches(Regex("^[a-f0-9]+$")))
  }

  @Test
  fun `signatureBySha256 with test text returns expected hash`() {
    val hash = CryptographicOperations.signatureBySha256(TEST_TEXT)
    assertEquals(EXPECTED_SHA256_HELLO, hash)
    assertEquals(64, hash.length) // SHA-256 produces 64 character hex string
    assertTrue(hash.matches(Regex("^[a-f0-9]+$")))
  }

  @Test
  fun `signatureBySha1 with empty string returns consistent hash`() {
    val hash = CryptographicOperations.signatureBySha1(EMPTY_STRING)
    assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", hash)
  }

  @Test
  fun `signatureBySha256 with empty string returns consistent hash`() {
    val hash = CryptographicOperations.signatureBySha256(EMPTY_STRING)
    assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", hash)
  }

  @Test
  fun `signatureBySha1 with Chinese text works correctly`() {
    val hash = CryptographicOperations.signatureBySha1(TEST_CHINESE_TEXT)
    assertNotNull(hash)
    assertEquals(40, hash.length)
    assertTrue(hash.matches(Regex("^[a-f0-9]+$")))
  }

  @Test
  fun `signatureBySha256 with Chinese text works correctly`() {
    val hash = CryptographicOperations.signatureBySha256(TEST_CHINESE_TEXT)
    assertNotNull(hash)
    assertEquals(64, hash.length)
    assertTrue(hash.matches(Regex("^[a-f0-9]+$")))
  }

  @Test
  fun `signatureBySha1 with custom charset works correctly`() {
    val customCharset = Charsets.UTF_16
    val hash = CryptographicOperations.signatureBySha1(TEST_TEXT, customCharset)
    assertNotNull(hash)
    assertEquals(40, hash.length)
    // Should be different from UTF-8 hash due to different byte representation
    assertNotEquals(EXPECTED_SHA1_HELLO, hash)
  }

  @Test
  fun `signatureBySha256 with custom charset works correctly`() {
    val customCharset = Charsets.UTF_16
    val hash = CryptographicOperations.signatureBySha256(TEST_TEXT, customCharset)
    assertNotNull(hash)
    assertEquals(64, hash.length)
    // Should be different from UTF-8 hash due to different byte representation
    assertNotEquals(EXPECTED_SHA256_HELLO, hash)
  }

  @Test
  fun `signatureBySha1ByteArray with test bytes returns expected hash`() {
    val testBytes = TEST_TEXT.toByteArray(Charsets.UTF_8)
    val hashBytes = CryptographicOperations.signatureBySha1ByteArray(testBytes)

    assertEquals(20, hashBytes.size) // SHA-1 produces 20 bytes
    val hexString = hashBytes.joinToString("") { "%02x".format(it) }
    assertEquals(EXPECTED_SHA1_HELLO, hexString)
  }

  @Test
  fun `signatureBySha256ByteArray with test bytes returns expected hash`() {
    val testBytes = TEST_TEXT.toByteArray(Charsets.UTF_8)
    val hashBytes = CryptographicOperations.signatureBySha256ByteArray(testBytes)

    assertEquals(32, hashBytes.size) // SHA-256 produces 32 bytes
    val hexString = hashBytes.joinToString("") { "%02x".format(it) }
    assertEquals(EXPECTED_SHA256_HELLO, hexString)
  }

  @Test
  fun `signatureBySha1ByteArray with empty array returns consistent hash`() {
    val emptyBytes = byteArrayOf()
    val hashBytes = CryptographicOperations.signatureBySha1ByteArray(emptyBytes)

    assertEquals(20, hashBytes.size)
    val hexString = hashBytes.joinToString("") { "%02x".format(it) }
    assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", hexString)
  }

  @Test
  fun `signatureBySha256ByteArray with empty array returns consistent hash`() {
    val emptyBytes = byteArrayOf()
    val hashBytes = CryptographicOperations.signatureBySha256ByteArray(emptyBytes)

    assertEquals(32, hashBytes.size)
    val hexString = hashBytes.joinToString("") { "%02x".format(it) }
    assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", hexString)
  }

  @Test
  fun `hash functions are deterministic`() {
    // Multiple calls should return same result
    val hash1 = CryptographicOperations.signatureBySha256(TEST_TEXT)
    val hash2 = CryptographicOperations.signatureBySha256(TEST_TEXT)
    val hash3 = CryptographicOperations.signatureBySha256(TEST_TEXT)

    assertEquals(hash1, hash2)
    assertEquals(hash2, hash3)
  }

  // ================================================================================================
  // Digital Signature Tests
  // ================================================================================================

  @Test
  fun `signWithSha256WithRsaByRsaPrivateKey creates valid signature object`() {
    val signature = CryptographicOperations.signWithSha256WithRsaByRsaPrivateKey(TEST_TEXT, rsaPrivateKey)

    assertNotNull(signature)
    assertEquals("SHA256withRSA", signature.algorithm)

    // Generate signature bytes
    val signatureBytes = signature.sign()
    assertNotNull(signatureBytes)
    assertTrue(signatureBytes.isNotEmpty())
  }

  @Test
  fun `signWithSha256WithRsaByRsaPrivateKey with custom charset works correctly`() {
    val customCharset = Charsets.UTF_16
    val signature = CryptographicOperations.signWithSha256WithRsaByRsaPrivateKey(TEST_TEXT, rsaPrivateKey, customCharset)

    assertNotNull(signature)
    assertEquals("SHA256withRSA", signature.algorithm)

    val signatureBytes = signature.sign()
    assertNotNull(signatureBytes)
    assertTrue(signatureBytes.isNotEmpty())
  }

  @Test
  fun `signWithSha256WithRsaByRsaPrivateKey with empty string works correctly`() {
    val signature = CryptographicOperations.signWithSha256WithRsaByRsaPrivateKey(EMPTY_STRING, rsaPrivateKey)

    assertNotNull(signature)
    val signatureBytes = signature.sign()
    assertNotNull(signatureBytes)
    assertTrue(signatureBytes.isNotEmpty())
  }

  // ================================================================================================
  // Utility Method Tests
  // ================================================================================================

  @Test
  fun `optimizedSharding with data smaller than shard size returns single shard`() {
    val data = "small".toByteArray()
    val shardSize = 100

    val shards = CryptographicOperations.optimizedSharding(data, shardSize)

    assertEquals(1, shards.size)
    assertContentEquals(data, shards[0])
  }

  @Test
  fun `optimizedSharding with data larger than shard size returns multiple shards`() {
    val data = "A".repeat(300).toByteArray()
    val shardSize = 100

    val shards = CryptographicOperations.optimizedSharding(data, shardSize)

    assertEquals(3, shards.size)
    assertEquals(100, shards[0].size)
    assertEquals(100, shards[1].size)
    assertEquals(100, shards[2].size)

    // Verify data integrity
    val reconstructed = shards.reduce { acc, bytes -> acc + bytes }
    assertContentEquals(data, reconstructed)
  }

  @Test
  fun `optimizedSharding with data not evenly divisible by shard size works correctly`() {
    val data = "A".repeat(250).toByteArray()
    val shardSize = 100

    val shards = CryptographicOperations.optimizedSharding(data, shardSize)

    assertEquals(3, shards.size)
    assertEquals(100, shards[0].size)
    assertEquals(100, shards[1].size)
    assertEquals(50, shards[2].size) // Last shard is smaller

    // Verify data integrity
    val reconstructed = shards.reduce { acc, bytes -> acc + bytes }
    assertContentEquals(data, reconstructed)
  }

  @Test
  fun `optimizedSharding with empty data returns single empty shard`() {
    val data = byteArrayOf()
    val shardSize = 100

    val shards = CryptographicOperations.optimizedSharding(data, shardSize)

    assertEquals(1, shards.size)
    assertEquals(0, shards[0].size)
  }

  @Test
  fun `optimizedSharding with zero shard size throws IllegalArgumentException`() {
    val data = "test".toByteArray()

    assertFailsWith<IllegalArgumentException> { CryptographicOperations.optimizedSharding(data, 0) }
  }

  @Test
  fun `optimizedSharding with negative shard size throws IllegalArgumentException`() {
    val data = "test".toByteArray()

    assertFailsWith<IllegalArgumentException> { CryptographicOperations.optimizedSharding(data, -1) }
  }

  @Test
  fun `optimizedSharding with shard size of 1 works correctly`() {
    val data = "ABC".toByteArray()
    val shardSize = 1

    val shards = CryptographicOperations.optimizedSharding(data, shardSize)

    assertEquals(3, shards.size)
    assertEquals(1, shards[0].size)
    assertEquals(1, shards[1].size)
    assertEquals(1, shards[2].size)

    assertEquals('A'.code.toByte(), shards[0][0])
    assertEquals('B'.code.toByte(), shards[1][0])
    assertEquals('C'.code.toByte(), shards[2][0])
  }

  @Test
  fun `deprecated sharding method works same as optimizedSharding`() {
    val data = "A".repeat(250).toByteArray()
    val shardSize = 100

    @Suppress("DEPRECATION") val oldShards = CryptographicOperations.sharding(data, shardSize)
    val newShards = CryptographicOperations.optimizedSharding(data, shardSize)

    assertEquals(oldShards.size, newShards.size)
    for (i in oldShards.indices) {
      assertContentEquals(oldShards[i], newShards[i])
    }
  }

  // ================================================================================================
  // Performance Tests
  // ================================================================================================

  @Test
  fun `hash functions perform efficiently with large data`() {
    val largeData = "A".repeat(10000)

    val sha1Time = measureTime { repeat(100) { CryptographicOperations.signatureBySha1(largeData) } }

    val sha256Time = measureTime { repeat(100) { CryptographicOperations.signatureBySha256(largeData) } }

    log.info("SHA-1 time for 100 iterations: $sha1Time")
    log.info("SHA-256 time for 100 iterations: $sha256Time")

    // Performance should be reasonable (less than 1 second for 100 iterations)
    assertTrue(sha1Time.inWholeMilliseconds < 1000)
    assertTrue(sha256Time.inWholeMilliseconds < 1000)
  }

  @Test
  fun `RSA encryption performs efficiently with sharded data`() {
    val largeData = "A".repeat(2000) // Larger than default shard size

    val encryptTime = measureTime { CryptographicOperations.encryptByRsaPublicKey(rsaPublicKey, largeData) }

    log.info("RSA encryption time for large data: $encryptTime")

    // Should complete in reasonable time (less than 5 seconds)
    assertTrue(encryptTime.inWholeMilliseconds < 5000)
  }

  @Test
  fun `AES encryption performs efficiently with large data`() {
    val largeData = "A".repeat(100000)

    val encryptTime = measureTime { CryptographicOperations.encryptByAesKey(aesKey, largeData) }

    log.info("AES encryption time for large data: $encryptTime")

    // AES should be very fast (less than 1 second)
    assertTrue(encryptTime.inWholeMilliseconds < 1000)
  }

  // ================================================================================================
  // Thread Safety Tests
  // ================================================================================================

  @Test
  fun `hash functions are thread safe`() {
    val testData = "concurrent-test-data"
    val results = mutableListOf<String>()
    val threads = mutableListOf<Thread>()

    // Create multiple threads that compute hashes concurrently
    repeat(10) { threadIndex ->
      val thread = Thread {
        repeat(100) {
          val hash = CryptographicOperations.signatureBySha256("$testData-$threadIndex-$it")
          synchronized(results) { results.add(hash) }
        }
      }
      threads.add(thread)
      thread.start()
    }

    // Wait for all threads to complete
    threads.forEach { it.join() }

    // Verify all hashes were computed
    assertEquals(1000, results.size)

    // Verify deterministic behavior - same input should produce same hash
    val firstHash = CryptographicOperations.signatureBySha256("$testData-0-0")
    val duplicateHash = CryptographicOperations.signatureBySha256("$testData-0-0")
    assertEquals(firstHash, duplicateHash)
  }

  // ================================================================================================
  // Exception Handling Tests
  // ================================================================================================

  @Test
  fun `encryption methods handle null gracefully`() {
    // These should not throw exceptions but return null
    assertNull(CryptographicOperations.encryptByRsaPublicKeyBase64("", TEST_TEXT))
    assertNull(CryptographicOperations.decryptByRsaPublicKeyBase64("", "test"))
    assertNull(CryptographicOperations.encryptByAesKeyBase64("", TEST_TEXT))
    assertNull(CryptographicOperations.decryptByAesKeyBase64("", "test"))
  }

  @Test
  fun `decryption with wrong key type returns null`() {
    // Encrypt with one key, try to decrypt with different key
    val encrypted1 = CryptographicOperations.encryptByRsaPublicKey(rsaPublicKey, TEST_TEXT)
    assertNotNull(encrypted1)

    // Generate different key pair
    val differentKeyPair = CryptographicKeyManager.generateRsaKeyPair()!!
    val differentPrivateKey = differentKeyPair.privateKey

    // Should return null when trying to decrypt with wrong key
    val decrypted = CryptographicOperations.decryptByRsaPrivateKey(differentPrivateKey, encrypted1!!)
    assertNull(decrypted)
  }

  @Test
  fun `AES decryption with wrong key returns null`() {
    val encrypted = CryptographicOperations.encryptByAesKey(aesKey, TEST_TEXT)
    assertNotNull(encrypted)

    // Generate different AES key
    val differentAesKey = CryptographicKeyManager.generateAesKey()!!

    // Should return null when trying to decrypt with wrong key
    val decrypted = CryptographicOperations.decryptByAesKey(differentAesKey, encrypted!!)
    assertNull(decrypted)
  }

  // ================================================================================================
  // Boundary Condition Tests
  // ================================================================================================

  @Test
  fun `encryption handles maximum RSA data size correctly`() {
    // Test with data at RSA encryption limit (245 bytes for 2048-bit key)
    val maxData = "A".repeat(245)

    val encrypted = CryptographicOperations.encryptByRsaPublicKey(rsaPublicKey, maxData)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByRsaPrivateKey(rsaPrivateKey, encrypted)
    assertEquals(maxData, decrypted)
  }

  @Test
  fun `encryption handles data just over RSA limit correctly`() {
    // Test with data just over RSA encryption limit
    val overLimitData = "A".repeat(246)

    val encrypted = CryptographicOperations.encryptByRsaPublicKey(rsaPublicKey, overLimitData)
    assertNotNull(encrypted)

    // Should contain sharding separator
    assertTrue(encrypted.contains("."))

    val decrypted = CryptographicOperations.decryptByRsaPrivateKey(rsaPrivateKey, encrypted)
    assertEquals(overLimitData, decrypted)
  }

  @Test
  fun `hash functions handle very large input correctly`() {
    val veryLargeData = "A".repeat(1000000) // 1MB of data

    val sha1Hash = CryptographicOperations.signatureBySha1(veryLargeData)
    val sha256Hash = CryptographicOperations.signatureBySha256(veryLargeData)

    assertEquals(40, sha1Hash.length)
    assertEquals(64, sha256Hash.length)
    assertTrue(sha1Hash.matches(Regex("^[a-f0-9]+$")))
    assertTrue(sha256Hash.matches(Regex("^[a-f0-9]+$")))
  }

  @Test
  fun `encryption preserves binary data integrity`() {
    // Test with binary data (not just text)
    val binaryData = byteArrayOf(0, 1, 2, 3, 255.toByte(), 254.toByte(), 127, 128.toByte())
    val binaryString = String(binaryData, Charsets.ISO_8859_1) // Preserve all byte values

    val encrypted = CryptographicOperations.encryptByAesKey(aesKey, binaryString, Charsets.ISO_8859_1)
    assertNotNull(encrypted)

    val decrypted = CryptographicOperations.decryptByAesKey(aesKey, encrypted!!, Charsets.ISO_8859_1)
    assertEquals(binaryString, decrypted)

    // Verify byte-level integrity
    val decryptedBytes = decrypted!!.toByteArray(Charsets.ISO_8859_1)
    assertContentEquals(binaryData, decryptedBytes)
  }

  @Test
  fun `sharding handles edge case of data size equal to shard size`() {
    val data = "A".repeat(245).toByteArray() // Exactly the default shard size
    val shardSize = 245

    val shards = CryptographicOperations.optimizedSharding(data, shardSize)

    assertEquals(1, shards.size)
    assertEquals(245, shards[0].size)
    assertContentEquals(data, shards[0])
  }

  @Test
  fun `deprecated methods maintain backward compatibility`() {
    // Test that deprecated SHA-1 methods still work
    @Suppress("DEPRECATION") val sha1Hash = CryptographicOperations.signatureBySha1(TEST_TEXT)
    assertEquals(EXPECTED_SHA1_HELLO, sha1Hash)

    @Suppress("DEPRECATION") val sha1Bytes = CryptographicOperations.signatureBySha1ByteArray(TEST_TEXT.toByteArray())
    assertEquals(20, sha1Bytes.size)

    @Suppress("DEPRECATION") val shards = CryptographicOperations.sharding("test".toByteArray(), 2)
    assertEquals(2, shards.size)
  }
}
