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
package net.yan100.compose.core.models

import com.fasterxml.jackson.annotation.JsonIgnore
import net.yan100.compose.core.encrypt.Base64Helper
import java.security.PrivateKey
import java.security.PublicKey

/**
 * ecc 密钥对
 *
 * @author TrueNine
 * @since 2022-12-15
 */
class EccKeyPair {
  @JsonIgnore var eccPublicKey: PublicKey? = null

  @JsonIgnore var eccPrivateKey: PrivateKey? = null
  val eccPublicKeyBase64: String
    get() = Base64Helper.encode(eccPublicKey!!.encoded)

  val eccPrivateKeyBase64: String
    get() = Base64Helper.encode(eccPrivateKey!!.encoded)

  @get:JsonIgnore
  val eccPublicKeyBase64Byte: ByteArray
    get() = Base64Helper.encodeToByte(eccPublicKey!!.encoded)

  @get:JsonIgnore
  val eccPrivateKeyBase64Byte: ByteArray
    get() = Base64Helper.encodeToByte(eccPrivateKey!!.encoded)
}
