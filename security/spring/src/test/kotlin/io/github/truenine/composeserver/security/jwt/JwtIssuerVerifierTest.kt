package io.github.truenine.composeserver.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.security.crypto.Keys
import io.github.truenine.composeserver.security.jwt.consts.IssuerParam
import io.github.truenine.composeserver.security.jwt.consts.VerifierParam
import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

/** Test JWT issuer and verifier functionality */
class JwtIssuerVerifierTest {

  private val objectMapper = ObjectMapper()

  @Test
  fun `test JWT issuer and verifier integration`() {
    val eccPair = Keys.generateEccKeyPair()!!
    val rsaPair = Keys.generateRsaKeyPair()!!

    val issuer =
      JwtIssuer.createIssuer()
        .issuer("test-issuer")
        .id("test-id")
        .signatureIssuerKey(rsaPair.privateKey)
        .signatureVerifyKey(rsaPair.publicKey)
        .contentEncryptKey(eccPair.publicKey)
        .serializer(objectMapper)
        .expireFromDuration(Duration.of(100, ChronoUnit.SECONDS))
        .build()

    val verifier =
      JwtVerifier.createVerifier()
        .issuer("test-issuer")
        .id("test-id")
        .contentDecryptKey(eccPair.privateKey)
        .signatureVerifyKey(rsaPair.publicKey)
        .serializer(objectMapper)
        .build()

    val testData = mapOf("key1" to "value1", "key2" to 123)
    val testSubject = listOf("user1", "admin")

    val issuerParam = IssuerParam<Any, Any>(signatureKey = rsaPair.privateKey)
    issuerParam.encryptedDataObj = testData
    issuerParam.subjectObj = testSubject

    val token = issuer.issued(issuerParam)
    assertNotNull(token)
    assertTrue(token.isNotEmpty())

    val verifierParam = VerifierParam(token = token, subjectTargetType = Any::class.java, encryptDataTargetType = Any::class.java)

    val result = verifier.verify(verifierParam)
    assertNotNull(result)
    assertNotNull(result.subject)
    assertNotNull(result.decryptedData)
  }

  @Test
  fun `test JWT issuer builder pattern`() {
    val eccPair = Keys.generateEccKeyPair()!!
    val rsaPair = Keys.generateRsaKeyPair()!!

    val issuer =
      JwtIssuer.createIssuer()
        .serializer(objectMapper)
        .issuer("test")
        .id("1")
        .signatureIssuerKey(rsaPair.privateKey)
        .signatureVerifyKey(rsaPair.publicKey)
        .contentEncryptKey(eccPair.publicKey)
        .contentDecryptKey(eccPair.privateKey)
        .build()

    assertNotNull(issuer)
  }

  @Test
  fun `test JWT verifier builder pattern`() {
    val eccPair = Keys.generateEccKeyPair()!!
    val rsaPair = Keys.generateRsaKeyPair()!!

    val verifier =
      JwtVerifier.createVerifier()
        .serializer(objectMapper)
        .issuer("test")
        .id("1")
        .contentDecryptKey(eccPair.privateKey)
        .signatureVerifyKey(rsaPair.publicKey)
        .build()

    assertNotNull(verifier)
  }

  @Test
  fun `test JWT with simple data types`() {
    val eccPair = Keys.generateEccKeyPair()!!
    val rsaPair = Keys.generateRsaKeyPair()!!

    val issuer =
      JwtIssuer.createIssuer()
        .issuer("simple-test")
        .id("simple-id")
        .signatureIssuerKey(rsaPair.privateKey)
        .signatureVerifyKey(rsaPair.publicKey)
        .contentEncryptKey(eccPair.publicKey)
        .serializer(objectMapper)
        .build()

    val verifier =
      JwtVerifier.createVerifier()
        .issuer("simple-test")
        .id("simple-id")
        .contentDecryptKey(eccPair.privateKey)
        .signatureVerifyKey(rsaPair.publicKey)
        .serializer(objectMapper)
        .build()

    val simpleData = "simple string data"
    val simpleSubject = "user123"

    val issuerParam = IssuerParam<String, String>(signatureKey = rsaPair.privateKey)
    issuerParam.encryptedDataObj = simpleData
    issuerParam.subjectObj = simpleSubject

    val token = issuer.issued(issuerParam)
    assertNotNull(token)

    val verifierParam = VerifierParam(token = token, subjectTargetType = String::class.java, encryptDataTargetType = String::class.java)

    val result = verifier.verify(verifierParam)
    assertNotNull(result)
    assertEquals(simpleSubject, result.subject)
    assertEquals(simpleData, result.decryptedData)
  }
}
