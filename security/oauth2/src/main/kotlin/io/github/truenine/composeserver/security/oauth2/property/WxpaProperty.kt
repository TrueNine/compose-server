package io.github.truenine.composeserver.security.oauth2.property

import io.github.truenine.composeserver.datetime
import io.github.truenine.composeserver.iso8601LongUtc
import io.github.truenine.composeserver.security.crypto.Keys
import io.github.truenine.composeserver.security.crypto.sha1

/**
 * # 微信公众号属性获取器
 *
 * @author TrueNine
 * @since 2024-01-04
 */
class WxpaProperty {
  data class WxpaSignatureResp(
    var appId: String? = null,
    var nonceString: String? = null,
    var timestamp: Long? = null,
    var url: String? = null,
    var sign: String? = null,
  )

  var fixedExpiredSecond: Long = 700_0L
  var preValidToken: String? = null
  var appId: String? = null
  var appSecret: String? = null

  var accessToken: String? = null
  var jsapiTicket: String? = null

  @JvmOverloads
  fun signature(url: String, nonceString: String = Keys.generateRandomAsciiString(), timestamp: Long = datetime.now().iso8601LongUtc): WxpaSignatureResp {
    val splitUrl = url.split("#")[0]
    val b =
      mutableMapOf("noncestr" to nonceString, "jsapi_ticket" to jsapiTicket, "timestamp" to timestamp.toString(), "url" to splitUrl)
        .map { "${it.key}=${it.value}" }
        .sorted()
        .joinToString("&")
        .sha1

    return WxpaSignatureResp(appId = appId, url = splitUrl, nonceString = nonceString, sign = b, timestamp = timestamp)
  }
}
