package io.github.truenine.composeserver.security.properties

import io.github.truenine.composeserver.consts.SpringBootConfigurationPropertiesPrefixes
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Security key configuration properties.
 *
 * @author TrueNine
 * @since 2023-04-22
 */
@ConfigurationProperties(prefix = SpringBootConfigurationPropertiesPrefixes.SECURITY_KEYS)
data class KeysProperties(
  /** Directory under resources where keys are stored */
  var dir: String = "security",

  /** ECC public key path */
  var eccPublicKeyPath: String = "ecc_public.key",

  /** ECC private key path */
  var eccPrivateKeyPath: String = "ecc_private.key",

  /** RSA public key path */
  var rsaPublicKeyPath: String = "rsa_public.key",

  /** RSA private key path */
  var rsaPrivateKeyPath: String = "rsa_private.key",

  /** AES key path */
  var aesKeyPath: String = "aes.key",
)
