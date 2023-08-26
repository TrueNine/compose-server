package net.yan100.compose.core.encrypt

import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.core.typing.EncryptAlgorithmTyping
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider
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
  private const val CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
  private const val RSA_KEY_SIZE = 1024
  private const val AES_KEY_SIZE = 256
  private val SECURE_RANDOM = SecureRandom()

  @JvmStatic
  private val rsaAlg = EncryptAlgorithmTyping.RSA.getValue()!!
  private const val DEFAULT_SEED = "T-DECRYPT-AND-ENCRYPT"

  @JvmStatic
  private val log = slf4j(Keys::class)


  /**
   * ## 生成随机字符串
   *
   * 范围包含
   *
   * - 0-9
   * - a-z
   * - A-Z
   */
  @JvmStatic
  fun generateRandomAsciiString(length: Int = 32): String {
    val sb = StringBuilder(length)
    for (i in 0 until length) {
      sb.append(CHARACTERS[SECURE_RANDOM.nextInt(CHARACTERS.length)])
    }
    return sb.toString()
  }

  /**
   * @param base64 rsa 公钥 base64 字符串
   * @return rsa公钥
   */
  @JvmStatic
  fun readRsaPublicKeyByBase64(base64: String): RSAPublicKey? =
    readPublicKeyByBase64AndAlg(base64, EncryptAlgorithmTyping.RSA) as? RSAPublicKey


  /**
   * @param base64 base64密钥
   * @return aes密钥
   */
  @JvmStatic
  fun readAesKeyByBase64(base64: String): SecretKeySpec? = runCatching {
    SecretKeySpec(Base64Helper.decodeToByte(base64), "AES")
  }.onFailure { log.error(::readAesKeyByBase64.name, it) }.getOrNull()

  /**
   * @param key 密钥
   * @return base64
   */
  @JvmStatic
  fun writeKeyToBase64(key: Key): String? = runCatching {
    Base64Helper.encode(key.encoded)
  }.onFailure { log.error(::writeKeyToBase64.name, it) }.getOrNull()

  /**
   * @param secret 密钥
   * @return base64密钥
   */
  @JvmStatic
  fun writeAesKeyToBase64(secret: SecretKeySpec): String? {
    return writeKeyToBase64(secret)
  }


  /**
   * @param base64 ecc base64 公钥
   * @return ecc公钥
   */
  @JvmStatic
  fun readEccPublicKeyByBase64(base64: String): PublicKey? =
    readPublicKeyByBase64AndAlg(base64, EncryptAlgorithmTyping.ECC)


  /**
   * @param privateKeyBase64 rsa base64 私钥
   * @return rsa私钥
   */
  @JvmStatic
  fun readRsaPrivateKeyByBase64(privateKeyBase64: String): RSAPrivateKey? =
    readPrivateKeyByBase64AndAlg(
      privateKeyBase64,
      EncryptAlgorithmTyping.RSA
    ) as? RSAPrivateKey

  /**
   * ## 此方法为解密标准 RSA 私钥
   * 标准的私钥即是：
   *
   * - 以 -----BEGIN PRIVATE KEY----- 开头
   * - 以 -----END PRIVATE KEY----- 结尾
   * - 中间包含换行的 base64 字符串
   * @param standardKeyBase64 rsa base64 私钥
   * @return rsa私钥
   */
  @JvmStatic
  fun readRsaPrivateKeyByBase64AndStandard(
    standardKeyBase64: String
  ): RSAPrivateKey? = readRsaPrivateKeyByBase64(
    standardKeyBase64.replace("-----BEGIN PRIVATE KEY-----", "")
      .replace("-----END PRIVATE KEY-----", "")
      .replace(Regex("\\s+"), "")
  )


  /**
   * @param base64 ecc base64 私钥
   * @return ecc私钥
   */
  @JvmStatic
  fun readEccPrivateKeyByBase64(base64: String): PrivateKey? {
    return readPrivateKeyByBase64AndAlg(base64, EncryptAlgorithmTyping.ECC)
  }

  /**
   * @param base64 base64 公钥
   * @param alg 公钥使用的算法
   * @return 公钥
   */
  @JvmStatic
  private fun readPublicKeyByBase64AndAlg(
    base64: String,
    alg: EncryptAlgorithmTyping
  ): PublicKey? {
    return runCatching {
      X509EncodedKeySpec(
        Base64Helper.decodeToByte(base64)
      ).run {
        KeyFactory.getInstance(alg.getValue()).generatePublic(this)
      }
    }.onFailure { log.error(::readPublicKeyByBase64AndAlg.name, it) }.getOrNull()
  }

  /**
   * @param base64 base64 私钥
   * @param alg 私钥使用的算法
   * @return 私钥
   */
  @JvmStatic
  private fun readPrivateKeyByBase64AndAlg(
    base64: String,
    alg: EncryptAlgorithmTyping
  ): PrivateKey? {
    return runCatching {
      PKCS8EncodedKeySpec(Base64Helper.decodeToByte(base64)).run {
        KeyFactory.getInstance(alg.getValue()).generatePrivate(this)
      }
    }.onFailure {
      log.error(::readPrivateKeyByBase64AndAlg.name, it)
    }.getOrNull()
  }

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
  ): KeyPair? {
    return runCatching {
      KeyPairGenerator.getInstance(algName, provider).run {
        initialize(
          keySize,
          SecureRandom(seed.toByteArray(StandardCharsets.UTF_8))
        )
        generateKeyPair()
      }
    }.onFailure { log.error(::generateKeyPair.name, it) }.getOrNull()
  }

  /**
   * @param seed 种子
   * @param keySize 密钥长度
   * @return rsa 密钥对
   */
  @JvmStatic
  fun generateRsaKeyPair(
    seed: String = DEFAULT_SEED,
    keySize: Int = RSA_KEY_SIZE
  ): RsaKeyPair? {
    return generateKeyPair(
      seed,
      keySize,
      EncryptAlgorithmTyping.RSA.getValue()!!,
      "SunRsaSign"
    )?.let { that ->
      RsaKeyPair().takeIf {
        that.private != null && that.public != null
      }?.apply {
        rsaPublicKey = that.public as? RSAPublicKey
        rsaPrivateKey = that.private as? RSAPrivateKey
      }
    }
  }

  /**
   * @param seed 种子
   * @return ecc密钥对
   */
  @JvmStatic
  fun generateEccKeyPair(seed: String = DEFAULT_SEED): EccKeyPair? {
    return runCatching {
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
  }

  /**
   * @param seed 种子
   * @param keySize aesKeySize
   * @return aesKey
   */
  @JvmStatic
  fun generateAesKey(
    seed: String = DEFAULT_SEED,
    keySize: Int = AES_KEY_SIZE
  ): SecretKeySpec? {
    return runCatching {
      val secureRandom = SecureRandom(seed.toByteArray(StandardCharsets.UTF_8))
      KeyGenerator.getInstance("AES").run {
        init(keySize, secureRandom)
        SecretKeySpec(generateKey().encoded, "AES")
      }
    }.onFailure { log.error(::generateAesKey.name, it) }.getOrNull()
  }

  /**
   * ## 获取一个 base64 的 aesKey
   * @param seed [Keys.DEFAULT_SEED]
   * @param keySize [Keys.AES_KEY_SIZE]
   */
  @JvmStatic
  fun generateAesKeyToBase64(
    seed: String = DEFAULT_SEED,
    keySize: Int = AES_KEY_SIZE
  ): String? {
    return generateAesKey(seed, keySize)?.let {
      Base64Helper.encode(it.encoded)
    }
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
  ): EccKeyPair? {
    return EccKeyPair()
      .apply {
        eccPublicKey = readEccPublicKeyByBase64(eccPublicKeyBase64)
        eccPrivateKey = readEccPrivateKeyByBase64(eccPrivateKeyBase64)
      }.takeIf { null != it.eccPublicKey && null != it.eccPrivateKey }
  }


  /**
   * @param rsaPublicKeyBase64 公钥
   * @param rsaPrivateKeyBase64 私钥
   * @return rsa密钥对
   */
  @JvmStatic
  fun readRsaKeyPair(
    rsaPublicKeyBase64: String,
    rsaPrivateKeyBase64: String,
  ): RsaKeyPair? {
    return RsaKeyPair()
      .apply {
        rsaPublicKey = readRsaPublicKeyByBase64(rsaPublicKeyBase64)
        rsaPrivateKey = readRsaPrivateKeyByBase64(rsaPrivateKeyBase64)
      }.takeIf { null != it.rsaPublicKey && null != it.rsaPrivateKeyBase64 }
  }

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
    alg: EncryptAlgorithmTyping
  ): KeyPair? {
    return KeyPair(
      readPublicKeyByBase64AndAlg(publicKeyBase64, alg),
      readPrivateKeyByBase64AndAlg(privateKeyBase64, alg)
    ).takeIf { null != it.public && null != it.private }
  }

  /**
   * 设置生成 ecc 的安全管理器
   */
  init {
    Security.addProvider(BouncyCastleProvider())
  }
}
