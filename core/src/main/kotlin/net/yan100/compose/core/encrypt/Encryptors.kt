package net.yan100.compose.core.encrypt

import com.google.common.annotations.VisibleForTesting
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.core.typing.EncryptAlgorithmTyping
import org.bouncycastle.jce.spec.IESParameterSpec
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * 加解密工具类
 *
 * @author TrueNine
 * @since 2023-02-28
 */
object Encryptors {
  @JvmStatic
  private val log = slf4j(Encryptors::class)

  @JvmStatic
  private val sha1 = MessageDigest.getInstance("SHA-1")

  /**
   * 分片 base64 分隔符
   */
  private const val SHARDING_SEP = "."

  /**
   * 分片大小
   */
  private const val SHARDING_SIZE = 245

  /**
   * 加密模式
   */
  private const val ENC_MODE = Cipher.ENCRYPT_MODE

  /**
   * 解密模式
   */
  private const val DEC_MODE = Cipher.DECRYPT_MODE

  /**
   * 默认加解密字符串编码格式
   */
  @JvmStatic
  val charset: Charset = StandardCharsets.UTF_8

  /**
   * @param publicKey 公钥
   * @param data 加密数据
   * @param shardingSize 分片长度
   * @param charset 默认字符集
   * @param alg 加密算法
   * @return 密文
   */
  @JvmStatic
  private fun encrypt(
    publicKey: PublicKey,
    data: String,
    shardingSize: Int = SHARDING_SIZE,
    charset: Charset = this.charset,
    alg: EncryptAlgorithmTyping = EncryptAlgorithmTyping.RSA_PADDING
  ): String? = runCatching {
    Cipher.getInstance(alg.getValue()).run {
      init(ENC_MODE, publicKey)
      sharding(data.toByteArray(charset), shardingSize)
        .joinToString(SHARDING_SEP) { Base64Helper.encode(doFinal(it)) }
    }
  }.onFailure { log.error(::encrypt.name, it) }.getOrNull()


  @JvmStatic
  private fun encryptByPrivateKey(
    privateKey: PrivateKey,
    data: String,
    shardingSize: Int = SHARDING_SIZE,
    charset: Charset = this.charset,
    alg: EncryptAlgorithmTyping = EncryptAlgorithmTyping.RSA_PADDING
  ): String? = runCatching {
    Cipher.getInstance(alg.getValue()).run {
      init(ENC_MODE, privateKey)
      sharding(data.toByteArray(charset), shardingSize)
        .joinToString(SHARDING_SEP) { Base64Helper.encode(doFinal(it)) }
    }
  }.onFailure { log.error(::encrypt.name, it) }.getOrNull()


  /**
   * @param privateKey 私钥
   * @param data 解密数据
   * @param alg 解密算法
   * @param charset 字符集
   * @return 明文
   */
  @JvmStatic
  private fun basicDecrypt(
    privateKey: PrivateKey,
    data: String,
    alg: EncryptAlgorithmTyping = EncryptAlgorithmTyping.RSA_PADDING,
    charset: Charset = this.charset,
  ): String? = runCatching {
    Cipher.getInstance(alg.getValue()).run {
      init(DEC_MODE, privateKey)
      data.split(SHARDING_SEP).map { Base64Helper.decodeToByte(it) }
        .map { doFinal(it) }
        .reduce { acc, byt -> acc + byt }
        .let { String(it, charset) }
    }
  }.onFailure { log.error(::basicDecrypt.name, it) }.getOrNull()


  /**
   * @param rsaPublicKey 公钥
   * @param data 加密数据
   * @param shardingSize 分片大小
   * @param charset 字符集
   * @return 密文
   */
  @JvmStatic
  @JvmOverloads
  fun encryptByRsaPublicKeyBase64(
    rsaPublicKey: String,
    data: String,
    shardingSize: Int = SHARDING_SIZE,
    charset: Charset = this.charset
  ): String? = encryptByRsaPublicKey(
    Keys.readRsaPublicKeyByBase64(rsaPublicKey)!!,
    data,
    shardingSize,
    charset
  )

  /**
   * @param publicKey 公钥
   * @param data 加密数据
   * @param shardingSize 分片大小
   * @param charset 字符集
   * @return 密文
   */
  @JvmStatic
  @JvmOverloads
  fun encryptByRsaPublicKey(
    publicKey: RSAPublicKey,
    data: String,
    shardingSize: Int = SHARDING_SIZE,
    charset: Charset = this.charset
  ): String? = encrypt(
    publicKey,
    data,
    shardingSize,
    charset,
    EncryptAlgorithmTyping.RSA_PADDING
  )

  /**
   * ## rsa 私钥加密
   *
   * @param privateKey rsa 私钥
   * @param data 加密数据
   * @param shardingSize 分片大小
   * @param charset 字符集
   * @return [String]? 加密后的 base64字符串
   */
  @JvmStatic
  @JvmOverloads
  fun encryptByRsaPrivateKey(
    privateKey: RSAPrivateKey,
    data: String,
    shardingSize: Int = SHARDING_SIZE,
    charset: Charset = this.charset
  ): String? = encryptByPrivateKey(
    privateKey,
    data,
    shardingSize,
    charset,
    EncryptAlgorithmTyping.RSA_PADDING
  )


  /**
   * @param eccPublicKey ecc 公钥
   * @param data 数据
   * @param charset 字符集
   * @return 密文
   */
  @JvmStatic
  @JvmOverloads
  fun encryptByEccPublicKey(
    eccPublicKey: PublicKey,
    data: String,
    charset: Charset = this.charset
  ): String? = runCatching {
    Cipher.getInstance("ECIES", "BC").run {
      init(ENC_MODE, eccPublicKey, IESParameterSpec(null, null, 256))
      doFinal(data.toByteArray(charset)).let(net.yan100.compose.core.encrypt.Base64Helper::encode)
    }
  }.onFailure { log.error(::encryptByEccPublicKey.name, it) }.getOrNull()

  /**
   * @param eccPrivateKey ecc 私钥
   * @param data 密文
   * @param charset 字符集
   * @return 明文
   */
  @JvmStatic
  @JvmOverloads
  fun decryptByEccPrivateKey(
    eccPrivateKey: PrivateKey,
    data: String,
    charset: Charset = this.charset
  ): String? = runCatching {
    Cipher.getInstance("ECIES", "BC").run {
      init(DEC_MODE, eccPrivateKey, IESParameterSpec(null, null, 256))
      String(doFinal(Base64Helper.decodeToByte(data)), charset)
    }
  }.onFailure {
    log.error(::decryptByEccPrivateKey.name, it)
  }.getOrNull()

  /**
   * @param rsaPrivateKey 私钥
   * @param ciphertext 数据
   * @param charset 字符集
   * @return 明文
   */
  @JvmStatic
  @JvmOverloads
  fun decryptByRsaPrivateKeyBase64(
    rsaPrivateKey: String,
    ciphertext: String,
    charset: Charset = this.charset
  ): String? = decryptByRsaPrivateKey(
    Keys.readRsaPrivateKeyByBase64(rsaPrivateKey)!!,
    ciphertext,
    charset
  )

  /**
   * 使用 SHA 1 进行签名
   */
  @JvmStatic
  @JvmOverloads
  fun signatureBySha1(plaintext: String, charset: Charset = this.charset): String {
    return signatureBySha1ByteArray(plaintext.toByteArray(charset)).joinToString("") { "%02x".format(it) }
  }

  /**
   * 使用 SHA 1 进行签名 byte[]
   */
  @JvmStatic
  fun signatureBySha1ByteArray(plaintext: ByteArray): ByteArray {
    return sha1.digest(plaintext)
  }


  /**
   * @param rsaPrivateKey 私钥
   * @param ciphertext 解密数据
   * @param charset 字符集
   * @return 解密数据
   */
  @JvmStatic
  @JvmOverloads
  fun decryptByRsaPrivateKey(
    rsaPrivateKey: RSAPrivateKey,
    ciphertext: String,
    charset: Charset = this.charset
  ): String? = basicDecrypt(rsaPrivateKey, ciphertext, EncryptAlgorithmTyping.RSA_PADDING, charset)

  /**
   * @param aesKey 密钥
   * @param plaintext 解密数据
   * @param charset 字符集
   * @return 解密数据
   */
  @JvmStatic
  @JvmOverloads
  fun encryptByAesKeyBase64(
    aesKey: String,
    plaintext: String,
    charset: Charset = StandardCharsets.UTF_8
  ): String? = encryptByAesKey(
    SecretKeySpec(Base64Helper.decodeToByte(aesKey), "AES"),
    plaintext,
    charset
  )

  /**
   * @param secret 密钥
   * @param plain 明文
   * @param charset 字符集
   * @return 密文
   */
  @JvmStatic
  @JvmOverloads
  fun encryptByAesKey(
    secret: SecretKeySpec,
    plain: String,
    charset: Charset = this.charset
  ): String? = runCatching {
    Cipher.getInstance("AES/ECB/PKCS5Padding").run {
      init(ENC_MODE, secret)
      Base64Helper.encode(doFinal(plain.toByteArray(charset)))
    }
  }.onFailure { log.error(::encryptByAesKey.name, it) }.getOrNull()

  /**
   * @param aesKey 密钥
   * @param ciphertext 密文
   * @param charset 字符集
   * @return 明文
   */
  @JvmStatic
  @JvmOverloads
  fun decryptByAesKeyBase64(
    aesKey: String,
    ciphertext: String,
    charset: Charset = this.charset
  ): String? = decryptByAesKey(
    SecretKeySpec(Base64Helper.decodeToByte(aesKey), "AES"),
    ciphertext,
    charset
  )

  /**
   * @param secret 密钥
   * @param ciphertext 密文
   * @param charset 字符集
   * @return 明文
   */
  @JvmStatic
  @JvmOverloads
  fun decryptByAesKey(
    secret: SecretKeySpec,
    ciphertext: String,
    charset: Charset = this.charset
  ): String? = kotlin.runCatching {
    Cipher.getInstance("AES/ECB/PKCS5Padding").run {
      init(DEC_MODE, secret)
      String(doFinal(Base64Helper.decodeToByte(ciphertext)), charset)
    }
  }.onFailure { log.error(::decryptByAesKey.name, it) }.getOrNull()

  @JvmStatic
  @JvmOverloads
  fun signWithSha256WithRsaByRsaPrivateKey(
    signContent: String,
    rsaPrivateKey: RSAPrivateKey,
    charset: Charset = this.charset
  ): Signature {
    val signature = Signature.getInstance(EncryptAlgorithmTyping.SHA256_WITH_RSA.getValue())
    signature.initSign(rsaPrivateKey)
    signature.update(signContent.toByteArray(charset))
    return signature
  }

  /**
   * @param data 数据字节数组
   * @param size 分片大小
   * @return 分片数据
   */
  @JvmStatic
  @JvmOverloads
  @VisibleForTesting
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
}

/**
 * 将字符串转换为 sha1
 */
val String.sha1: String get() = Encryptors.signatureBySha1(this)
