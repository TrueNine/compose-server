/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 * 
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
*/
package net.yan100.compose.core.properties

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 密钥
 *
 * @author TrueNine
 * @since 2023-04-22
 */
@ConfigurationProperties(prefix = "compose.core.keys")
class KeysProperties {
  /** 密钥存放的 resources 对应目录  */
  var dir = "security"

  /** ecc 公钥路径  */
  var eccPublicKeyPath = "ecc_public.key"

  /** ecc 私钥路径  */
  var eccPrivateKeyPath = "ecc_private.key"

  /** rsa 公钥路径  */
  var rsaPublicKeyPath = "rsa_public.key"

  /** rsa 私钥路径  */
  var rsaPrivateKeyPath = "rsa_private.key"

  /** aes key 路径  */
  var aesKeyPath = "aes.key"
}
