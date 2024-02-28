/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.core.models

import com.fasterxml.jackson.annotation.JsonIgnore
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import net.yan100.compose.core.encrypt.Base64Helper

/**
 * rsa密钥对
 *
 * @author TrueNine
 * @since 2022-12-09
 */
data class RsaKeyPair(
  @JsonIgnore var rsaPublicKey: RSAPublicKey? = null,
  @JsonIgnore var rsaPrivateKey: RSAPrivateKey? = null
) {
  @get:JsonIgnore
  val rsaPublicKeyBase64: String
    get() = Base64Helper.encode(rsaPublicKey!!.encoded)

  @get:JsonIgnore
  val rsaPrivateKeyBase64: String
    get() = Base64Helper.encode(rsaPrivateKey!!.encoded)

  @get:JsonIgnore
  val rsaPublicKeyBase64Byte: ByteArray
    get() = Base64Helper.encodeToByte(rsaPublicKey!!.encoded)

  @get:JsonIgnore
  val rsaPrivateKeyBase64Byte: ByteArray
    get() = Base64Helper.encodeToByte(rsaPrivateKey!!.encoded)
}
