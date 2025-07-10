package io.github.truenine.composeserver.security.properties

import org.springframework.boot.context.properties.ConfigurationProperties

private const val PREFIX = "compose.security"

/**
 * 密钥
 *
 * @author TrueNine
 * @since 2023-04-22
 */
@ConfigurationProperties(prefix = "$PREFIX.keys")
data class KeysProperties(
  /** 密钥存放的 resources 对应目录 */
  var dir: String = "security",

  /** ecc 公钥路径 */
  var eccPublicKeyPath: String = "ecc_public.key",

  /** ecc 私钥路径 */
  var eccPrivateKeyPath: String = "ecc_private.key",

  /** rsa 公钥路径 */
  var rsaPublicKeyPath: String = "rsa_public.key",

  /** rsa 私钥路径 */
  var rsaPrivateKeyPath: String = "rsa_private.key",

  /** aes key 路径 */
  var aesKeyPath: String = "aes.key",
)
