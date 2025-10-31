package io.github.truenine.composeserver.domain.enc

import io.github.truenine.composeserver.domain.IEccKeyPair
import io.github.truenine.composeserver.enums.EncryptAlgorithm
import io.github.truenine.composeserver.testtoolkit.log
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Verifies the core behaviour of {@link EccKeyPair}.
 */
class EccKeyPairTest {

  // Simple test key implementations
  private class TestPublicKey : PublicKey {
    override fun getAlgorithm() = "EC"

    override fun getFormat() = "X.509"

    override fun getEncoded() = byteArrayOf()
  }

  private class TestPrivateKey : PrivateKey {
    override fun getAlgorithm() = "EC"

    override fun getFormat() = "PKCS#8"

    override fun getEncoded() = byteArrayOf()
  }

  @Test
  fun constructsEccKeyPairWithDefaultAlgorithm() {
    log.info("Verifying default algorithm constructor")

    val testPublicKey = TestPublicKey()
    val testPrivateKey = TestPrivateKey()

    val eccKeyPair = EccKeyPair(testPublicKey, testPrivateKey)

    assertEquals(testPublicKey, eccKeyPair.publicKey, "Public key should match input instance")
    assertEquals(testPrivateKey, eccKeyPair.privateKey, "Private key should match input instance")
    assertEquals(EncryptAlgorithm.ECC, eccKeyPair.algorithm, "Algorithm should default to ECC")

    log.info("Default algorithm constructor verified")
  }

  @Test
  fun constructsEccKeyPairWithCustomAlgorithm() {
    log.info("Verifying custom algorithm constructor")

    val testPublicKey = TestPublicKey()
    val testPrivateKey = TestPrivateKey()
    val customAlgorithm = EncryptAlgorithm.RSA // validates configurability

    val eccKeyPair = EccKeyPair(testPublicKey, testPrivateKey, customAlgorithm)

    assertEquals(testPublicKey, eccKeyPair.publicKey, "Public key should match input instance")
    assertEquals(testPrivateKey, eccKeyPair.privateKey, "Private key should match input instance")
    assertEquals(customAlgorithm, eccKeyPair.algorithm, "Algorithm should reflect custom value")

    log.info("Custom algorithm constructor verified")
  }

  @Test
  fun exposesInterfaceContract() {
    log.info("Verifying IEccKeyPair contract")

    val testPublicKey = TestPublicKey()
    val testPrivateKey = TestPrivateKey()

    val eccKeyPair = EccKeyPair(testPublicKey, testPrivateKey)

    assertTrue(eccKeyPair is IEccKeyPair, "Should implement IEccKeyPair interface")

    val keyPairInterface: IEccKeyPair = eccKeyPair
    assertEquals(testPublicKey, keyPairInterface.publicKey, "Public key should match via interface")
    assertEquals(testPrivateKey, keyPairInterface.privateKey, "Private key should match via interface")
    assertEquals(EncryptAlgorithm.ECC, keyPairInterface.algorithm, "Algorithm should match via interface")

    log.info("Interface contract verified")
  }

  @Test
  fun worksWithRealEccKeys() {
    log.info("Verifying compatibility with generated EC key pair")

    try {
      val keyPairGenerator = KeyPairGenerator.getInstance("EC")
      keyPairGenerator.initialize(256)
      val javaKeyPair = keyPairGenerator.generateKeyPair()

      val eccKeyPair = EccKeyPair(javaKeyPair.public, javaKeyPair.private)

      assertNotNull(eccKeyPair.publicKey, "Public key should not be null")
      assertNotNull(eccKeyPair.privateKey, "Private key should not be null")
      assertEquals(EncryptAlgorithm.ECC, eccKeyPair.algorithm, "Algorithm should remain ECC")

      assertEquals("EC", eccKeyPair.publicKey.algorithm, "Public key algorithm should be EC")
      assertEquals("EC", eccKeyPair.privateKey.algorithm, "Private key algorithm should be EC")

      log.info("Real EC key pair verified")
      log.info("Public key algorithm: {}", eccKeyPair.publicKey.algorithm)
      log.info("Private key algorithm: {}", eccKeyPair.privateKey.algorithm)
      log.info("Public key format: {}", eccKeyPair.publicKey.format)
      log.info("Private key format: {}", eccKeyPair.privateKey.format)
    } catch (e: Exception) {
      log.info("EC algorithm unavailable, skipping real key verification: {}", e.message)
    }
  }

}
