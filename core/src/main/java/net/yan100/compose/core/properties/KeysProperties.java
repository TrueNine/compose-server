package net.yan100.compose.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 密钥
 *
 * @author TrueNine
 * @since 2023-04-22
 */
@Data
@ConfigurationProperties(prefix = "compose.keys")
public class KeysProperties {
  /**
   * 密钥存放的 resources 对应目录
   */
  private String dir = "security";
  /**
   * ecc 公钥路径
   */
  private String eccPublicKeyPath = "ecc_public.key";
  /**
   * ecc 私钥路径
   */
  private String eccPrivateKeyPath = "ecc_private.key";
  /**
   * rsa 公钥路径
   */
  private String rsaPublicKeyPath = "rsa_public.key";
  /**
   * rsa 私钥路径
   */
  private String rsaPrivateKeyPath = "rsa_private.key";
  /**
   * aes key 路径
   */
  private String aesKeyPath = "aes.key";
}
