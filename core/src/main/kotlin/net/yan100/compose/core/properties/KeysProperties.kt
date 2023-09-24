package net.yan100.compose.core.properties

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 密钥
 *
 * @author TrueNine
 * @since 2023-04-22
 */
@ConfigurationProperties(prefix = "compose.keys")
class KeysProperties {
  /**
   * 密钥存放的 resources 对应目录
   */
  val dir = "security"

  /**
   * ecc 公钥路径
   */
  val eccPublicKeyPath = "ecc_public.key"

  /**
   * ecc 私钥路径
   */
  val eccPrivateKeyPath = "ecc_private.key"

  /**
   * rsa 公钥路径
   */
  val rsaPublicKeyPath = "rsa_public.key"

  /**
   * rsa 私钥路径
   */
  val rsaPrivateKeyPath = "rsa_private.key"

  /**
   * aes key 路径
   */
  val aesKeyPath = "aes.key"
}
