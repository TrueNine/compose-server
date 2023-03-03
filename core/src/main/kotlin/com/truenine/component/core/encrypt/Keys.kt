package com.truenine.component.core.encrypt

import com.truenine.component.core.consts.Algorithm
import com.truenine.component.core.encrypt.base64.Base64Helper
import com.truenine.component.core.encrypt.base64.SimpleUtf8Base64
import com.truenine.component.core.encrypt.consts.EccKeyPair
import com.truenine.component.core.encrypt.consts.RsaKeyPair
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

/**
 * 加解密密钥工具类
 *
 * @author TrueNine
 * @since 2023-02-19
 */
object Keys {
  private const val RSA_KEY_SIZE = 1024
  private const val AES_KEY_SIZE = 256
  private val rsaAlg = Algorithm.RSA.str()
  private val h: Base64Helper = SimpleUtf8Base64()
  private const val DEFAULT_SEED = "T-DECRYPT"

  /**
   * 将 rsa 供公钥 base64 版本转换为 java 类型的 rsa 公钥
   * @param base64 rsa 公钥 base64 字符串
   */
  @JvmStatic
  fun readRsaPublicKeyByBase64(base64: String): RSAPublicKey {
    return readPublicKeyByBase64AndAlg(base64, Algorithm.RSA) as RSAPublicKey
  }

  /**
   * 读取 aes 密钥
   *
   * @param base64 base64 密钥
   */
  @JvmStatic
  fun readAesKeyByBase64(base64: String): SecretKeySpec {
    return SecretKeySpec(h.decodeToByte(base64), "AES")
  }

  fun writeAesKeyByBase64(secret: SecretKeySpec): String {
    return h.encode(secret.encoded)
  }

  /**
   * 将 base64 ecc 公钥转换为 PublicKey
   *
   * @param base64 ecc base64 公钥
   */
  @JvmStatic
  fun readEccPublicKeyByBase64(base64: String): PublicKey {
    return readPublicKeyByBase64AndAlg(base64, Algorithm.ECC)
  }

  /**
   * 将 rsa base64 私钥 转换为 java 类型和的 RSAPrivateKey
   *
   * @param privateKeyBase64 rsa base64 私钥
   */
  @JvmStatic
  fun readRsaPrivateKeyByBase64(privateKeyBase64: String): RSAPrivateKey {
    return readPrivateKeyByBase64AndAlg(
      privateKeyBase64,
      Algorithm.RSA
    ) as RSAPrivateKey
  }

  /**
   * 将 ecc base64 私钥 转换为 java 类型的 PrivateKey
   *
   * @param base64 ecc base64 私钥
   */
  @JvmStatic
  fun readEccPrivateKeyByBase64(base64: String): PrivateKey {
    return readPrivateKeyByBase64AndAlg(base64, Algorithm.ECC)
  }

  /**
   * 将 一个 base64 公钥 根据提供的算法转换为 java 类型的 PublicKey
   * @param base64 base64 公钥
   * @param alg 公钥使用的算法
   */
  @JvmStatic
  private fun readPublicKeyByBase64AndAlg(
    base64: String,
    alg: Algorithm
  ): PublicKey {
    return try {
      val spec = X509EncodedKeySpec(h.decodeToByte(base64))
      KeyFactory.getInstance(alg.str()).generatePublic(spec)
    } catch (e: NoSuchAlgorithmException) {
      throw RuntimeException(e)
    } catch (e: InvalidKeySpecException) {
      throw RuntimeException(e)
    }
  }

  /**
   * 将 一个 base64 私钥 根据提供的算法转换为 java 类型的 PrivateKey
   * @param base64 base64 私钥
   * @param alg 私钥使用的算法
   */
  @JvmStatic
  private fun readPrivateKeyByBase64AndAlg(
    base64: String,
    alg: Algorithm
  ): PrivateKey {
    return try {
      val spec = PKCS8EncodedKeySpec(h.decodeToByte(base64))
      KeyFactory.getInstance(alg.str()).generatePrivate(spec)
    } catch (e: NoSuchAlgorithmException) {
      throw java.lang.RuntimeException(e)
    } catch (e: InvalidKeySpecException) {
      throw java.lang.RuntimeException(e)
    }
  }

  /**
   * 使用 KeyPairGenerator 生成一个密钥对
   *
   * @param seed 种子
   * @param keySize key 长度
   * @param algName 算法名称
   */
  @JvmStatic
  fun generateKeyPair(
    seed: String = DEFAULT_SEED,
    keySize: Int = RSA_KEY_SIZE,
    algName: String = rsaAlg
  ): KeyPair {
    val gen: KeyPairGenerator = try {
      KeyPairGenerator.getInstance(algName)
    } catch (e: NoSuchAlgorithmException) {
      throw RuntimeException(e)
    }
    val secureRandom = SecureRandom(seed.toByteArray(StandardCharsets.UTF_8))
    gen.initialize(keySize, secureRandom)
    return gen.generateKeyPair()
  }

  /**
   * 生成一对 rsa 密钥对
   *
   * @param seed 种子
   * @param keySize 密钥长度
   */
  @JvmStatic
  fun generateRsaKeyPair(
    seed: String = DEFAULT_SEED,
    keySize: Int = RSA_KEY_SIZE
  ): RsaKeyPair {
    val keyPair = generateKeyPair(seed, keySize)
    return RsaKeyPair()
      .setRsaPublicKey(keyPair.public as RSAPublicKey)
      .setRsaPrivateKey(keyPair.private as RSAPrivateKey)
  }

  /**
   * 生成一对 ecc 密钥对
   *
   * @return ecc密钥对
   */
  @JvmStatic
  fun generateEccKeyPair(): EccKeyPair? {
    return try {
      val curve = ECNamedCurveTable.getParameterSpec("P-256")
      val generator = KeyPairGenerator.getInstance("EC", "BC")
      generator.initialize(curve)
      val keyPair = generator.generateKeyPair()
      val publicKey = keyPair.public
      val privateKey = keyPair.private
      val ecc = EccKeyPair()
      ecc.eccPrivateKey = privateKey
      ecc.eccPublicKey = publicKey
      ecc
    } catch (e: Exception) {
      null
    }
  }

  fun generateAesKey(keySize: Int = AES_KEY_SIZE): SecretKeySpec {
    val keyGenerator = KeyGenerator.getInstance("AES")
    keyGenerator.init(keySize)
    val secretKey = keyGenerator.generateKey()
    return SecretKeySpec(secretKey.encoded, "AES")
  }

  @JvmStatic
  fun readEccKeyPair(
    eccPublicKeyBase64: String,
    eccPrivateKeyBase64: String,
  ): EccKeyPair {
    val rp = EccKeyPair()
    val b = readEccPublicKeyByBase64(eccPublicKeyBase64)
    val v = readEccPrivateKeyByBase64(eccPrivateKeyBase64)
    rp.eccPublicKey = b
    rp.eccPrivateKey = v
    return rp
  }

  @JvmStatic
  fun readRsaKeyPair(
    rsaPublicKeyBase64: String,
    rsaPrivateKeyBase64: String,
  ): RsaKeyPair {
    val rp = RsaKeyPair()
    val b = readRsaPublicKeyByBase64(rsaPublicKeyBase64)
    val v = readRsaPrivateKeyByBase64(rsaPrivateKeyBase64)
    rp.rsaPublicKey = b
    rp.rsaPrivateKey = v
    return rp
  }

  @JvmStatic
  fun readKeyPair(
    publicKeyBase64: String,
    privateKeyBase64: String,
    alg: Algorithm
  ): KeyPair {
    val b = readPublicKeyByBase64AndAlg(publicKeyBase64, alg)
    val v = readPrivateKeyByBase64AndAlg(privateKeyBase64, alg)
    return KeyPair(b, v)
  }

  /**
   * 设置生成 ecc 的安全管理器
   */
  init {
    Security.addProvider(BouncyCastleProvider())
  }
}
