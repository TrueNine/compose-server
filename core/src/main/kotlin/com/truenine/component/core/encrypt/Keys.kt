package com.truenine.component.core.encrypt

import com.truenine.component.core.consts.Algorithm
import com.truenine.component.core.dev.UnImplemented
import com.truenine.component.core.encrypt.base64.Base64Helper
import com.truenine.component.core.encrypt.base64.SimpleUtf8Base64
import com.truenine.component.core.encrypt.consts.EccKeyPair
import com.truenine.component.core.encrypt.consts.RsaKeyPair
import com.truenine.component.core.lang.ResourcesLocator
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

/**
 * 加解密密钥工具类
 *
 * @author TrueNine
 * @since 2023-02-19
 */
object Keys {
  const val RSA_KEY_SIZE = 2048
  const val ECC_KEY_SIZE = 512
  private val RSA_ALG = Algorithm.RSA.str()
  private val BASE_64_HELPER: Base64Helper = SimpleUtf8Base64()
  const val SEED = "T-DECRYPT"
  private const val PUB_NAME = "pub.key"
  private const val PRI_NAME = "pri.key"

  /**
   * 将 rsa 供公钥 base64 版本转换为 java 类型的 rsa 公钥
   * @param base64 rsa 公钥 base64 字符串
   */
  @JvmStatic
  fun rsaPubKey(base64: String): RSAPublicKey {
    return pubKey(base64, Algorithm.RSA) as RSAPublicKey
  }

  /**
   * 将 base64 ecc 公钥转换为 java 的 publlic key
   *
   * @param base64 ecc base64 公钥
   */
  @UnImplemented
  @JvmStatic
  fun eccPubKey(base64: String): PublicKey {
    return pubKey(base64, Algorithm.ECC)
  }

  /**
   * 将 rsa base64 私钥 转换为 java 类型和的 RSAPrivateKey
   *
   * @param base64 rsa base64 私钥
   */
  @JvmStatic
  fun rsaPriKey(base64: String): RSAPrivateKey {
    return priKey(base64, Algorithm.RSA) as RSAPrivateKey
  }

  /**
   * 将 ecc base64 私钥 转换为 java 类型的 PrivateKey
   *
   * @param base64 ecc base64 私钥
   */
  @UnImplemented
  @JvmStatic
  fun eccPriKey(base64: String): PrivateKey {
    return priKey(base64, Algorithm.ECC)
  }

  /**
   * 将 一个 base64 公钥 根据提供的算法转换为 java 类型的 PublicKey
   * @param base64 base64 公钥
   * @param alg 公钥使用的算法
   */
  @JvmStatic
  fun pubKey(base64: String, alg: Algorithm): PublicKey {
    return try {
      val spec = X509EncodedKeySpec(BASE_64_HELPER.decodeToByte(base64))
      KeyFactory.getInstance(alg.str()).generatePublic(spec)
    } catch (e: NoSuchAlgorithmException) {
      throw RuntimeException(e)
    } catch (e: InvalidKeySpecException) {
      throw RuntimeException(e)
    }
  }

  /**
   * 将 一个 base64 私钥 根据提供的算法转换为 java 类型的 PublicKey
   * @param base64 base64 私钥
   * @param alg 私钥使用的算法
   */
  @JvmStatic
  fun priKey(base64: String, alg: Algorithm): PrivateKey {
    return try {
      val spec = PKCS8EncodedKeySpec(BASE_64_HELPER.decodeToByte(base64))
      KeyFactory.getInstance(alg.str()).generatePrivate(spec)
    } catch (e: NoSuchAlgorithmException) {
      throw java.lang.RuntimeException(e)
    } catch (e: InvalidKeySpecException) {
      throw java.lang.RuntimeException(e)
    }
  }

  /**
   * 使用 KeyPairGenerator 生成一个密钥对
   * @param seed 种子
   * @param keySize key 长度
   */
  @JvmStatic
  fun genPair(seed: String = SEED, keySize: Int): KeyPair {
    val gen: KeyPairGenerator = try {
      KeyPairGenerator.getInstance(RSA_ALG)
    } catch (e: NoSuchAlgorithmException) {
      throw java.lang.RuntimeException(e)
    }
    val secureRandom = SecureRandom(seed.toByteArray(StandardCharsets.UTF_8))
    gen.initialize(keySize, secureRandom)
    return gen.generateKeyPair()
  }

  /**
   * 将 密钥对 以 base64 形式保存至文件，文件路径为默认的项目生成目录内
   *
   * @param publicKey 公钥
   * @param privateKey 私钥
   */
  @JvmStatic
  private fun save(publicKey: PublicKey, privateKey: PrivateKey) {
    val pub = ResourcesLocator.createGenerateFile("gen", PUB_NAME)!!
    val pri = ResourcesLocator.createGenerateFile("gen", PRI_NAME)!!
    Files.writeString(pub.toPath(), BASE_64_HELPER.encode(publicKey.encoded))
    Files.writeString(pri.toPath(), BASE_64_HELPER.encode(privateKey.encoded))
  }

  /**
   * 将 密钥对 以 base64 形式保存至文件，文件路径为默认的项目生成目录内
   *
   * @param keyPair 密钥对
   */
  @JvmStatic
  fun savePair(keyPair: KeyPair) {
    save(keyPair.public, keyPair.private)
  }

  /**
   * 将 rsa密钥对 以 base64 形式保存至文件，文件路径为默认的项目生成目录内
   *
   * @param keyPair rsa密钥对
   */
  @JvmStatic
  fun saveRsa(keyPair: RsaKeyPair) {
    save(keyPair.pub, keyPair.pri)
  }

  /**
   * 将 ecc密钥对 以 base64 形式保存至文件，文件路径为默认的项目生成目录内
   *
   * @param keyPair ecc密钥对
   */
  @JvmStatic
  fun saveEcc(keyPair: EccKeyPair) {
    save(keyPair.pub, keyPair.pri)
  }

  /**
   * 生成一对 rsa 密钥对
   *
   * @param seed 种子
   * @param keySize 密钥长度
   */
  @JvmStatic
  fun genRsaPair(seed: String = SEED, keySize: Int = RSA_KEY_SIZE): RsaKeyPair {
    val keyPair = genPair(seed, keySize)
    return RsaKeyPair()
      .setPubKey(keyPair.public as RSAPublicKey)
      .setPriKey(keyPair.private as RSAPrivateKey)
  }

  /**
   * 将指定文件路径的rsa密钥对还原为 java 类型
   *
   * @param priPath 私钥路径
   * @param pubPath 公钥路径
   */
  @JvmStatic
  fun fromRsa(priPath: String, pubPath: String): RsaKeyPair {

    val pair = RsaKeyPair();
    try {
      val pri = ResourcesLocator.classpathUrl(priPath).readText()
      val pub = ResourcesLocator.classpathUrl(pubPath).readText()
      pair.setPriKey(rsaPriKey(pri))
      pair.setPubKey(rsaPubKey(pub))
    } catch (ex: NullPointerException) {
      throw RuntimeException("请配置公私钥 到 $priPath 以及 $pubPath")
    }
    return pair
  }

  /**
   * 生成一对 ecc 密钥对
   *
   * @param keySize 密钥长度
   * @return ecc密钥对
   */
  @UnImplemented
  @JvmStatic
  fun genEccPair(keySize: Int = ECC_KEY_SIZE): EccKeyPair? {
    return try {
      val generator = KeyPairGenerator.getInstance("EC", "BC")
      generator.initialize(keySize)
      val keyPair = generator.generateKeyPair()
      val publicKey = keyPair.public
      val privateKey = keyPair.private
      val ecc = EccKeyPair()
      ecc.setPriKey(privateKey)
      ecc.setPubKey(publicKey)
      ecc
    } catch (e: Exception) {
      null
    }
  }
}
