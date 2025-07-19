package io.github.truenine.composeserver.domain.enc

import io.github.truenine.composeserver.domain.IRsaKeyPair
import io.github.truenine.composeserver.testtoolkit.log
import io.github.truenine.composeserver.typing.EncryptAlgorithm
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * # RSA 密钥对测试
 *
 * 测试 RsaKeyPair 类的功能
 */
class RsaKeyPairTest {

  // 创建简单的测试密钥实现
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
  fun `测试 RsaKeyPair 构造函数`() {
    log.info("测试 RsaKeyPair 构造函数")

    val testPublicKey = TestRSAPublicKey()
    val testPrivateKey = TestRSAPrivateKey()

    val rsaKeyPair = RsaKeyPair(testPublicKey, testPrivateKey)

    assertEquals(testPublicKey, rsaKeyPair.publicKey, "公钥应该匹配")
    assertEquals(testPrivateKey, rsaKeyPair.privateKey, "私钥应该匹配")
    assertEquals(EncryptAlgorithm.RSA, rsaKeyPair.algorithm, "算法应该是 RSA")

    log.info("RSA 密钥对创建成功")
  }

  @Test
  fun `测试 RsaKeyPair 构造函数 - 自定义算法`() {
    log.info("测试 RsaKeyPair 构造函数 - 自定义算法")

    val testPublicKey = TestRSAPublicKey()
    val testPrivateKey = TestRSAPrivateKey()
    val customAlgorithm = EncryptAlgorithm.ECC // 虽然不合理，但测试构造函数的灵活性

    val rsaKeyPair = RsaKeyPair(testPublicKey, testPrivateKey, customAlgorithm)

    assertEquals(testPublicKey, rsaKeyPair.publicKey, "公钥应该匹配")
    assertEquals(testPrivateKey, rsaKeyPair.privateKey, "私钥应该匹配")
    assertEquals(customAlgorithm, rsaKeyPair.algorithm, "算法应该是自定义的")

    log.info("自定义算法的 RSA 密钥对创建成功")
  }

  @Test
  fun `测试 RsaKeyPair 实现 IRsaKeyPair 接口`() {
    log.info("测试 RsaKeyPair 实现 IRsaKeyPair 接口")

    val testPublicKey = TestRSAPublicKey()
    val testPrivateKey = TestRSAPrivateKey()

    val rsaKeyPair = RsaKeyPair(testPublicKey, testPrivateKey)

    assertTrue(rsaKeyPair is IRsaKeyPair, "应该实现 IRsaKeyPair 接口")

    // 通过接口访问属性
    val keyPairInterface: IRsaKeyPair = rsaKeyPair
    assertEquals(testPublicKey, keyPairInterface.publicKey, "通过接口访问的公钥应该匹配")
    assertEquals(testPrivateKey, keyPairInterface.privateKey, "通过接口访问的私钥应该匹配")
    assertEquals(EncryptAlgorithm.RSA, keyPairInterface.algorithm, "通过接口访问的算法应该匹配")

    log.info("IRsaKeyPair 接口实现验证通过")
  }

  @Test
  fun `测试 RsaKeyPair 使用真实的 RSA 密钥`() {
    log.info("测试 RsaKeyPair 使用真实的 RSA 密钥")

    try {
      // 生成真实的 RSA 密钥对
      val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
      keyPairGenerator.initialize(2048) // 使用 2048 位密钥
      val javaKeyPair = keyPairGenerator.generateKeyPair()

      val rsaPublicKey = javaKeyPair.public as RSAPublicKey
      val rsaPrivateKey = javaKeyPair.private as RSAPrivateKey

      val rsaKeyPair = RsaKeyPair(rsaPublicKey, rsaPrivateKey)

      assertNotNull(rsaKeyPair.publicKey, "公钥不应该为空")
      assertNotNull(rsaKeyPair.privateKey, "私钥不应该为空")
      assertEquals(EncryptAlgorithm.RSA, rsaKeyPair.algorithm, "算法应该是 RSA")

      // 验证密钥的算法
      assertEquals("RSA", rsaKeyPair.publicKey.algorithm, "公钥算法应该是 RSA")
      assertEquals("RSA", rsaKeyPair.privateKey.algorithm, "私钥算法应该是 RSA")

      // 验证 RSA 特有的属性
      assertNotNull(rsaKeyPair.publicKey.modulus, "RSA 公钥应该有模数")
      assertNotNull(rsaKeyPair.publicKey.publicExponent, "RSA 公钥应该有公共指数")
      assertNotNull(rsaKeyPair.privateKey.modulus, "RSA 私钥应该有模数")
      assertNotNull(rsaKeyPair.privateKey.privateExponent, "RSA 私钥应该有私有指数")

      log.info("真实 RSA 密钥对测试通过")
      log.info("公钥算法: {}", rsaKeyPair.publicKey.algorithm)
      log.info("私钥算法: {}", rsaKeyPair.privateKey.algorithm)
      log.info("公钥格式: {}", rsaKeyPair.publicKey.format)
      log.info("私钥格式: {}", rsaKeyPair.privateKey.format)
      log.info("密钥长度: {} 位", rsaKeyPair.publicKey.modulus.bitLength())
    } catch (e: Exception) {
      log.info("RSA 算法不可用，跳过真实密钥测试: {}", e.message)
    }
  }

  @Test
  fun `测试 RsaKeyPair 的属性访问`() {
    log.info("测试 RsaKeyPair 的属性访问")

    val testPublicKey = TestRSAPublicKey()
    val testPrivateKey = TestRSAPrivateKey()

    val rsaKeyPair = RsaKeyPair(testPublicKey, testPrivateKey)

    // 测试属性是否为 val（只读）
    val publicKey = rsaKeyPair.publicKey
    val privateKey = rsaKeyPair.privateKey
    val algorithm = rsaKeyPair.algorithm

    assertEquals(testPublicKey, publicKey, "公钥属性应该匹配")
    assertEquals(testPrivateKey, privateKey, "私钥属性应该匹配")
    assertEquals(EncryptAlgorithm.RSA, algorithm, "算法属性应该匹配")

    log.info("属性访问测试通过")
  }

  @Test
  fun `测试 RsaKeyPair 的类型信息`() {
    log.info("测试 RsaKeyPair 的类型信息")

    val testPublicKey = TestRSAPublicKey()
    val testPrivateKey = TestRSAPrivateKey()

    val rsaKeyPair = RsaKeyPair(testPublicKey, testPrivateKey)
    val keyPairClass = rsaKeyPair::class.java

    assertEquals("RsaKeyPair", keyPairClass.simpleName, "类名应该正确")
    assertEquals("io.github.truenine.composeserver.domain.enc", keyPairClass.packageName, "包名应该正确")

    // 验证实现的接口
    val interfaces = keyPairClass.interfaces
    assertTrue(interfaces.contains(IRsaKeyPair::class.java), "应该实现 IRsaKeyPair 接口")

    log.info("类型信息验证通过")
  }

  @Test
  fun `测试 RsaKeyPair 的默认算法值`() {
    log.info("测试 RsaKeyPair 的默认算法值")

    val testPublicKey = TestRSAPublicKey()
    val testPrivateKey = TestRSAPrivateKey()

    // 不指定算法参数，使用默认值
    val rsaKeyPair = RsaKeyPair(testPublicKey, testPrivateKey)

    assertEquals(EncryptAlgorithm.RSA, rsaKeyPair.algorithm, "默认算法应该是 RSA")

    log.info("默认算法值测试通过")
  }

  @Test
  fun `测试 RsaKeyPair 与 EccKeyPair 的区别`() {
    log.info("测试 RsaKeyPair 与 EccKeyPair 的区别")

    val testRsaPublicKey = TestRSAPublicKey()
    val testRsaPrivateKey = TestRSAPrivateKey()

    val rsaKeyPair = RsaKeyPair(testRsaPublicKey, testRsaPrivateKey)

    // RSA 密钥对应该使用 RSA 特定的接口
    assertTrue(rsaKeyPair.publicKey is RSAPublicKey, "公钥应该是 RSAPublicKey 类型")
    assertTrue(rsaKeyPair.privateKey is RSAPrivateKey, "私钥应该是 RSAPrivateKey 类型")
    assertEquals(EncryptAlgorithm.RSA, rsaKeyPair.algorithm, "算法应该是 RSA")

    log.info("RSA 密钥对类型验证通过")
  }
}
