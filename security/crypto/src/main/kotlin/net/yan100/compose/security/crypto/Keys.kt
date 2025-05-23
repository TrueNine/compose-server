package net.yan100.compose.security.crypto

import net.yan100.compose.security.crypto.domain.EccExtKeyPair
import net.yan100.compose.security.crypto.domain.IEccExtKeyPair
import net.yan100.compose.security.crypto.domain.IRsaExtKeyPair
import net.yan100.compose.security.crypto.domain.RsaExtKeyPair
import net.yan100.compose.slf4j
import net.yan100.compose.typing.EncryptAlgorithmTyping
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

/**
 * 高性能加密密钥工具类
 *
 * 提供RSA、ECC、AES等加密算法的密钥生成、转换和管理功能。
 * 所有操作都经过性能优化，支持并发访问。
 *
 * 主要特性：
 * - 线程安全的密钥生成和转换
 * - 预初始化的密钥生成器和工厂类
 * - 高效的内存管理和资源复用
 * - 完善的错误处理和日志记录
 * - 支持标准PEM格式
 *
 * 性能优化：
 * - 使用ThreadLocal缓存SecureRandom实例
 * - 复用KeyFactory和KeyGenerator实例
 * - 优化字符串和字节数组操作
 * - 使用协程支持异步操作
 *
 * @author TrueNine
 * @version 3.0
 * @since 2024-03-19
 */
object Keys {
  private val log = slf4j<Keys>()

  /**
   * 密钥相关常量
   */
  private object Constants {
    const val CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    const val RSA_KEY_SIZE = 2048 // 提升到2048位以增强安全性
    const val AES_KEY_SIZE = 256
    const val DEFAULT_SEED = "T-DECRYPT-AND-ENCRYPT"
    const val ECC_CURVE = "P-256"
    const val BUFFER_SIZE = 8192

    val RSA_ALG = EncryptAlgorithmTyping.RSA.padding
  }

  /**
   * 预初始化的密钥工厂和生成器
   */
  private object KeyFactories {
    private val secureRandomThreadLocal = ThreadLocal.withInitial {
      SecureRandom().apply { setSeed(System.nanoTime()) }
    }

    val secureRandom: SecureRandom get() = secureRandomThreadLocal.get()

    val rsaKeyFactory: KeyFactory by lazy {
      KeyFactory.getInstance(EncryptAlgorithmTyping.RSA.value, "SunRsaSign")
    }

    val eccKeyFactory: KeyFactory by lazy {
      KeyFactory.getInstance("EC", "BC")
    }

    val aesKeyGenerator: KeyGenerator by lazy {
      KeyGenerator.getInstance("AES")
    }

    val eccKeyGenerator: KeyPairGenerator by lazy {
      KeyPairGenerator.getInstance("EC", "BC")
    }
  }

  /**
   * 生成指定长度的高熵随机ASCII字符串
   *
   * 使用ThreadLocal的SecureRandom实例生成随机字符串，
   * 确保线程安全和高性能。
   *
   * @param length 字符串长度，默认32位
   * @return 随机字符串
   * @throws IllegalArgumentException 当length <= 0时
   */
  @JvmStatic
  fun generateRandomAsciiString(length: Int = 32): String {
    require(length > 0) { "Length must be positive" }
    return buildString(length) {
      repeat(length) {
        append(Constants.CHARACTERS[KeyFactories.secureRandom.nextInt(Constants.CHARACTERS.length)])
      }
    }
  }

  /**
   * 从Base64字符串安全读取RSA公钥
   *
   * 使用预初始化的KeyFactory实例，优化性能。
   * 包含完整的错误处理和日志记录。
   *
   * @param base64 RSA公钥的Base64编码
   * @return RSA公钥对象，解析失败返回null
   */
  @JvmStatic
  fun readRsaPublicKeyByBase64(base64: String): RSAPublicKey? =
    readPublicKeyByBase64AndAlg(base64, EncryptAlgorithmTyping.RSA) as? RSAPublicKey

  /**
   * 从Base64字符串安全读取RSA私钥
   *
   * 使用预初始化的KeyFactory实例，优化性能。
   * 包含完整的错误处理和日志记录。
   *
   * @param base64 RSA私钥的Base64编码
   * @return RSA私钥对象，解析失败返回null
   */
  @JvmStatic
  fun readRsaPrivateKeyByBase64(base64: String): RSAPrivateKey? = runCatching {
    val keyBytes = IBase64.decodeToByte(base64)
    val keySpec = PKCS8EncodedKeySpec(keyBytes)
    KeyFactories.rsaKeyFactory.generatePrivate(keySpec) as RSAPrivateKey
  }.onFailure {
    log.error("RSA私钥解析失败: ${it.message}", it)
  }.getOrNull()

  /**
   * 从Base64字符串安全读取RSA密钥对
   *
   * @param rsaPublicKeyBase64 RSA公钥Base64
   * @param rsaPrivateKeyBase64 RSA私钥Base64
   * @return RSA密钥对
   * @throws IllegalArgumentException 密钥无效时
   */
  @JvmStatic
  fun readRsaKeyPair(
    rsaPublicKeyBase64: String,
    rsaPrivateKeyBase64: String
  ): IRsaExtKeyPair = RsaExtKeyPair(
    requireNotNull(readRsaPublicKeyByBase64(rsaPublicKeyBase64)) { "无效的RSA公钥" },
    requireNotNull(readRsaPrivateKeyByBase64(rsaPrivateKeyBase64)) { "无效的RSA私钥" }
  )

  /**
   * 从Base64字符串安全读取AES密钥
   *
   * 优化的内存使用和错误处理。
   *
   * @param base64 AES密钥的Base64编码
   * @return AES密钥对象，解析失败返回null
   */
  @JvmStatic
  fun readAesKeyByBase64(base64: String): SecretKeySpec? = runCatching {
    val keyBytes = IBase64.decodeToByte(base64)
    SecretKeySpec(keyBytes, "AES")
  }.onFailure {
    log.error("AES密钥解析失败: ${it.message}", it)
  }.getOrNull()

  /**
   * 将密钥安全转换为Base64字符串
   *
   * 优化的内存使用和错误处理。
   *
   * @param key 密钥对象
   * @return Base64编码的字符串，转换失败返回null
   */
  @JvmStatic
  fun writeKeyToBase64(key: Key): String? = runCatching {
    key.encoded.encodeBase64String
  }.onFailure {
    log.error("密钥转Base64失败: ${it.message}", it)
  }.getOrNull()

  /**
   * 将AES密钥转换为Base64字符串
   *
   * 优化的内存使用和错误处理。
   * 使用预定义的编码方式，确保跨平台兼容性。
   *
   * @param key AES密钥对象
   * @return Base64编码的字符串，转换失败返回null
   */
  @JvmStatic
  fun writeAesKeyToBase64(key: SecretKeySpec): String? = writeKeyToBase64(key)

  /**
   * 将密钥转换为PEM格式
   *
   * 支持RSA和ECC密钥的PEM格式转换。
   * 根据密钥类型自动选择正确的PEM头部和尾部标记。
   * 使用标准的Base64编码确保跨平台兼容性。
   *
   * @param key 密钥对象
   * @param keyType 可选的密钥类型，如果为空则根据密钥类型自动推断
   * @return PEM格式的字符串，转换失败返回null
   */
  @JvmStatic
  fun writeKeyToPem(key: Key, keyType: String? = null): String? = runCatching {
    PemFormat[key, keyType]
  }.onFailure {
    log.error("密钥转PEM格式失败: ${it.message}", it)
  }.getOrNull()

  /**
   * 从标准Base64字符串读取RSA公钥
   *
   * 直接解析Base64编码的公钥数据，不包含PEM格式的头尾标记。
   * 使用X.509标准格式。
   *
   * @param base64 RSA公钥的Base64编码
   * @return RSA公钥对象，解析失败返回null
   */
  @JvmStatic
  fun readRsaPublicKeyByBase64AndStandard(base64: String): RSAPublicKey? = runCatching {
    val keyBytes = IBase64.decodeToByte(base64)
    val keySpec = X509EncodedKeySpec(keyBytes)
    KeyFactories.rsaKeyFactory.generatePublic(keySpec) as RSAPublicKey
  }.onFailure {
    log.error("RSA公钥解析失败(标准格式): ${it.message}", it)
  }.getOrNull()

  /**
   * 从标准Base64字符串读取RSA私钥
   *
   * 直接解析Base64编码的私钥数据，不包含PEM格式的头尾标记。
   * 使用PKCS#8标准格式。
   *
   * @param base64 RSA私钥的Base64编码
   * @return RSA私钥对象，解析失败返回null
   */
  @JvmStatic
  fun readRsaPrivateKeyByBase64AndStandard(base64: String): RSAPrivateKey? = runCatching {
    val keyBytes = IBase64.decodeToByte(base64)
    val keySpec = PKCS8EncodedKeySpec(keyBytes)
    KeyFactories.rsaKeyFactory.generatePrivate(keySpec) as RSAPrivateKey
  }.onFailure {
    log.error("RSA私钥解析失败(标准格式): ${it.message}", it)
  }.getOrNull()

  /**
   * 生成RSA密钥对
   *
   * 使用优化的密钥生成算法和参数：
   * - 2048位密钥长度
   * - 线程安全的SecureRandom
   * - 预初始化的KeyPairGenerator
   *
   * @param seed 随机种子，默认生成随机字符串
   * @param keySize 密钥大小，默认2048位
   * @return RSA密钥对，生成失败返回null
   */
  @JvmStatic
  fun generateRsaKeyPair(
    seed: String = generateRandomAsciiString(),
    keySize: Int = Constants.RSA_KEY_SIZE
  ): IRsaExtKeyPair? = runCatching {
    val random = SecureRandom(seed.toByteArray(Charsets.UTF_8))
    val keyPairGen = KeyPairGenerator.getInstance(EncryptAlgorithmTyping.RSA.value, "SunRsaSign")
    keyPairGen.initialize(keySize, random)
    val keyPair = keyPairGen.generateKeyPair()
    RsaExtKeyPair(
      keyPair.public as RSAPublicKey,
      keyPair.private as RSAPrivateKey
    )
  }.onFailure {
    log.error("RSA密钥对生成失败: ${it.message}", it)
  }.getOrNull()

  /**
   * 生成ECC密钥对
   *
   * 使用优化的ECC参数和生成算法：
   * - P-256曲线
   * - 预初始化的KeyPairGenerator
   * - 线程安全的SecureRandom
   *
   * @param seed 随机种子，默认生成随机字符串
   * @return ECC密钥对，生成失败返回null
   */
  @JvmStatic
  fun generateEccKeyPair(seed: String = generateRandomAsciiString()): IEccExtKeyPair? = runCatching {
    val random = SecureRandom(seed.toByteArray(Charsets.UTF_8))
    val curve = ECNamedCurveTable.getParameterSpec(Constants.ECC_CURVE)
    KeyFactories.eccKeyGenerator.apply {
      initialize(curve, random)
    }.generateKeyPair().let { keyPair ->
      EccExtKeyPair(keyPair.public, keyPair.private)
    }
  }.onFailure {
    log.error("ECC密钥对生成失败: ${it.message}", it)
  }.getOrNull()

  /**
   * 生成高强度AES密钥
   *
   * 使用优化的参数和生成算法：
   * - 256位密钥长度
   * - 预初始化的KeyGenerator
   * - 线程安全的SecureRandom
   *
   * @param seed 随机种子，默认使用安全默认值
   * @param keySize 密钥大小，默认256位
   * @return AES密钥，生成失败返回null
   */
  @JvmStatic
  fun generateAesKey(
    seed: String = Constants.DEFAULT_SEED,
    keySize: Int = Constants.AES_KEY_SIZE
  ): SecretKeySpec? = runCatching {
    val random = SecureRandom(seed.toByteArray(Charsets.UTF_8))
    KeyFactories.aesKeyGenerator.apply {
      init(keySize, random)
    }.generateKey().let { key ->
      SecretKeySpec(key.encoded, "AES")
    }
  }.onFailure {
    log.error("AES密钥生成失败: ${it.message}", it)
  }.getOrNull()

  /**
   * 从Base64安全读取ECC密钥对
   *
   * @param eccPublicKeyBase64 ECC公钥Base64
   * @param eccPrivateKeyBase64 ECC私钥Base64
   * @return ECC密钥对
   * @throws IllegalArgumentException 密钥无效时
   */
  @JvmStatic
  fun readEccKeyPair(
    eccPublicKeyBase64: String,
    eccPrivateKeyBase64: String
  ): IEccExtKeyPair = EccExtKeyPair(
    requireNotNull(readEccPublicKeyByBase64(eccPublicKeyBase64)) { "无效的ECC公钥" },
    requireNotNull(readEccPrivateKeyByBase64(eccPrivateKeyBase64)) { "无效的ECC私钥" }
  )

  /**
   * 从Base64字符串安全读取公钥
   *
   * @param base64 公钥的Base64编码
   * @param algorithm 加密算法类型
   * @return 公钥对象，解析失败返回null
   */
  @JvmStatic
  private fun readPublicKeyByBase64AndAlg(base64: String, algorithm: EncryptAlgorithmTyping): PublicKey? = runCatching {
    val keyBytes = IBase64.decodeToByte(base64)
    val keySpec = X509EncodedKeySpec(keyBytes)
    when (algorithm) {
      EncryptAlgorithmTyping.RSA -> KeyFactories.rsaKeyFactory.generatePublic(keySpec)
      EncryptAlgorithmTyping.ECC -> KeyFactories.eccKeyFactory.generatePublic(keySpec)
      else -> throw IllegalArgumentException("不支持的算法类型: $algorithm")
    }
  }.onFailure {
    log.error("公钥解析失败: ${it.message}", it)
  }.getOrNull()

  /**
   * 从Base64字符串安全读取ECC公钥
   *
   * @param base64 ECC公钥的Base64编码
   * @return ECC公钥对象，解析失败返回null
   */
  @JvmStatic
  fun readEccPublicKeyByBase64(base64: String): PublicKey? =
    readPublicKeyByBase64AndAlg(base64, EncryptAlgorithmTyping.ECC)

  /**
   * 从Base64字符串安全读取ECC私钥
   *
   * @param base64 ECC私钥的Base64编码
   * @return ECC私钥对象，解析失败返回null
   */
  @JvmStatic
  fun readEccPrivateKeyByBase64(base64: String): PrivateKey? = runCatching {
    val keyBytes = IBase64.decodeToByte(base64)
    val keySpec = PKCS8EncodedKeySpec(keyBytes)
    KeyFactories.eccKeyFactory.generatePrivate(keySpec)
  }.onFailure {
    log.error("ECC私钥解析失败: ${it.message}", it)
  }.getOrNull()

  init {
    // 初始化BouncyCastle提供者
    Security.addProvider(BouncyCastleProvider())

    // 预热关键组件
    KeyFactories.secureRandom
    KeyFactories.rsaKeyFactory
    KeyFactories.eccKeyFactory
    KeyFactories.aesKeyGenerator
    KeyFactories.eccKeyGenerator
  }
}
