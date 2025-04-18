package net.yan100.compose.security.crypto

import net.yan100.compose.slf4j
import net.yan100.compose.typing.EncryptAlgorithmTyping
import org.bouncycastle.jce.spec.IESParameterSpec
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * 加密解密工具类
 * 提供RSA、ECC、AES等多种加密算法的实现，支持加密、解密、签名等操作
 *
 * @author TrueNine
 * @since 2023-02-28
 */
object Encryptors {
  /**
   * 使用公钥加密数据
   *
   * @param publicKey 加密用的公钥
   * @param data 待加密的原文数据
   * @param shardingSize 分片大小，用于处理大数据量
   * @param charset 字符编码，默认UTF-8
   * @param alg 加密算法类型
   * @return 加密后的Base64编码字符串，失败返回null
   */
  @JvmStatic
  private fun encrypt(
    publicKey: PublicKey,
    data: String,
    shardingSize: Int = SHARDING_SIZE,
    charset: Charset = defaultCharset,
    alg: EncryptAlgorithmTyping = EncryptAlgorithmTyping.RSA,
  ): String? =
    runCatching {
      Cipher.getInstance(alg.padding).run {
        init(ENC_MODE, publicKey)
        sharding(data.toByteArray(charset), shardingSize)
          .joinToString(SHARDING_SEP) { IBase64.encode(doFinal(it)) }
      }
    }
      .onFailure { log.error("${Encryptors::encrypt.name} failed", it) }
      .getOrNull()

  /**
   * 使用私钥加密数据
   *
   * @param privateKey 加密用的私钥
   * @param data 待加密的原文数据
   * @param shardingSize 分片大小，用于处理大数据量
   * @param charset 字符编码，默认UTF-8
   * @param alg 加密算法类型
   * @return 加密后的Base64编码字符串，失败返回null
   */
  @JvmStatic
  private fun encryptByPrivateKey(
    privateKey: PrivateKey,
    data: String,
    shardingSize: Int = SHARDING_SIZE,
    charset: Charset = defaultCharset,
    alg: EncryptAlgorithmTyping = EncryptAlgorithmTyping.RSA,
  ): String? =
    runCatching {
      Cipher.getInstance(alg.padding).run {
        init(ENC_MODE, privateKey)
        sharding(data.toByteArray(charset), shardingSize)
          .joinToString(SHARDING_SEP) { IBase64.encode(doFinal(it)) }
      }
    }
      .onFailure { log.error("${Encryptors::encryptByPrivateKey.name} failed", it) }
      .getOrNull()

  /**
   * 使用私钥解密数据
   *
   * @param privateKey 解密用的私钥
   * @param data Base64编码的加密数据
   * @param alg 解密算法类型
   * @param charset 字符编码，默认UTF-8
   * @return 解密后的原文，失败返回null
   */
  @JvmStatic
  private fun basicDecrypt(
    privateKey: PrivateKey,
    data: String,
    alg: EncryptAlgorithmTyping = EncryptAlgorithmTyping.RSA,
    charset: Charset = defaultCharset,
  ): String? =
    runCatching {
      if (data.isEmpty()) return ""
      Cipher.getInstance(alg.padding).run {
        init(DEC_MODE, privateKey)
        data
          .split(SHARDING_SEP)
          .map { IBase64.decodeToByte(it) }
          .map { doFinal(it) }
          .reduce { acc, byt -> acc + byt }
          .let { String(it, charset) }
      }
    }
      .onFailure { log.error("${Encryptors::basicDecrypt.name} failed", it) }
      .getOrNull()

  /**
   * 使用Base64编码的RSA公钥加密数据
   *
   * @param rsaPublicKey Base64编码的RSA公钥
   * @param data 待加密的原文数据
   * @param shardingSize 分片大小，用于处理大数据量
   * @param charset 字符编码，默认UTF-8
   * @return 加密后的Base64编码字符串，失败返回null
   */
  @JvmStatic
  @JvmOverloads
  fun encryptByRsaPublicKeyBase64(
    rsaPublicKey: String,
    data: String,
    shardingSize: Int = SHARDING_SIZE,
    charset: Charset = defaultCharset,
  ): String? =
    encryptByRsaPublicKey(
      Keys.readRsaPublicKeyByBase64(rsaPublicKey)!!,
      data,
      shardingSize,
      charset,
    )

  /**
   * 使用RSA公钥加密数据
   *
   * @param publicKey RSA公钥对象
   * @param data 待加密的原文数据
   * @param shardingSize 分片大小，用于处理大数据量
   * @param charset 字符编码，默认UTF-8
   * @return 加密后的Base64编码字符串，失败返回null
   */
  @JvmStatic
  @JvmOverloads
  fun encryptByRsaPublicKey(
    publicKey: RSAPublicKey,
    data: String,
    shardingSize: Int = SHARDING_SIZE,
    charset: Charset = defaultCharset,
  ): String? =
    encrypt(publicKey, data, shardingSize, charset, EncryptAlgorithmTyping.RSA)

  /**
   * 使用RSA私钥加密数据
   *
   * @param privateKey RSA私钥对象
   * @param data 待加密的原文数据
   * @param shardingSize 分片大小，用于处理大数据量
   * @param charset 字符编码，默认UTF-8
   * @return 加密后的Base64编码字符串，失败返回null
   */
  @JvmStatic
  @JvmOverloads
  fun encryptByRsaPrivateKey(
    privateKey: RSAPrivateKey,
    data: String,
    shardingSize: Int = SHARDING_SIZE,
    charset: Charset = defaultCharset,
  ): String? =
    encryptByPrivateKey(
      privateKey,
      data,
      shardingSize,
      charset,
      EncryptAlgorithmTyping.RSA,
    )

  /**
   * 使用ECC公钥加密数据
   *
   * @param eccPublicKey ECC公钥对象
   * @param data 待加密的原文数据
   * @param charset 字符编码，默认UTF-8
   * @return 加密后的Base64编码字符串，失败返回null
   */
  @JvmStatic
  @JvmOverloads
  fun encryptByEccPublicKey(
    eccPublicKey: PublicKey,
    data: String,
    charset: Charset = defaultCharset,
  ): String? =
    runCatching {
      Cipher.getInstance("ECIES", "BC").run {
        init(ENC_MODE, eccPublicKey, IESParameterSpec(null, null, 256))
        doFinal(data.toByteArray(charset)).let(IBase64::encode)
      }
    }
      .onFailure { log.error("${Encryptors::encryptByEccPublicKey.name} failed", it) }
      .getOrNull()

  /**
   * 使用ECC私钥解密数据
   *
   * @param eccPrivateKey ECC私钥对象
   * @param data Base64编码的加密数据
   * @param charset 字符编码，默认UTF-8
   * @return 解密后的原文，失败返回null
   */
  @JvmStatic
  @JvmOverloads
  fun decryptByEccPrivateKey(
    eccPrivateKey: PrivateKey,
    data: String,
    charset: Charset = defaultCharset,
  ): String? =
    runCatching {
      Cipher.getInstance("ECIES", "BC").run {
        init(DEC_MODE, eccPrivateKey, IESParameterSpec(null, null, 256))
        String(doFinal(IBase64.decodeToByte(data)), charset)
      }
    }
      .onFailure { log.error("${Encryptors::decryptByEccPrivateKey.name} failed", it) }
      .getOrNull()

  /**
   * 使用Base64编码的RSA私钥解密数据
   *
   * @param rsaPrivateKey Base64编码的RSA私钥
   * @param ciphertext Base64编码的加密数据
   * @param charset 字符编码，默认UTF-8
   * @return 解密后的原文，失败返回null
   */
  @JvmStatic
  @JvmOverloads
  fun decryptByRsaPrivateKey(
    rsaPrivateKey: RSAPrivateKey,
    ciphertext: String,
    charset: Charset = defaultCharset,
  ): String? =
    basicDecrypt(rsaPrivateKey, ciphertext, EncryptAlgorithmTyping.RSA, charset)

  /**
   * 使用RSA公钥解密数据
   *
   * @param rsaPublicKey RSA公钥对象
   * @param ciphertext Base64编码的加密数据
   * @param charset 字符编码，默认UTF-8
   * @return 解密后的原文，失败返回null
   */
  @JvmStatic
  @JvmOverloads
  fun decryptByRsaPublicKey(
    rsaPublicKey: RSAPublicKey,
    ciphertext: String,
    charset: Charset = defaultCharset,
  ): String? =
    runCatching {
      if (ciphertext.isEmpty()) return ""
      Cipher.getInstance(EncryptAlgorithmTyping.RSA.padding).run {
        init(DEC_MODE, rsaPublicKey)
        ciphertext
          .split(SHARDING_SEP)
          .map { IBase64.decodeToByte(it) }
          .map { doFinal(it) }
          .reduce { acc, byt -> acc + byt }
          .let { String(it, charset) }
      }
    }
      .onFailure { log.error("${Encryptors::decryptByRsaPublicKey.name} failed", it) }
      .getOrNull()

  /**
   * 使用Base64编码的RSA公钥解密数据
   *
   * @param rsaPublicKey Base64编码的RSA公钥
   * @param ciphertext Base64编码的加密数据
   * @param charset 字符编码，默认UTF-8
   * @return 解密后的原文，失败返回null
   */
  @JvmStatic
  @JvmOverloads
  fun decryptByRsaPublicKeyBase64(
    rsaPublicKey: String,
    ciphertext: String,
    charset: Charset = defaultCharset,
  ): String? =
    decryptByRsaPublicKey(
      Keys.readRsaPublicKeyByBase64(rsaPublicKey)!!,
      ciphertext,
      charset,
    )

  /**
   * 使用SHA-1算法计算数据的哈希值
   *
   * @param plaintext 待计算哈希的原文数据
   * @param charset 字符编码，默认UTF-8
   * @return 十六进制格式的哈希值字符串
   */
  @JvmStatic
  @JvmOverloads
  fun signatureBySha1(
    plaintext: String,
    charset: Charset = defaultCharset,
  ): String =
    signatureBySha1ByteArray(plaintext.toByteArray(charset))
      .joinToString("") { "%02x".format(it) }

  /**
   * 使用SHA-256算法计算数据的哈希值
   *
   * @param plaintext 待计算哈希的原文数据
   * @param charset 字符编码，默认UTF-8
   * @return 十六进制格式的哈希值字符串
   */
  @JvmStatic
  @JvmOverloads
  fun signatureBySha256(
    plaintext: String,
    charset: Charset = defaultCharset,
  ): String =
    signatureBySha256ByteArray(plaintext.toByteArray(charset))
      .joinToString("") { "%02x".format(it) }

  /**
   * 使用SHA-1算法计算字节数组的哈希值
   *
   * @param plaintext 待计算哈希的字节数组
   * @return 哈希值字节数组
   */
  @JvmStatic
  fun signatureBySha1ByteArray(plaintext: ByteArray): ByteArray =
    sha1.digest(plaintext)

  /**
   * 使用SHA-256算法计算字节数组的哈希值
   *
   * @param plaintext 待计算哈希的字节数组
   * @return 哈希值字节数组
   */
  @JvmStatic
  fun signatureBySha256ByteArray(plaintext: ByteArray): ByteArray =
    sha256.digest(plaintext)

  /**
   * 使用Base64编码的AES密钥加密数据
   *
   * @param aesKey Base64编码的AES密钥
   * @param plaintext 待加密的原文数据
   * @param charset 字符编码，默认UTF-8
   * @return 加密后的Base64编码字符串，失败返回null
   */
  @JvmStatic
  @JvmOverloads
  fun encryptByAesKeyBase64(
    aesKey: String,
    plaintext: String,
    charset: Charset = Charsets.UTF_8,
  ): String? =
    encryptByAesKey(
      SecretKeySpec(IBase64.decodeToByte(aesKey), "AES"),
      plaintext,
      charset,
    )

  /**
   * 使用AES密钥加密数据
   *
   * @param secret AES密钥对象
   * @param plain 待加密的原文数据
   * @param charset 字符编码，默认UTF-8
   * @return 加密后的Base64编码字符串，失败返回null
   */
  @JvmStatic
  @JvmOverloads
  fun encryptByAesKey(
    secret: SecretKeySpec,
    plain: String,
    charset: Charset = defaultCharset,
  ): String? =
    runCatching {
      Cipher.getInstance("AES/ECB/PKCS5Padding").run {
        init(ENC_MODE, secret)
        IBase64.encode(doFinal(plain.toByteArray(charset)))
      }
    }
      .onFailure { log.error("${Encryptors::encryptByAesKey.name} failed", it) }
      .getOrNull()

  /**
   * 使用Base64编码的AES密钥解密数据
   *
   * @param aesKey Base64编码的AES密钥
   * @param ciphertext Base64编码的加密数据
   * @param charset 字符编码，默认UTF-8
   * @return 解密后的原文，失败返回null
   */
  @JvmStatic
  @JvmOverloads
  fun decryptByAesKeyBase64(
    aesKey: String,
    ciphertext: String,
    charset: Charset = defaultCharset,
  ): String? =
    decryptByAesKey(
      SecretKeySpec(IBase64.decodeToByte(aesKey), "AES"),
      ciphertext,
      charset,
    )

  /**
   * 使用AES密钥解密数据
   *
   * @param secret AES密钥对象
   * @param ciphertext Base64编码的加密数据
   * @param charset 字符编码，默认UTF-8
   * @return 解密后的原文，失败返回null
   */
  @JvmStatic
  @JvmOverloads
  fun decryptByAesKey(
    secret: SecretKeySpec,
    ciphertext: String,
    charset: Charset = defaultCharset,
  ): String? =
    runCatching {
      Cipher.getInstance("AES/ECB/PKCS5Padding").run {
        init(DEC_MODE, secret)
        String(doFinal(IBase64.decodeToByte(ciphertext)), charset)
      }
    }
      .onFailure { log.error("${Encryptors::decryptByAesKey.name} failed", it) }
      .getOrNull()

  /**
   * 使用RSA私钥和SHA256算法进行数字签名
   *
   * @param signContent 待签名的数据
   * @param rsaPrivateKey RSA私钥对象
   * @param charset 字符编码，默认UTF-8
   * @return 签名对象
   */
  @JvmStatic
  @JvmOverloads
  fun signWithSha256WithRsaByRsaPrivateKey(
    signContent: String,
    rsaPrivateKey: RSAPrivateKey,
    charset: Charset = defaultCharset,
  ): Signature {
    val signature = Signature.getInstance(EncryptAlgorithmTyping.SHA256_WITH_RSA.value)
    signature.initSign(rsaPrivateKey)
    signature.update(signContent.toByteArray(charset))
    return signature
  }

  /**
   * 将字节数组按指定大小进行分片
   *
   * @param data 待分片的字节数组
   * @param size 分片大小
   * @return 分片后的字节数组列表
   */
  @JvmStatic
  internal fun sharding(data: ByteArray, size: Int): List<ByteArray> {
    val lastSliceSize = data.size % size
    val shardingData = mutableListOf<ByteArray>()
    var step = 0
    while (step < data.size) {
      val sliceSize = if (step + size > data.size) lastSliceSize else size
      shardingData += data.sliceArray(step until step + sliceSize)
      step += size
    }
    return shardingData
  }

  /** 日志记录器 */
  private val log = slf4j<Encryptors>()

  /** 分片 base64 分隔符 */
  private const val SHARDING_SEP = "."

  /** SHA-1 消息摘要实例 */
  private val sha1 = MessageDigest.getInstance("SHA-1")

  /** SHA-256 消息摘要实例 */
  private val sha256 = MessageDigest.getInstance("SHA-256")

  /** RSA加密分片大小 */
  private const val SHARDING_SIZE = 245

  /** 加密模式 */
  private const val ENC_MODE = Cipher.ENCRYPT_MODE

  /** 解密模式 */
  private const val DEC_MODE = Cipher.DECRYPT_MODE

  /** 默认字符编码 */
  private val defaultCharset: Charset = Charsets.UTF_8
}
