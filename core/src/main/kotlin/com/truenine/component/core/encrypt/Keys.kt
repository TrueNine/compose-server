package com.truenine.component.core.encrypt

import com.truenine.component.core.lang.LogKt
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.slf4j.Logger
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
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

  @JvmStatic
  private val rsaAlg = EncryptAlgorithm.RSA.str()

  @JvmStatic
  private val h: Base64Helper =
    SimpleUtf8Base64()
  private const val DEFAULT_SEED = "T-DECRYPT-AND-ENCRYPT"

  @JvmStatic
  private val log: Logger = LogKt.getLog(Keys::class)

  /**
   * @param base64 rsa 公钥 base64 字符串
   * @return rsa公钥
   */
  @JvmStatic
  fun readRsaPublicKeyByBase64(base64: String): RSAPublicKey? =
    readPublicKeyByBase64AndAlg(base64, EncryptAlgorithm.RSA) as? RSAPublicKey


  /**
   * @param base64 base64密钥
   * @return aes密钥
   */
  @JvmStatic
  fun readAesKeyByBase64(base64: String): SecretKeySpec? = runCatching {
    SecretKeySpec(h.decodeToByte(base64), "AES")
  }.onFailure { log.error(::readAesKeyByBase64.name, it) }.getOrNull()

  /**
   * @param key 密钥
   * @return base64
   */
  @JvmStatic
  fun writeKeyToBase64(key: Key): String? = runCatching {
    h.encode(key.encoded)
  }.onFailure { log.error(::writeKeyToBase64.name, it) }.getOrNull()

  /**
   * @param secret 密钥
   * @return base64密钥
   */
  @JvmStatic
  fun writeAesKeyToBase64(secret: SecretKeySpec): String? =
    writeKeyToBase64(secret)


  /**
   * @param base64 ecc base64 公钥
   * @return ecc公钥
   */
  @JvmStatic
  fun readEccPublicKeyByBase64(base64: String): PublicKey? =
    readPublicKeyByBase64AndAlg(base64, EncryptAlgorithm.ECC)


  /**
   * @param privateKeyBase64 rsa base64 私钥
   * @return rsa私钥
   */
  @JvmStatic
  fun readRsaPrivateKeyByBase64(privateKeyBase64: String): RSAPrivateKey? =
    readPrivateKeyByBase64AndAlg(
      privateKeyBase64,
      EncryptAlgorithm.RSA
    ) as? RSAPrivateKey


  /**
   * @param base64 ecc base64 私钥
   * @return ecc私钥
   */
  @JvmStatic
  fun readEccPrivateKeyByBase64(base64: String): PrivateKey? =
    readPrivateKeyByBase64AndAlg(base64, EncryptAlgorithm.ECC)


  /**
   * @param base64 base64 公钥
   * @param alg 公钥使用的算法
   * @return 公钥
   */
  @JvmStatic
  private fun readPublicKeyByBase64AndAlg(
    base64: String,
    alg: EncryptAlgorithm
  ): PublicKey? = runCatching {
    X509EncodedKeySpec(
      h.decodeToByte(base64)
    ).run {
      KeyFactory.getInstance(alg.str()).generatePublic(this)
    }
  }.onFailure { log.error(::readPublicKeyByBase64AndAlg.name, it) }.getOrNull()


  /**
   * @param base64 base64 私钥
   * @param alg 私钥使用的算法
   * @return 私钥
   */
  @JvmStatic
  private fun readPrivateKeyByBase64AndAlg(
    base64: String,
    alg: EncryptAlgorithm
  ): PrivateKey? = runCatching {
    PKCS8EncodedKeySpec(h.decodeToByte(base64)).run {
      KeyFactory.getInstance(alg.str()).generatePrivate(this)
    }
  }.onFailure {
    log.error(::readPrivateKeyByBase64AndAlg.name, it)
  }.getOrNull()

  /**
   * @param seed 种子
   * @param keySize key 长度
   * @param algName 算法名称
   * @param provider 指定生成器
   * @return 密钥对
   */
  @JvmStatic
  fun generateKeyPair(
    seed: String = DEFAULT_SEED,
    keySize: Int = RSA_KEY_SIZE,
    algName: String = rsaAlg,
    provider: String? = "SunJCE"
  ): KeyPair? = runCatching {
    KeyPairGenerator.getInstance(algName, provider).run {
      initialize(
        keySize,
        SecureRandom(seed.toByteArray(StandardCharsets.UTF_8))
      )
      generateKeyPair()
    }
  }.onFailure { log.error(::generateKeyPair.name, it) }.getOrNull()

  /**
   * @param seed 种子
   * @param keySize 密钥长度
   * @return rsa 密钥对
   */
  @JvmStatic
  fun generateRsaKeyPair(
    seed: String = DEFAULT_SEED,
    keySize: Int = RSA_KEY_SIZE
  ): RsaKeyPair? = generateKeyPair(
    seed,
    keySize,
    EncryptAlgorithm.RSA.str(),
    "SunRsaSign"
  )?.let { that ->
    RsaKeyPair().takeIf {
      that.private != null && that.public != null
    }?.apply {
      rsaPublicKey = that.public as? RSAPublicKey
      rsaPrivateKey = that.private as? RSAPrivateKey
    }
  }

  /**
   * @param seed 种子
   * @return ecc密钥对
   */
  @JvmStatic
  fun generateEccKeyPair(seed: String = DEFAULT_SEED): EccKeyPair? =
    runCatching {
      val random = SecureRandom(seed.toByteArray(StandardCharsets.UTF_8))
      val curve = ECNamedCurveTable.getParameterSpec("P-256")
      KeyPairGenerator.getInstance("EC", "BC").run {
        initialize(curve, random)
        val keyPair = generateKeyPair()
        EccKeyPair().apply {
          eccPrivateKey = keyPair.private
          eccPublicKey = keyPair.public
        }
      }
    }.onFailure { log.error(::generateEccKeyPair.name, it) }.getOrNull()

  /**
   * @param seed 种子
   * @param keySize aesKeySize
   * @return aesKey
   */
  fun generateAesKey(
    seed: String = DEFAULT_SEED,
    keySize: Int = AES_KEY_SIZE
  ): SecretKeySpec? = runCatching {
    val secureRandom = SecureRandom(seed.toByteArray(StandardCharsets.UTF_8))
    KeyGenerator.getInstance("AES").run {
      init(keySize, secureRandom)
      SecretKeySpec(generateKey().encoded, "AES")
    }
  }.onFailure { log.error(::generateAesKey.name, it) }.getOrNull()

  /**
   * ## 获取一个 base64 的 aesKey
   * @param seed [Keys.DEFAULT_SEED]
   * @param keySize [Keys.AES_KEY_SIZE]
   */
  fun generateAesKeyToBase64(
    seed: String = DEFAULT_SEED,
    keySize: Int = AES_KEY_SIZE
  ) = generateAesKey(seed, keySize)?.let {
    h.encode(it.encoded)
  }

  /**
   * @param eccPublicKeyBase64 公钥
   * @param eccPrivateKeyBase64 私钥
   * @return eccKeyPair
   */
  @JvmStatic
  fun readEccKeyPair(
    eccPublicKeyBase64: String,
    eccPrivateKeyBase64: String,
  ): EccKeyPair? = EccKeyPair()
    .apply {
      eccPublicKey = readEccPublicKeyByBase64(eccPublicKeyBase64)
      eccPrivateKey = readEccPrivateKeyByBase64(eccPrivateKeyBase64)
    }.takeIf { null != it.eccPublicKey && null != it.eccPrivateKey }


  /**
   * @param rsaPublicKeyBase64 公钥
   * @param rsaPrivateKeyBase64 私钥
   * @return rsa密钥对
   */
  @JvmStatic
  fun readRsaKeyPair(
    rsaPublicKeyBase64: String,
    rsaPrivateKeyBase64: String,
  ): RsaKeyPair? = RsaKeyPair()
    .apply {
      rsaPublicKey = readRsaPublicKeyByBase64(rsaPublicKeyBase64)
      rsaPrivateKey = readRsaPrivateKeyByBase64(rsaPrivateKeyBase64)
    }.takeIf { null != it.rsaPublicKey && null != it.rsaPrivateKeyBase64 }

  /**
   * @param publicKeyBase64 公钥
   * @param privateKeyBase64 私钥
   * @param alg key 使用算法
   * @return 密钥对
   */
  @JvmStatic
  fun readKeyPair(
    publicKeyBase64: String,
    privateKeyBase64: String,
    alg: EncryptAlgorithm
  ): KeyPair? = KeyPair(
    readPublicKeyByBase64AndAlg(publicKeyBase64, alg),
    readPrivateKeyByBase64AndAlg(privateKeyBase64, alg)
  ).takeIf { null != it.public && null != it.private }


  /**
   * 设置生成 ecc 的安全管理器
   */
  init {
    Security.addProvider(BouncyCastleProvider())
  }
}
