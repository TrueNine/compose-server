package io.tn.core.encrypt

import io.tn.core.consts.Algorithm
import io.tn.core.encrypt.base64.Base64Helper
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import javax.crypto.Cipher

/**
 * 加解密工具类
 *
 * @author TrueNine
 * @since 2023-02-28
 */
object Enc {
  private val H = Base64Helper.defaultHelper()

  /**
   * 分片 base64 分隔符
   */
  private const val SHARDING_SEP = "."

  /**
   * 分片大小
   */
  const val SHARDING_SIZE = 245

  /**
   * 加密模式
   */
  private const val ENC_MODE = Cipher.ENCRYPT_MODE

  /**
   * 解密模式
   */
  private const val DEC_MODE = Cipher.DECRYPT_MODE

  /**
   * rsa 最大加密长度
   */
  const val RSA_ENC_MAX = 117

  /**
   * rsa 最大解密长度
   */
  const val RSA_DEC_MAX = 128

  /**
   * 默认加解密字符串编码格式
   */
  @JvmStatic
  val charset: Charset = StandardCharsets.UTF_8

  /**
   * 通用加密基方法
   *
   * @param priKey 私钥
   * @param shardingSize 分片长度
   * @param data 加密数据
   * @param charset 默认字符集
   * @param alg 加密算法
   */
  @JvmStatic
  fun enc(
    priKey: PrivateKey,
    shardingSize: Int = SHARDING_SIZE,
    data: String,
    charset: Charset = this.charset,
    alg: Algorithm = Algorithm.RSA_PADDING
  ): String {
    val c = Cipher.getInstance(alg.str())
    c.init(ENC_MODE, priKey)
    val bf = StringBuilder()
    sharding(data.toByteArray(charset), shardingSize).forEach {
      val encoding = H.encode(c.doFinal(it.toByteArray()))
      bf.append(encoding)
      bf.append(SHARDING_SEP)
    }
    return bf.removeSuffix(SHARDING_SEP).toString()
  }

  /**
   * 通用解密方法
   * @param pubKey 公钥
   * @param data 解密数据
   * @param alg 解密算法
   * @param charset 字符集
   */
  @JvmStatic
  fun dec(
    pubKey: PublicKey,
    data: String,
    alg: Algorithm = Algorithm.RSA_PADDING,
    charset: Charset = this.charset,
  ): String {
    val c = Cipher.getInstance(alg.str())
    c.init(DEC_MODE, pubKey)

    val allBytes = mutableListOf<ByteArray>()
    data.split(SHARDING_SEP).forEach {
      val cipherText = H.decodeToByte(it)
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
   * @param priKey 私钥
   * @param data 加密数据
   * @param shardingSize 分片大小
   * @param charset 字符集
   */
  @JvmStatic
  fun encRsa(
    priKey: String,
    data: String,
    shardingSize: Int = SHARDING_SIZE,
    charset: Charset = StandardCharsets.UTF_8
  ): String {
    return enc(
      Keys.rsaPriKey(priKey),
      shardingSize,
      data,
      charset,
      Algorithm.RSA_PADDING
    )
  }

  /**
   * rsa 加密
   *
   * @param priKey 私钥 java 类型
   * @param data 加密数据
   * @param shardingSize 分片大小
   * @param charset 字符集
   */
  @JvmStatic
  fun encRsaBy(
    priKey: RSAPrivateKey,
    data: String,
    shardingSize: Int = SHARDING_SIZE,
    charset: Charset = this.charset
  ): String {
    return enc(priKey, shardingSize, data, charset, Algorithm.RSA_PADDING)
  }

  /**
   * 数据分片方法
   *
   * @param data 数据字节数组
   * @param size 分片大小
   */
  @JvmStatic
  fun sharding(data: ByteArray, size: Int): List<MutableList<Byte>> {
    val remainder = data.size % size
    val shardingData = mutableListOf<MutableList<Byte>>()
    for (i in data.indices) {
      if (0 != i && i % size == 0) {
        val slice = data.slice(i - size until i)
        shardingData.add(slice.toMutableList())
      }
    }
    shardingData += data.slice((data.size - remainder) until data.size)
      .toMutableList()
    return shardingData.toList()
  }

  /**
   * rsa 解密
   *
   * @param pubKey 公钥
   * @param data 数据
   * @param charset 字符集
   */
  @JvmStatic
  fun decRsa(
    pubKey: String,
    data: String,
    charset: Charset = this.charset
  ): String {
    return dec(Keys.rsaPubKey(pubKey), data, Algorithm.RSA_PADDING, charset)
  }

  /**
   * rsa 解密
   *
   * @param pubKey 公钥
   * @param data 解密数据
   * @param charset 字符集
   */
  @JvmStatic
  fun decRsaBy(
    pubKey: RSAPublicKey,
    data: String,
    charset: Charset = this.charset
  ): String {
    return dec(pubKey, data, Algorithm.RSA_PADDING, charset)
  }
}
