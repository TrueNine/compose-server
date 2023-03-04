package com.truenine.component.core.encrypt

import com.google.common.annotations.VisibleForTesting
import com.truenine.component.core.consts.Algorithm
import com.truenine.component.core.encrypt.base64.Base64Helper
import com.truenine.component.core.lang.LogKt
import org.slf4j.Logger
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.PrivateKey
import java.security.PublicKey
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
  private val h = Base64Helper.defaultHelper()

  @JvmStatic
  private val log: Logger = LogKt.getLog(Encryptors::class)

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
  private fun basicEncrypt(
    publicKey: PublicKey,
    data: String,
    shardingSize: Int = SHARDING_SIZE,
    charset: Charset = this.charset,
    alg: Algorithm = Algorithm.RSA_PADDING
  ): String? = runCatching {
    Cipher.getInstance(alg.str()).run {
      init(ENC_MODE, publicKey)
      sharding(data.toByteArray(charset), shardingSize)
        .joinToString(SHARDING_SEP) { h.encode(doFinal(it)) }
    }
  }.onFailure { log.error(::basicEncrypt.name, it) }.getOrNull()

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
    alg: Algorithm = Algorithm.RSA_PADDING,
    charset: Charset = this.charset,
  ): String? = runCatching {
    Cipher.getInstance(alg.str()).run {
      init(DEC_MODE, privateKey)
      data.split(SHARDING_SEP).map { h.decodeToByte(it) }
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
  fun encryptByRsaPublicKeyBase64(
    rsaPublicKey: String,
    data: String,
    shardingSize: Int = SHARDING_SIZE,
    charset: Charset = StandardCharsets.UTF_8
  ): String? = encryptByRsaPublicKey(
    Keys.readRsaPublicKeyByBase64(rsaPublicKey)!!,
    data,
    shardingSize,
    charset
  )

  /**
   * @param privateKey 私钥
   * @param data 加密数据
   * @param shardingSize 分片大小
   * @param charset 字符集
   * @return 密文
   */
  @JvmStatic
  fun encryptByRsaPublicKey(
    privateKey: RSAPublicKey,
    data: String,
    shardingSize: Int = SHARDING_SIZE,
    charset: Charset = this.charset
  ): String? = basicEncrypt(
    privateKey,
    data,
    shardingSize,
    charset,
    Algorithm.RSA_PADDING
  )


  /**
   * @param eccPublicKey ecc 公钥
   * @param data 数据
   * @param charset 字符集
   * @return 密文
   */
  @JvmStatic
  fun encryptByEccPublicKey(
    eccPublicKey: PublicKey,
    data: String,
    charset: Charset = this.charset
  ): String? = runCatching {
    Cipher.getInstance("ECIES", "BC").run {
      init(ENC_MODE, eccPublicKey)
      doFinal(data.toByteArray(charset)).let(h::encode)
    }
  }.onFailure { log.error(::encryptByEccPublicKey.name, it) }.getOrNull()

  /**
   * @param eccPrivateKey ecc 私钥
   * @param data 密文
   * @param charset 字符集
   * @return 明文
   */
  @JvmStatic
  fun decryptByEccPrivateKey(
    eccPrivateKey: PrivateKey,
    data: String,
    charset: Charset = this.charset
  ): String? = runCatching {
    Cipher.getInstance("ECIES", "BC").run {
      init(DEC_MODE, eccPrivateKey)
      String(doFinal(h.decodeToByte(data)), charset)
    }
  }.onFailure {
    log.error(::decryptByEccPrivateKey.name, it)
  }.getOrNull()

  /**
   * @param rsaPrivateKey 私钥
   * @param data 数据
   * @param charset 字符集
   * @return 明文
   */
  @JvmStatic
  fun decryptByRsaPrivateKeyBase64(
    rsaPrivateKey: String,
    data: String,
    charset: Charset = this.charset
  ): String? = decryptByRsaPrivateKey(
    Keys.readRsaPrivateKeyByBase64(rsaPrivateKey)!!,
    data,
    charset
  )


  /**
   * @param rsaPrivateKey 私钥
   * @param data 解密数据
   * @param charset 字符集
   * @return 解密数据
   */
  @JvmStatic
  fun decryptByRsaPrivateKey(
    rsaPrivateKey: RSAPrivateKey,
    data: String,
    charset: Charset = this.charset
  ): String? = basicDecrypt(rsaPrivateKey, data, Algorithm.RSA_PADDING, charset)

  /**
   * @param aesKey 密钥
   * @param data 解密数据
   * @param charset 字符集
   * @return 解密数据
   */
  @JvmStatic
  fun encryptByAesKeyBase64(
    aesKey: String,
    data: String,
    charset: Charset = StandardCharsets.UTF_8
  ): String? = encryptByAesKey(
    SecretKeySpec(h.decodeToByte(aesKey), "AES"),
    data,
    charset
  )

  /**
   * @param secret 密钥
   * @param data 明文
   * @param charset 字符集
   * @return 密文
   */
  @JvmStatic
  fun encryptByAesKey(
    secret: SecretKeySpec,
    data: String,
    charset: Charset = this.charset
  ): String? = runCatching {
    Cipher.getInstance("AES/ECB/PKCS5Padding").run {
      init(ENC_MODE, secret)
      h.encode(doFinal(data.toByteArray(charset)))
    }
  }.onFailure { log.error(::encryptByAesKey.name, it) }.getOrNull()

  /**
   * @param aesKey 密钥
   * @param data 密文
   * @param charset 字符集
   * @return 明文
   */
  @JvmStatic
  fun decryptByAesKeyBase64(
    aesKey: String,
    data: String,
    charset: Charset = StandardCharsets.UTF_8
  ): String? = decryptByAesKey(
    SecretKeySpec(h.decodeToByte(aesKey), "AES"),
    data,
    charset
  )

  /**
   * @param secret 密钥
   * @param data 密文
   * @param charset 字符集
   * @return 明文
   */
  @JvmStatic
  fun decryptByAesKey(
    secret: SecretKeySpec,
    data: String,
    charset: Charset = this.charset
  ): String? = kotlin.runCatching {
    Cipher.getInstance("AES/ECB/PKCS5Padding").run {
      init(DEC_MODE, secret)
      String(doFinal(h.decodeToByte(data)), charset)
    }
  }.onFailure { log.error(::decryptByAesKey.name, it) }.getOrNull()


  /**
   * @param data 数据字节数组
   * @param size 分片大小
   * @return 分片数据
   */
  @JvmStatic
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
