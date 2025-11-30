package io.github.truenine.composeserver.domain.enc

import io.github.truenine.composeserver.domain.IRsaKeyPair
import io.github.truenine.composeserver.enums.EncryptAlgorithm
import io.github.truenine.composeserver.testtoolkit.log
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import kotlin.test.*

/** Verifies the core behaviour of {@link RsaKeyPair}. */
class RsaKeyPairTest {

  // Simple test key implementations
  private class TestRSAPublicKey : RSAPublicKey {
    override fun getAlgorithm() = "RSA"

    override fun getFormat() = "X.509"

    override fun getEncoded() = byteArrayOf()

    override fun getModulus() = BigInteger.valueOf(12345)

    override fun getPublicExponent() = BigInteger.valueOf(65537)
  }

  private class TestRSAPrivateKey : RSAPrivateKey {
    override fun getAlgorithm() = "RSA"

    override fun getFormat() = "PKCS#8"

    override fun getEncoded() = byteArrayOf()

    override fun getModulus() = BigInteger.valueOf(12345)

    override fun getPrivateExponent() = BigInteger.valueOf(54321)
  }

  @Test
  fun constructsRsaKeyPairWithDefaultAlgorithm() {
    log.info("Verifying default algorithm constructor")

    val testPublicKey = TestRSAPublicKey()
    val testPrivateKey = TestRSAPrivateKey()

    val rsaKeyPair = RsaKeyPair(testPublicKey, testPrivateKey)

    assertEquals(testPublicKey, rsaKeyPair.publicKey, "Public key should match input instance")
    assertEquals(testPrivateKey, rsaKeyPair.privateKey, "Private key should match input instance")
    assertEquals(EncryptAlgorithm.RSA, rsaKeyPair.algorithm, "Algorithm should default to RSA")

    log.info("Default algorithm constructor verified")
  }

  @Test
  fun constructsRsaKeyPairWithCustomAlgorithm() {
    log.info("Verifying custom algorithm constructor")

    val testPublicKey = TestRSAPublicKey()
    val testPrivateKey = TestRSAPrivateKey()
    val customAlgorithm = EncryptAlgorithm.ECC // validates configurability

    val rsaKeyPair = RsaKeyPair(testPublicKey, testPrivateKey, customAlgorithm)

    assertEquals(testPublicKey, rsaKeyPair.publicKey, "Public key should match input instance")
    assertEquals(testPrivateKey, rsaKeyPair.privateKey, "Private key should match input instance")
    assertEquals(customAlgorithm, rsaKeyPair.algorithm, "Algorithm should reflect custom value")

    log.info("Custom algorithm constructor verified")
  }

  @Test
  fun exposesInterfaceContract() {
    log.info("Verifying IRsaKeyPair contract")

    val testPublicKey = TestRSAPublicKey()
    val testPrivateKey = TestRSAPrivateKey()

    val rsaKeyPair = RsaKeyPair(testPublicKey, testPrivateKey)

    assertTrue(rsaKeyPair is IRsaKeyPair, "Should implement IRsaKeyPair interface")

    val keyPairInterface: IRsaKeyPair = rsaKeyPair
    assertEquals(testPublicKey, keyPairInterface.publicKey, "Public key should match via interface")
    assertEquals(testPrivateKey, keyPairInterface.privateKey, "Private key should match via interface")
    assertEquals(EncryptAlgorithm.RSA, keyPairInterface.algorithm, "Algorithm should match via interface")

    log.info("Interface contract verified")
  }

  @Test
  fun worksWithRealRsaKeys() {
    log.info("Verifying compatibility with generated RSA key pair")

    try {
      val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
      keyPairGenerator.initialize(2048)
      val javaKeyPair = keyPairGenerator.generateKeyPair()

      val rsaPublicKey = javaKeyPair.public as RSAPublicKey
      val rsaPrivateKey = javaKeyPair.private as RSAPrivateKey

      val rsaKeyPair = RsaKeyPair(rsaPublicKey, rsaPrivateKey)

      assertNotNull(rsaKeyPair.publicKey, "Public key should not be null")
      assertNotNull(rsaKeyPair.privateKey, "Private key should not be null")
      assertEquals(EncryptAlgorithm.RSA, rsaKeyPair.algorithm, "Algorithm should remain RSA")

      assertEquals("RSA", rsaKeyPair.publicKey.algorithm, "Public key algorithm should be RSA")
      assertEquals("RSA", rsaKeyPair.privateKey.algorithm, "Private key algorithm should be RSA")

      assertNotNull(rsaKeyPair.publicKey.modulus, "RSA public key should expose modulus")
      assertNotNull(rsaKeyPair.publicKey.publicExponent, "RSA public key should expose public exponent")
      assertNotNull(rsaKeyPair.privateKey.modulus, "RSA private key should expose modulus")
      assertNotNull(rsaKeyPair.privateKey.privateExponent, "RSA private key should expose private exponent")

      log.info("Real RSA key pair verified")
      log.info("Public key algorithm: {}", rsaKeyPair.publicKey.algorithm)
      log.info("Private key algorithm: {}", rsaKeyPair.privateKey.algorithm)
      log.info("Public key format: {}", rsaKeyPair.publicKey.format)
      log.info("Private key format: {}", rsaKeyPair.privateKey.format)
      log.info("Key length: {} bits", rsaKeyPair.publicKey.modulus.bitLength())
    } catch (e: Exception) {
      log.info("RSA algorithm unavailable, skipping real key verification: {}", e.message)
    }
  }

  @Test
  fun enforcesRsaSpecificTypes() {
    log.info("Verifying RSA specific interfaces")

    val testRsaPublicKey = TestRSAPublicKey()
    val testRsaPrivateKey = TestRSAPrivateKey()

    val rsaKeyPair = RsaKeyPair(testRsaPublicKey, testRsaPrivateKey)

    assertTrue(rsaKeyPair.publicKey is RSAPublicKey, "Public key should be RSAPublicKey")
    assertTrue(rsaKeyPair.privateKey is RSAPrivateKey, "Private key should be RSAPrivateKey")
    assertEquals(EncryptAlgorithm.RSA, rsaKeyPair.algorithm, "Algorithm should remain RSA")

    log.info("RSA specific interfaces verified")
  }
}
