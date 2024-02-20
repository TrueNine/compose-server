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
package net.yan100.compose.security.oauth2.property

import net.yan100.compose.core.encrypt.Keys
import net.yan100.compose.core.encrypt.sha1
import net.yan100.compose.core.lang.iso8601LongUtc
import java.time.LocalDateTime

/**
 * 微信公众号属性获取器
 *
 * @author TrueNine
 * @since 2024-01-04
 */
class WxpaProperty {
  var fixedExpiredSecond: Long = 700_0L
  lateinit var preValidToken: String
  lateinit var appId: String
  lateinit var appSecret: String

  var accessToken: String? = null
  var jsapiTicket: String? = null

  @JvmOverloads
  fun signature(
    url: String,
    nonceString: String = Keys.generateRandomAsciiString(),
    timestamp: Long = LocalDateTime.now().iso8601LongUtc
  ): WxpaSignatureResp {
    val splitUrl = url.split("#")[0]

    val b =
      mutableMapOf(
          "noncestr" to nonceString,
          "jsapi_ticket" to jsapiTicket,
          "timestamp" to timestamp.toString(),
          "url" to splitUrl
        )
        .map { "${it.key}=${it.value}" }
        .sorted()
        .joinToString("&")
        .sha1

    return WxpaSignatureResp().also {
      it.appId = appId
      it.url = splitUrl
      it.nonceString = nonceString
      it.sign = b
      it.timestamp = timestamp
    }
  }
}

open class WxpaSignatureResp {
  open var appId: String? = null
  open var nonceString: String? = null
  open var timestamp: Long? = null
  open var url: String? = null
  open var sign: String? = null
}
