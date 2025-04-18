package net.yan100.compose.security.crypto

import net.yan100.compose.consts.IRegexes
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EncryptAndDecryptTest {
  // 测试数据
  private val testData = mapOf(
    "empty" to "",
    "short" to "测试数据",
    "chinese" to "我是中文测试数据啊啊啊",
    "long" to "a".repeat(117), // RSA 1024位密钥最大加密长度约为117字节
    "special" to "!@#$%^&*()_+-=[]{}|;:'\",.<>?/~`"
  )

  @Test
  fun `测试 AES 密钥生成与序列化反序列化`() {
    // 生成密钥
    val aesKey = Keys.generateAesKey()
    assertNotNull(aesKey, "AES密钥生成失败")

    // 转换为Base64
    val base64Key = Keys.writeAesKeyToBase64(aesKey)
    assertNotNull(base64Key, "AES密钥Base64编码失败")

    // 从Base64还原
    val recoveredKey = Keys.readAesKeyByBase64(base64Key)
    assertNotNull(recoveredKey, "AES密钥Base64解码失败")

    // 验证密钥一致性
    assertContentEquals(
      aesKey.encoded,
      recoveredKey.encoded,
      "AES密钥序列化和反序列化后不一致"
    )
  }

  @Test
  fun `测试 AES 加解密功能`() {
    val aesKey = Keys.generateAesKey()!!

    testData.forEach { (type, data) ->
      // 加密
      val encrypted = Encryptors.encryptByAesKey(aesKey, data)
      assertNotNull(encrypted, "AES加密失败: $type")
      assertNotEquals(data, encrypted, "AES加密后数据未变化: $type")

      // 解密
      val decrypted = Encryptors.decryptByAesKey(aesKey, encrypted)
      assertEquals(data, decrypted, "AES解密后数据不一致: $type")
    }
  }

  @Test
  fun `测试 RSA 密钥对生成与序列化反序列化`() {
    // 生成密钥对
    val keyPair = Keys.generateRsaKeyPair()
    assertNotNull(keyPair, "RSA密钥对生成失败")

    // 转换为Base64
    val publicKeyBase64 = keyPair.publicKeyBase64
    val privateKeyBase64 = keyPair.privateKeyBase64

    // 从Base64还原
    val recoveredPair = Keys.readRsaKeyPair(publicKeyBase64, privateKeyBase64)

    // 验证公钥一致性
    assertContentEquals(
      keyPair.publicKeyBase64ByteArray,
      recoveredPair.publicKeyBase64ByteArray,
      "RSA公钥序列化和反序列化后不一致"
    )

    // 验证私钥一致性
    assertContentEquals(
      keyPair.privateKeyBase64ByteArray,
      recoveredPair.privateKeyBase64ByteArray,
      "RSA私钥序列化和反序列化后不一致"
    )
  }

  @Test
  fun `测试 RSA 加解密功能`() {
    val keyPair = Keys.generateRsaKeyPair()!!

    testData.forEach { (type, data) ->
      // 使用公钥加密
      val encryptedByPublic = Encryptors.encryptByRsaPublicKey(keyPair.publicKey, data)
      assertNotNull(encryptedByPublic, "RSA公钥加密失败: $type")

      // 使用私钥解密
      val decryptedByPrivate = Encryptors.decryptByRsaPrivateKey(keyPair.privateKey, encryptedByPublic)
      assertNotNull(decryptedByPrivate, "RSA私钥解密失败: $type")
      assertEquals(data, decryptedByPrivate, "RSA公钥加密私钥解密后数据不一致: $type")

      // 使用私钥加密
      val encryptedByPrivate = Encryptors.encryptByRsaPrivateKey(keyPair.privateKey, data)
      assertNotNull(encryptedByPrivate, "RSA私钥加密失败: $type")

      // 使用公钥解密
      val decryptedByPublic = Encryptors.decryptByRsaPublicKey(keyPair.publicKey, encryptedByPrivate)
      assertNotNull(decryptedByPublic, "RSA公钥解密失败: $type")
      assertEquals(data, decryptedByPublic, "RSA私钥加密公钥解密后数据不一致: $type")
    }
  }

  @Test
  fun `测试 ECC 密钥对生成与序列化反序列化`() {
    // 生成密钥对
    val keyPair = Keys.generateEccKeyPair()
    assertNotNull(keyPair, "ECC密钥对生成失败")

    // 从Base64还原公钥和私钥
    val recoveredPublicKey = Keys.readEccPublicKeyByBase64(keyPair.publicKeyBase64)
    val recoveredPrivateKey = Keys.readEccPrivateKeyByBase64(keyPair.privateKeyBase64)

    assertNotNull(recoveredPublicKey, "ECC公钥反序列化失败")
    assertNotNull(recoveredPrivateKey, "ECC私钥反序列化失败")

    // 使用恢复的密钥对重新构建密钥对
    val recoveredPair = Keys.readEccKeyPair(keyPair.publicKeyBase64, keyPair.privateKeyBase64)
    assertContentEquals(
      keyPair.publicKeyBase64ByteArray,
      recoveredPair.publicKeyBase64ByteArray,
      "ECC公钥序列化和反序列化后不一致"
    )
    assertContentEquals(
      keyPair.privateKeyBase64ByteArray,
      recoveredPair.privateKeyBase64ByteArray,
      "ECC私钥序列化和反序列化后不一致"
    )
  }

  @Test
  fun `测试 ECC 加解密功能`() {
    val keyPair = Keys.generateEccKeyPair()!!

    testData.forEach { (type, data) ->
      // 使用公钥加密
      val encrypted = Encryptors.encryptByEccPublicKey(keyPair.publicKey, data)
      assertNotNull(encrypted, "ECC加密失败: $type")
      assertNotEquals(data, encrypted, "ECC加密后数据未变化: $type")

      // 使用私钥解密
      val decrypted = Encryptors.decryptByEccPrivateKey(keyPair.privateKey, encrypted)
      assertNotNull(decrypted, "ECC解密失败: $type")
      assertEquals(data, decrypted, "ECC解密后数据不一致: $type")
    }
  }

  @Test
  fun `测试 SHA1 和 SHA256 签名功能`() {
    testData.forEach { (type, data) ->
      // SHA1 测试
      val sha1 = Encryptors.signatureBySha1(data)
      assertNotEquals(data, sha1, "SHA1签名与原文相同: $type")
      assertTrue(IRegexes.SH1.toRegex().matches(sha1), "SHA1签名格式不正确: $type")

      // SHA256 测试
      val sha256 = Encryptors.signatureBySha256(data)
      assertNotEquals(data, sha256, "SHA256签名与原文相同: $type")
      assertTrue(sha256.length == 64, "SHA256签名长度不正确: $type")
    }
  }

  @Test
  fun `测试 PEM 格式密钥转换`() {
    // RSA密钥对PEM测试
    val rsaKeyPair = Keys.generateRsaKeyPair()!!
    val rsaPublicPem = Keys.writeKeyToPem(rsaKeyPair.publicKey, "RSA PUBLIC KEY")
    val rsaPrivatePem = Keys.writeKeyToPem(rsaKeyPair.privateKey, "RSA PRIVATE KEY")

    assertNotNull(rsaPublicPem, "RSA公钥PEM转换失败")
    assertNotNull(rsaPrivatePem, "RSA私钥PEM转换失败")
    assertTrue(rsaPublicPem.contains("BEGIN RSA PUBLIC KEY"), "RSA公钥PEM格式不正确")
    assertTrue(rsaPrivatePem.contains("BEGIN RSA PRIVATE KEY"), "RSA私钥PEM格式不正确")

    // ECC密钥对PEM测试
    val eccKeyPair = Keys.generateEccKeyPair()!!
    val eccPublicPem = Keys.writeKeyToPem(eccKeyPair.publicKey, "EC PUBLIC KEY")
    val eccPrivatePem = Keys.writeKeyToPem(eccKeyPair.privateKey, "EC PRIVATE KEY")

    assertNotNull(eccPublicPem, "ECC公钥PEM转换失败")
    assertNotNull(eccPrivatePem, "ECC私钥PEM转换失败")
    assertTrue(eccPublicPem.contains("BEGIN EC PUBLIC KEY"), "ECC公钥PEM格式不正确")
    assertTrue(eccPrivatePem.contains("BEGIN EC PRIVATE KEY"), "ECC私钥PEM格式不正确")
  }

  @Test
  fun `测试随机字符串生成`() {
    val lengths = listOf(8, 16, 32, 64, 128)

    lengths.forEach { length ->
      val random = Keys.generateRandomAsciiString(length)
      assertEquals(length, random.length, "生成的随机字符串长度不正确")
      assertTrue(
        random.all { it in '0' .. '9' || it in 'a' .. 'z' || it in 'A' .. 'Z' },
        "随机字符串包含非法字符"
      )
    }

    // 测试默认长度
    val defaultRandom = Keys.generateRandomAsciiString()
    assertEquals(32, defaultRandom.length, "默认随机字符串长度不是32")
  }
}
