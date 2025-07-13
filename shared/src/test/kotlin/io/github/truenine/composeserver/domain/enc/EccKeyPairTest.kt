package io.github.truenine.composeserver.domain.enc

import io.github.truenine.composeserver.domain.IEccKeyPair
import io.github.truenine.composeserver.testtoolkit.log
import io.github.truenine.composeserver.typing.EncryptAlgorithmTyping
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * # ECC 密钥对测试
 *
 * 测试 EccKeyPair 类的功能
 */
class EccKeyPairTest {

  // 创建简单的测试密钥实现
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
  fun `测试 EccKeyPair 构造函数`() {
    log.info("测试 EccKeyPair 构造函数")

    val testPublicKey = TestPublicKey()
    val testPrivateKey = TestPrivateKey()

    val eccKeyPair = EccKeyPair(testPublicKey, testPrivateKey)

    assertEquals(testPublicKey, eccKeyPair.publicKey, "公钥应该匹配")
    assertEquals(testPrivateKey, eccKeyPair.privateKey, "私钥应该匹配")
    assertEquals(EncryptAlgorithmTyping.ECC, eccKeyPair.algorithm, "算法应该是 ECC")

    log.info("ECC 密钥对创建成功")
  }

  @Test
  fun `测试 EccKeyPair 构造函数 - 自定义算法`() {
    log.info("测试 EccKeyPair 构造函数 - 自定义算法")

    val testPublicKey = TestPublicKey()
    val testPrivateKey = TestPrivateKey()
    val customAlgorithm = EncryptAlgorithmTyping.RSA // 虽然不合理，但测试构造函数的灵活性

    val eccKeyPair = EccKeyPair(testPublicKey, testPrivateKey, customAlgorithm)

    assertEquals(testPublicKey, eccKeyPair.publicKey, "公钥应该匹配")
    assertEquals(testPrivateKey, eccKeyPair.privateKey, "私钥应该匹配")
    assertEquals(customAlgorithm, eccKeyPair.algorithm, "算法应该是自定义的")

    log.info("自定义算法的 ECC 密钥对创建成功")
  }

  @Test
  fun `测试 EccKeyPair 实现 IEccKeyPair 接口`() {
    log.info("测试 EccKeyPair 实现 IEccKeyPair 接口")

    val testPublicKey = TestPublicKey()
    val testPrivateKey = TestPrivateKey()

    val eccKeyPair = EccKeyPair(testPublicKey, testPrivateKey)

    assertTrue(eccKeyPair is IEccKeyPair, "应该实现 IEccKeyPair 接口")

    // 通过接口访问属性
    val keyPairInterface: IEccKeyPair = eccKeyPair
    assertEquals(testPublicKey, keyPairInterface.publicKey, "通过接口访问的公钥应该匹配")
    assertEquals(testPrivateKey, keyPairInterface.privateKey, "通过接口访问的私钥应该匹配")
    assertEquals(EncryptAlgorithmTyping.ECC, keyPairInterface.algorithm, "通过接口访问的算法应该匹配")

    log.info("IEccKeyPair 接口实现验证通过")
  }

  @Test
  fun `测试 EccKeyPair 使用真实的 EC 密钥`() {
    log.info("测试 EccKeyPair 使用真实的 EC 密钥")

    try {
      // 生成真实的 EC 密钥对
      val keyPairGenerator = KeyPairGenerator.getInstance("EC")
      keyPairGenerator.initialize(256) // 使用 256 位密钥
      val javaKeyPair = keyPairGenerator.generateKeyPair()

      val eccKeyPair = EccKeyPair(javaKeyPair.public, javaKeyPair.private)

      assertNotNull(eccKeyPair.publicKey, "公钥不应该为空")
      assertNotNull(eccKeyPair.privateKey, "私钥不应该为空")
      assertEquals(EncryptAlgorithmTyping.ECC, eccKeyPair.algorithm, "算法应该是 ECC")

      // 验证密钥的算法
      assertEquals("EC", eccKeyPair.publicKey.algorithm, "公钥算法应该是 EC")
      assertEquals("EC", eccKeyPair.privateKey.algorithm, "私钥算法应该是 EC")

      log.info("真实 EC 密钥对测试通过")
      log.info("公钥算法: {}", eccKeyPair.publicKey.algorithm)
      log.info("私钥算法: {}", eccKeyPair.privateKey.algorithm)
      log.info("公钥格式: {}", eccKeyPair.publicKey.format)
      log.info("私钥格式: {}", eccKeyPair.privateKey.format)
    } catch (e: Exception) {
      log.info("EC 算法不可用，跳过真实密钥测试: {}", e.message)
    }
  }

  @Test
  fun `测试 EccKeyPair 的属性访问`() {
    log.info("测试 EccKeyPair 的属性访问")

    val testPublicKey = TestPublicKey()
    val testPrivateKey = TestPrivateKey()

    val eccKeyPair = EccKeyPair(testPublicKey, testPrivateKey)

    // 测试属性是否为 val（只读）
    val publicKey = eccKeyPair.publicKey
    val privateKey = eccKeyPair.privateKey
    val algorithm = eccKeyPair.algorithm

    assertEquals(testPublicKey, publicKey, "公钥属性应该匹配")
    assertEquals(testPrivateKey, privateKey, "私钥属性应该匹配")
    assertEquals(EncryptAlgorithmTyping.ECC, algorithm, "算法属性应该匹配")

    log.info("属性访问测试通过")
  }

  @Test
  fun `测试 EccKeyPair 的类型信息`() {
    log.info("测试 EccKeyPair 的类型信息")

    val testPublicKey = TestPublicKey()
    val testPrivateKey = TestPrivateKey()

    val eccKeyPair = EccKeyPair(testPublicKey, testPrivateKey)
    val keyPairClass = eccKeyPair::class.java

    assertEquals("EccKeyPair", keyPairClass.simpleName, "类名应该正确")
    assertEquals("io.github.truenine.composeserver.domain.enc", keyPairClass.packageName, "包名应该正确")

    // 验证实现的接口
    val interfaces = keyPairClass.interfaces
    assertTrue(interfaces.contains(IEccKeyPair::class.java), "应该实现 IEccKeyPair 接口")

    log.info("类型信息验证通过")
  }

  @Test
  fun `测试 EccKeyPair 的默认算法值`() {
    log.info("测试 EccKeyPair 的默认算法值")

    val testPublicKey = TestPublicKey()
    val testPrivateKey = TestPrivateKey()

    // 不指定算法参数，使用默认值
    val eccKeyPair = EccKeyPair(testPublicKey, testPrivateKey)

    assertEquals(EncryptAlgorithmTyping.ECC, eccKeyPair.algorithm, "默认算法应该是 ECC")

    log.info("默认算法值测试通过")
  }
}
