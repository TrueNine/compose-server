package com.truenine.component.core.encrypt

import com.truenine.component.core.consts.Algorithm
import com.truenine.component.core.encrypt.base64.Base64Helper
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
   * 通用加密基方法
   *
   * @param publicKey 公钥
   * @param data 加密数据
   * @param shardingSize 分片长度
   * @param charset 默认字符集
   * @param alg 加密算法
   */
  @JvmStatic
  private fun basicEncrypt(
    publicKey: PublicKey,
    data: String,
    shardingSize: Int = SHARDING_SIZE,
    charset: Charset = this.charset,
    alg: Algorithm = Algorithm.RSA_PADDING
  ): String {
    val c = Cipher.getInstance(alg.str())
    c.init(ENC_MODE, publicKey)
    val bf = StringBuilder()
    sharding(data.toByteArray(charset), shardingSize).forEach {
      val encoding = h.encode(c.doFinal(it))
      bf.append(encoding)
      bf.append(SHARDING_SEP)
    }
    return bf.removeSuffix(SHARDING_SEP).toString()
  }

  /**
   * 通用解密方法
   * @param privateKey 私钥
   * @param data 解密数据
   * @param alg 解密算法
   * @param charset 字符集
   */
  @JvmStatic
  private fun basicDecrypt(
    privateKey: PrivateKey,
    data: String,
    alg: Algorithm = Algorithm.RSA_PADDING,
    charset: Charset = this.charset,
  ): String {
    val c = Cipher.getInstance(alg.str())
    c.init(DEC_MODE, privateKey)
    val allBytes = mutableListOf<ByteArray>()
    data.split(SHARDING_SEP).forEach {
      val cipherText = h.decodeToByte(it)
      val cc = c.doFinal(cipherText)
      allBytes += cc
    }
    return String(
      allBytes.reduce { acc, byt ->
        byteArrayOf(*acc, *byt)
      }, charset
    )
  }

  /**
   * rsa 加密
   *
   * @param rsaPublicKey 公钥
   * @param data 加密数据
   * @param shardingSize 分片大小
   * @param charset 字符集
   */
  @JvmStatic
  fun encryptByRsaPublicKeyBase64(
    rsaPublicKey: String,
    data: String,
    shardingSize: Int = SHARDING_SIZE,
    charset: Charset = StandardCharsets.UTF_8
  ): String {
    return encryptByRsaPublicKey(
      Keys.readRsaPublicKeyByBase64(rsaPublicKey),
      data,
      shardingSize,
      charset
    )
  }

  /**
   * rsa 加密
   *
   * @param privateKey 私钥
   * @param data 加密数据
   * @param shardingSize 分片大小
   * @param charset 字符集
   */
  @JvmStatic
  fun encryptByRsaPublicKey(
    privateKey: RSAPublicKey,
    data: String,
    shardingSize: Int = SHARDING_SIZE,
    charset: Charset = this.charset
  ): String {
    return basicEncrypt(
      privateKey,
      data,
      shardingSize,
      charset,
      Algorithm.RSA_PADDING
    )
  }

  /**
   * ecc 加密，使用 ECC PublicKey
   *
   * @param eccPublicKey ecc 公钥
   * @param data 数据
   * @param charset 字符集
   */
  @JvmStatic
  fun encryptByEccPublicKey(
    eccPublicKey: PublicKey,
    data: String,
    charset: Charset = this.charset
  ): String {
    val cipher = Cipher.getInstance("ECIES", "BC")
    cipher.init(ENC_MODE, eccPublicKey)
    val cipherText = cipher.doFinal(data.toByteArray(charset))
    return h.encode(cipherText)
  }

  /**
   * ecc 解密
   * @param eccPrivateKey ecc 私钥
   * @param data 密文
   * @param charset 字符集
   */
  @JvmStatic
  fun decryptByEccPrivateKey(
    eccPrivateKey: PrivateKey,
    data: String,
    charset: Charset = this.charset
  ): String {
    val cipher = Cipher.getInstance("ECIES", "BC")
    cipher.init(DEC_MODE, eccPrivateKey)
    val cipherBytes = h.decodeToByte(data)
    return String(cipher.doFinal(cipherBytes), charset)
  }

  /**
   * rsa 解密
   *
   * @param rsaPrivateKey 私钥
   * @param data 数据
   * @param charset 字符集
   */
  @JvmStatic
  fun decryptByRsaPrivateKeyBase64(
    rsaPrivateKey: String,
    data: String,
    charset: Charset = this.charset
  ): String {
    return decryptByRsaPrivateKey(
      Keys.readRsaPrivateKeyByBase64(rsaPrivateKey),
      data,
      charset
    )
  }

  /**
   * rsa 解密
   *
   * @param rsaPrivateKey 私钥
   * @param data 解密数据
   * @param charset 字符集
   */
  @JvmStatic
  fun decryptByRsaPrivateKey(
    rsaPrivateKey: RSAPrivateKey,
    data: String,
    charset: Charset = this.charset
  ): String {
    return basicDecrypt(rsaPrivateKey, data, Algorithm.RSA_PADDING, charset)
  }

  @JvmStatic
  fun encryptByAesKeyBase64(
    aesKey: String,
    data: String,
    charset: Charset = StandardCharsets.UTF_8
  ): String {
    return encryptByAesKey(
      SecretKeySpec(h.decodeToByte(aesKey), "AES"),
      data,
      charset
    )
  }

  @JvmStatic
  fun encryptByAesKey(
    secret: SecretKeySpec,
    data: String,
    charset: Charset = this.charset
  ): String {
    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(
      ENC_MODE,
      secret
    )
    val encryptedBytes = cipher.doFinal(data.toByteArray(charset))
    return this.h.encode(encryptedBytes)
  }

  @JvmStatic
  fun decryptByAesKeyBase64(
    aesKey: String,
    data: String,
    charset: Charset = StandardCharsets.UTF_8
  ): String {
    return decryptByAesKey(
      SecretKeySpec(h.decodeToByte(aesKey), "AES"),
      data,
      charset
    )
  }

  @JvmStatic
  fun decryptByAesKey(
    secret: SecretKeySpec,
    data: String,
    charset: Charset = this.charset
  ): String {
    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(
      DEC_MODE,
      secret
    )
    val decryptedBytes = cipher.doFinal(h.decodeToByte(data))
    return String(decryptedBytes, charset)
  }


  /**
   * 数据分片方法
   *
   * @param data 数据字节数组
   * @param size 分片大小
   */
  @JvmStatic
  private fun sharding(data: ByteArray, size: Int): List<ByteArray> {
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
