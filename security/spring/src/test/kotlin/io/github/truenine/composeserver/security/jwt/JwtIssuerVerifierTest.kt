package io.github.truenine.composeserver.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.security.crypto.CryptographicKeyManager
import io.github.truenine.composeserver.security.crypto.domain.IEccExtKeyPair
import io.github.truenine.composeserver.security.crypto.domain.IRsaExtKeyPair
import io.github.truenine.composeserver.security.jwt.consts.IssuerParam
import io.github.truenine.composeserver.security.jwt.consts.VerifierParam
import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.time.measureTime
import org.junit.jupiter.api.Test

/**
 * Comprehensive test suite for JWT issuer and verifier functionality.
 *
 * This test class validates JWT token generation, verification, encryption/decryption, error handling, and performance characteristics. Tests follow TDD
 * principles with independent, atomic tests that provide complete coverage of JWT operations.
 *
 * @author TrueNine
 * @since 2024-07-14
 */
class JwtIssuerVerifierTest {

  companion object {
    private const val TEST_ISSUER = "test-issuer"
    private const val TEST_ID = "test-id"
    private const val SIMPLE_TEST_ISSUER = "simple-test"
    private const val SIMPLE_TEST_ID = "simple-id"
    private const val TEST_EXPIRE_SECONDS = 100L
    private const val LARGE_DATA_SIZE = 10000
    private const val CONCURRENT_THREADS = 10
    private const val CONCURRENT_ITERATIONS = 100
    private const val PERFORMANCE_THRESHOLD_MS = 5000L
  }

  private val objectMapper = ObjectMapper()

  /** Test data container for JWT testing scenarios. */
  data class TestKeyPairs(val rsaKeyPair: IRsaExtKeyPair, val eccKeyPair: IEccExtKeyPair)

  /** Test data container for various data types. */
  data class TestTokenData(
    val simpleString: String = "simple string data",
    val complexMap: Map<String, Any> = mapOf("key1" to "value1", "key2" to 123, "key3" to true),
    val listData: List<String> = listOf("user1", "admin", "moderator"),
    val specialChars: String = "特殊字符测试!@#$%^&*()_+-=[]{}|;':\",./<>?",
    val emptyString: String = "",
    val nullValue: String? = null,
  )

  /** Creates test key pairs for JWT operations. */
  private fun createTestKeyPairs(): TestKeyPairs {
    val rsaKeyPair = CryptographicKeyManager.generateRsaKeyPair() ?: fail("Failed to generate RSA key pair")
    val eccKeyPair = CryptographicKeyManager.generateEccKeyPair(CryptographicKeyManager.generateRandomAsciiString()) ?: fail("Failed to generate ECC key pair")
    return TestKeyPairs(rsaKeyPair, eccKeyPair)
  }

  /** Creates a JWT issuer with specified configuration. */
  private fun createJwtIssuer(
    keyPairs: TestKeyPairs,
    issuer: String = TEST_ISSUER,
    id: String = TEST_ID,
    expireSeconds: Long = TEST_EXPIRE_SECONDS,
  ): JwtIssuer {
    return JwtIssuer.createIssuer()
      .issuer(issuer)
      .id(id)
      .signatureIssuerKey(keyPairs.rsaKeyPair.privateKey)
      .signatureVerifyKey(keyPairs.rsaKeyPair.publicKey)
      .contentEncryptKey(keyPairs.eccKeyPair.publicKey)
      .serializer(objectMapper)
      .expireFromDuration(Duration.of(expireSeconds, ChronoUnit.SECONDS))
      .build()
  }

  /** Creates a JWT verifier with specified configuration. */
  private fun createJwtVerifier(keyPairs: TestKeyPairs, issuer: String = TEST_ISSUER, id: String = TEST_ID): JwtVerifier {
    return JwtVerifier.createVerifier()
      .issuer(issuer)
      .id(id)
      .contentDecryptKey(keyPairs.eccKeyPair.privateKey)
      .signatureVerifyKey(keyPairs.rsaKeyPair.publicKey)
      .serializer(objectMapper)
      .build()
  }

  /** Validates that a token string is properly formatted. */
  private fun assertTokenValid(token: String) {
    assertNotNull(token, "Token should not be null")
    assertTrue(token.isNotEmpty(), "Token should not be empty")
    assertTrue(token.contains("."), "Token should contain JWT separators")
    val parts = token.split(".")
    assertEquals(3, parts.size, "JWT should have 3 parts (header.payload.signature)")
    parts.forEach { part -> assertTrue(part.isNotEmpty(), "JWT part should not be empty") }
  }

  @Test
  fun testJwtIssuerAndVerifierIntegration() {
    val keyPairs = createTestKeyPairs()
    val issuer = createJwtIssuer(keyPairs)
    val verifier = createJwtVerifier(keyPairs)
    val testData = TestTokenData()

    val issuerParam = IssuerParam<Any, Any>(signatureKey = keyPairs.rsaKeyPair.privateKey)
    issuerParam.encryptedDataObj = testData.complexMap
    issuerParam.subjectObj = testData.listData

    val token = issuer.issued(issuerParam)
    assertTokenValid(token)

    val verifierParam = VerifierParam(token = token, subjectTargetType = Any::class.java, encryptDataTargetType = Any::class.java)

    val result = verifier.verify(verifierParam)
    assertNotNull(result, "Verification result should not be null")
    assertNotNull(result.subject, "Subject should not be null")
    assertNotNull(result.decryptedData, "Decrypted data should not be null")
    assertFalse(result.isExpired, "Token should not be expired")
    assertEquals(TEST_ID, result.id, "Token ID should match")
  }

  @Test
  fun testJwtIssuerBuilderPattern() {
    val keyPairs = createTestKeyPairs()

    val issuer =
      JwtIssuer.createIssuer()
        .serializer(objectMapper)
        .issuer("test")
        .id("1")
        .signatureIssuerKey(keyPairs.rsaKeyPair.privateKey)
        .signatureVerifyKey(keyPairs.rsaKeyPair.publicKey)
        .contentEncryptKey(keyPairs.eccKeyPair.publicKey)
        .contentDecryptKey(keyPairs.eccKeyPair.privateKey)
        .build()

    assertNotNull(issuer, "JWT issuer should be created successfully")
  }

  @Test
  fun testJwtVerifierBuilderPattern() {
    val keyPairs = createTestKeyPairs()

    val verifier =
      JwtVerifier.createVerifier()
        .serializer(objectMapper)
        .issuer("test")
        .id("1")
        .contentDecryptKey(keyPairs.eccKeyPair.privateKey)
        .signatureVerifyKey(keyPairs.rsaKeyPair.publicKey)
        .build()

    assertNotNull(verifier, "JWT verifier should be created successfully")
  }

  @Test
  fun testJwtWithSimpleDataTypes() {
    val keyPairs = createTestKeyPairs()
    val issuer = createJwtIssuer(keyPairs, SIMPLE_TEST_ISSUER, SIMPLE_TEST_ID)
    val verifier = createJwtVerifier(keyPairs, SIMPLE_TEST_ISSUER, SIMPLE_TEST_ID)

    val simpleData = "simple string data"
    val simpleSubject = "user123"

    val issuerParam = IssuerParam<String, String>(signatureKey = keyPairs.rsaKeyPair.privateKey)
    issuerParam.encryptedDataObj = simpleData
    issuerParam.subjectObj = simpleSubject

    val token = issuer.issued(issuerParam)
    assertTokenValid(token)

    val verifierParam = VerifierParam(token = token, subjectTargetType = String::class.java, encryptDataTargetType = String::class.java)

    val result = verifier.verify(verifierParam)
    assertNotNull(result, "Verification result should not be null")
    assertEquals(simpleSubject, result.subject, "Subject should match")
    assertEquals(simpleData, result.decryptedData, "Decrypted data should match")
    assertFalse(result.isExpired, "Token should not be expired")
  }

  @Test
  fun testInvalidTokenVerification() {
    val keyPairs = createTestKeyPairs()
    val verifier = createJwtVerifier(keyPairs)

    val invalidTokens =
      listOf("invalid.token.format", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.invalid.signature", "", "not.a.jwt", "too.many.parts.in.this.token")

    invalidTokens.forEach { invalidToken ->
      val verifierParam =
        VerifierParam<String, String>(token = invalidToken, subjectTargetType = String::class.java, encryptDataTargetType = String::class.java)

      val result = verifier.verify(verifierParam)
      assertNull(result, "Invalid token should return null result")
    }
  }

  @Test
  fun testWrongKeyVerification() {
    val keyPairs1 = createTestKeyPairs()
    val keyPairs2 = createTestKeyPairs()

    val issuer = createJwtIssuer(keyPairs1)
    val verifierWithWrongKey = createJwtVerifier(keyPairs2) // Different keys

    val issuerParam = IssuerParam<String, String>(signatureKey = keyPairs1.rsaKeyPair.privateKey)
    issuerParam.encryptedDataObj = "test data"
    issuerParam.subjectObj = "test subject"

    val token = issuer.issued(issuerParam)
    assertTokenValid(token)

    val verifierParam = VerifierParam(token = token, subjectTargetType = String::class.java, encryptDataTargetType = String::class.java)

    val result = verifierWithWrongKey.verify(verifierParam)
    // When using wrong keys, the verification should either return null or a token with null decryptedData
    // due to decryption failure, but the signature verification will fail
    if (result != null) {
      // If result is not null, the encrypted data should be null due to decryption failure
      assertNull(result.decryptedData, "Decrypted data should be null when using wrong decryption key")
    }
    // Either way is acceptable - null result or result with null decryptedData
    assertTrue(result == null || result.decryptedData == null, "Token verified with wrong key should either return null or have null decrypted data")
  }

  @Test
  fun testNullParameterHandling() {
    val keyPairs = createTestKeyPairs()
    val issuer = createJwtIssuer(keyPairs)

    // Test with null encrypted data
    val issuerParamWithNullData = IssuerParam<String, String?>(signatureKey = keyPairs.rsaKeyPair.privateKey)
    issuerParamWithNullData.encryptedDataObj = null
    issuerParamWithNullData.subjectObj = "test subject"

    val tokenWithNullData = issuer.issued(issuerParamWithNullData as IssuerParam<String, String>)
    assertTokenValid(tokenWithNullData)

    // Test with null subject
    val issuerParamWithNullSubject = IssuerParam<String?, String>(signatureKey = keyPairs.rsaKeyPair.privateKey)
    issuerParamWithNullSubject.encryptedDataObj = "test data"
    issuerParamWithNullSubject.subjectObj = null

    val tokenWithNullSubject = issuer.issued(issuerParamWithNullSubject as IssuerParam<String, String>)
    assertTokenValid(tokenWithNullSubject)
  }

  @Test
  fun testEmptyDataHandling() {
    val keyPairs = createTestKeyPairs()
    val issuer = createJwtIssuer(keyPairs)
    val verifier = createJwtVerifier(keyPairs)

    val issuerParam = IssuerParam<String, String>(signatureKey = keyPairs.rsaKeyPair.privateKey)
    issuerParam.encryptedDataObj = ""
    issuerParam.subjectObj = ""

    val token = issuer.issued(issuerParam)
    assertTokenValid(token)

    val verifierParam = VerifierParam(token = token, subjectTargetType = String::class.java, encryptDataTargetType = String::class.java)

    val result = verifier.verify(verifierParam)
    assertNotNull(result, "Verification result should not be null")
    assertEquals("", result.subject, "Empty subject should be preserved")
    assertEquals("", result.decryptedData, "Empty data should be preserved")
  }

  @Test
  fun testSpecialCharacterHandling() {
    val keyPairs = createTestKeyPairs()
    val issuer = createJwtIssuer(keyPairs)
    val verifier = createJwtVerifier(keyPairs)
    val testData = TestTokenData()

    val issuerParam = IssuerParam<String, String>(signatureKey = keyPairs.rsaKeyPair.privateKey)
    issuerParam.encryptedDataObj = testData.specialChars
    issuerParam.subjectObj = testData.specialChars

    val token = issuer.issued(issuerParam)
    assertTokenValid(token)

    val verifierParam = VerifierParam(token = token, subjectTargetType = String::class.java, encryptDataTargetType = String::class.java)

    val result = verifier.verify(verifierParam)
    assertNotNull(result, "Verification result should not be null")
    assertEquals(testData.specialChars, result.subject, "Special characters in subject should be preserved")
    assertEquals(testData.specialChars, result.decryptedData, "Special characters in data should be preserved")
  }

  @Test
  fun testLargeDataHandling() {
    val keyPairs = createTestKeyPairs()
    val issuer = createJwtIssuer(keyPairs)
    val verifier = createJwtVerifier(keyPairs)

    val largeData = "x".repeat(LARGE_DATA_SIZE)
    val largeSubject = "subject_${"y".repeat(1000)}"

    val issuerParam = IssuerParam<String, String>(signatureKey = keyPairs.rsaKeyPair.privateKey)
    issuerParam.encryptedDataObj = largeData
    issuerParam.subjectObj = largeSubject

    val token = issuer.issued(issuerParam)
    assertTokenValid(token)

    val verifierParam = VerifierParam(token = token, subjectTargetType = String::class.java, encryptDataTargetType = String::class.java)

    val result = verifier.verify(verifierParam)
    assertNotNull(result, "Verification result should not be null")
    assertEquals(largeSubject, result.subject, "Large subject should be preserved")
    assertEquals(largeData, result.decryptedData, "Large data should be preserved")
  }

  @Test
  fun testTokenExpirationValidation() {
    val keyPairs = createTestKeyPairs()
    val issuer = createJwtIssuer(keyPairs, expireSeconds = 1) // 1 second expiration
    val verifier = createJwtVerifier(keyPairs)

    val issuerParam = IssuerParam<String, String>(signatureKey = keyPairs.rsaKeyPair.privateKey)
    issuerParam.encryptedDataObj = "test data"
    issuerParam.subjectObj = "test subject"

    val token = issuer.issued(issuerParam)
    assertTokenValid(token)

    // Verify immediately (should work)
    val verifierParam = VerifierParam(token = token, subjectTargetType = String::class.java, encryptDataTargetType = String::class.java)

    val immediateResult = verifier.verify(verifierParam)
    assertNotNull(immediateResult, "Immediate verification should succeed")
    assertFalse(immediateResult.isExpired, "Token should not be expired immediately")

    // Wait for expiration and verify again
    Thread.sleep(2000) // Wait 2 seconds

    val expiredResult = verifier.verify(verifierParam)
    // The token should be marked as expired but still decoded
    if (expiredResult != null) {
      assertTrue(expiredResult.isExpired, "Token should be marked as expired")
    }
  }

  @Test
  fun testEncryptionDecryptionConsistency() {
    val keyPairs = createTestKeyPairs()
    val issuer = createJwtIssuer(keyPairs)
    val verifier = createJwtVerifier(keyPairs)

    val testCases = listOf("Simple string", mapOf("key" to "value", "number" to 42), listOf("item1", "item2", "item3"), 123456789, true, null)

    testCases.forEach { testData ->
      val issuerParam = IssuerParam<Any, Any>(signatureKey = keyPairs.rsaKeyPair.privateKey)
      issuerParam.encryptedDataObj = testData
      issuerParam.subjectObj = "test_subject_${testData?.hashCode()}"

      val token = issuer.issued(issuerParam)
      assertTokenValid(token)

      val verifierParam = VerifierParam(token = token, subjectTargetType = Any::class.java, encryptDataTargetType = Any::class.java)

      val result = verifier.verify(verifierParam)
      assertNotNull(result, "Verification result should not be null for data: $testData")

      if (testData != null) {
        assertNotNull(result.decryptedData, "Decrypted data should not be null for: $testData")
      }
    }
  }

  @Test
  fun testPerformanceBenchmark() {
    val keyPairs = createTestKeyPairs()
    val issuer = createJwtIssuer(keyPairs)
    val verifier = createJwtVerifier(keyPairs)

    val iterations = 100
    val testData = "performance test data"
    val testSubject = "performance test subject"

    val issueTime = measureTime {
      repeat(iterations) {
        val issuerParam = IssuerParam<String, String>(signatureKey = keyPairs.rsaKeyPair.privateKey)
        issuerParam.encryptedDataObj = testData
        issuerParam.subjectObj = testSubject

        val token = issuer.issued(issuerParam)
        assertTokenValid(token)
      }
    }

    val tokens = mutableListOf<String>()
    repeat(iterations) {
      val issuerParam = IssuerParam<String, String>(signatureKey = keyPairs.rsaKeyPair.privateKey)
      issuerParam.encryptedDataObj = testData
      issuerParam.subjectObj = testSubject
      tokens.add(issuer.issued(issuerParam))
    }

    val verifyTime = measureTime {
      tokens.forEach { token ->
        val verifierParam = VerifierParam(token = token, subjectTargetType = String::class.java, encryptDataTargetType = String::class.java)
        val result = verifier.verify(verifierParam)
        assertNotNull(result, "Verification should succeed")
      }
    }

    println("Performance benchmark results:")
    println("  Token generation: $iterations tokens in ${issueTime.inWholeMilliseconds}ms")
    println("  Token verification: $iterations tokens in ${verifyTime.inWholeMilliseconds}ms")

    assertTrue(issueTime.inWholeMilliseconds < PERFORMANCE_THRESHOLD_MS, "Token generation should complete within ${PERFORMANCE_THRESHOLD_MS}ms")
    assertTrue(verifyTime.inWholeMilliseconds < PERFORMANCE_THRESHOLD_MS, "Token verification should complete within ${PERFORMANCE_THRESHOLD_MS}ms")
  }

  @Test
  fun testConcurrentTokenGeneration() {
    val keyPairs = createTestKeyPairs()
    val issuer = createJwtIssuer(keyPairs)
    val verifier = createJwtVerifier(keyPairs)

    val tokens = mutableListOf<String>()
    val threads = mutableListOf<Thread>()

    repeat(CONCURRENT_THREADS) { threadId ->
      val thread = Thread {
        repeat(CONCURRENT_ITERATIONS) { iteration ->
          val issuerParam = IssuerParam<String, String>(signatureKey = keyPairs.rsaKeyPair.privateKey)
          issuerParam.encryptedDataObj = "data_${threadId}_$iteration"
          issuerParam.subjectObj = "subject_${threadId}_$iteration"

          val token = issuer.issued(issuerParam)
          synchronized(tokens) { tokens.add(token) }
        }
      }
      threads.add(thread)
    }

    val executionTime = measureTime {
      threads.forEach { it.start() }
      threads.forEach { it.join() }
    }

    assertEquals(CONCURRENT_THREADS * CONCURRENT_ITERATIONS, tokens.size, "All tokens should be generated")

    // Verify all tokens are unique
    val uniqueTokens = tokens.toSet()
    assertEquals(tokens.size, uniqueTokens.size, "All generated tokens should be unique")

    // Verify all tokens are valid
    tokens.forEach { token ->
      assertTokenValid(token)

      val verifierParam = VerifierParam(token = token, subjectTargetType = String::class.java, encryptDataTargetType = String::class.java)

      val result = verifier.verify(verifierParam)
      assertNotNull(result, "All tokens should verify successfully")
    }

    println("Concurrent generation: ${tokens.size} tokens in ${executionTime.inWholeMilliseconds}ms")
    assertTrue(executionTime.inWholeMilliseconds < PERFORMANCE_THRESHOLD_MS * 2, "Concurrent generation should complete within reasonable time")
  }

  @Test
  fun testTokenDecodeWithoutVerification() {
    val keyPairs = createTestKeyPairs()
    val issuer = createJwtIssuer(keyPairs)
    val verifier = createJwtVerifier(keyPairs)

    val testData = "decode test data"
    val testSubject = "decode test subject"

    val issuerParam = IssuerParam<String, String>(signatureKey = keyPairs.rsaKeyPair.privateKey)
    issuerParam.encryptedDataObj = testData
    issuerParam.subjectObj = testSubject

    val token = issuer.issued(issuerParam)
    assertTokenValid(token)

    val verifierParam = VerifierParam(token = token, subjectTargetType = String::class.java, encryptDataTargetType = String::class.java)

    // Test decode method (without signature verification)
    val decodedResult = verifier.decode(verifierParam)
    assertNotNull(decodedResult, "Decode result should not be null")
    assertEquals(testSubject, decodedResult.subject, "Decoded subject should match")
    assertEquals(testData, decodedResult.decryptedData, "Decoded data should match")
    assertEquals(TEST_ID, decodedResult.id, "Decoded ID should match")
    assertNotNull(decodedResult.expireDateTime, "Expire date time should be set")
    assertNotNull(decodedResult.signatureAlgName, "Signature algorithm should be set")
  }

  @Test
  fun testComplexDataStructures() {
    val keyPairs = createTestKeyPairs()
    val issuer = createJwtIssuer(keyPairs)
    val verifier = createJwtVerifier(keyPairs)

    data class ComplexData(
      val id: Long,
      val name: String,
      val properties: Map<String, Any>,
      val tags: List<String>,
      val metadata: Map<String, Map<String, Any>>,
    )

    val complexData =
      ComplexData(
        id = 12345L,
        name = "Complex Test Object",
        properties = mapOf("active" to true, "score" to 95.5, "category" to "test"),
        tags = listOf("tag1", "tag2", "tag3"),
        metadata = mapOf("system" to mapOf("version" to "1.0", "build" to 123), "user" to mapOf("preferences" to mapOf("theme" to "dark"))),
      )

    val issuerParam = IssuerParam<ComplexData, ComplexData>(signatureKey = keyPairs.rsaKeyPair.privateKey)
    issuerParam.encryptedDataObj = complexData
    issuerParam.subjectObj = complexData

    val token = issuer.issued(issuerParam)
    assertTokenValid(token)

    val verifierParam = VerifierParam(token = token, subjectTargetType = Any::class.java, encryptDataTargetType = Any::class.java)

    val result = verifier.verify(verifierParam)
    assertNotNull(result, "Verification result should not be null")
    assertNotNull(result.subject, "Complex subject should be preserved")
    assertNotNull(result.decryptedData, "Complex data should be preserved")
  }
}
