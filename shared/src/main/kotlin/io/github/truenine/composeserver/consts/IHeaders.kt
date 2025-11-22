package io.github.truenine.composeserver.consts

import io.github.truenine.composeserver.hasText
import jakarta.servlet.http.HttpServletRequest
import java.net.URLEncoder
import java.nio.charset.Charset

/**
 * http Header Info
 *
 * @author TrueNine
 * @since 2022-10-28
 */
interface IHeaders {
  companion object {
    /**
     * Set Content-Disposition download filename. Example: `Content-Disposition: attachment; filename="filename"`.
     *
     * @param fileName File name
     * @return attachment; filename="fileName"
     */
    fun downloadDisposition(fileName: String, charset: Charset): String {
      return "attachment; filename=" + URLEncoder.encode(fileName, charset)
    }

    /**
     * Get client device identifier, preferring [IHeaders].DEVICE_ID, then [IHeaders].USER_AGENT.
     *
     * @param request HTTP request
     * @return Device identifier, or null if unavailable
     */
    fun getDeviceId(request: HttpServletRequest): String? {
      val deviceId = request.getHeader(X_DEVICE_ID)
      val userAgent = request.getHeader(USER_AGENT)
      return if (deviceId.hasText()) deviceId else if (userAgent.hasText()) userAgent else null
    }

    const val SERVER: String = "Server"
    const val ACCEPT: String = "Accept"
    const val ACCEPT_ENCODING: String = "Accept-Encoding"
    const val ACCEPT_LANGUAGE: String = "Accept-Language"
    const val COOKIE: String = "Cookie"
    const val HOST: String = "Host"
    const val REFERER: String = "Referer"
    const val USER_AGENT: String = "User-Agent"
    const val X_FORWARDED_FOR: String = "X-Forwarded-For"
    const val X_FORWARDED_PROTO: String = "X-Forwarded-Proto"
    const val PROXY_CLIENT_IP: String = "Proxy-Client-IP"

    /**
     * <h2>Real client IP address header</h2>
     *
     * Typically set by reverse proxies such as nginx.
     */
    const val X_REAL_IP: String = "X-Real-IP"

    /** Device identifier header */
    const val X_DEVICE_ID: String = "X-Device-Id"

    const val AUTHORIZATION: String = "Authorization"

    /** Custom refresh header */
    const val X_REFRESH: String = "X-Refresh"

    /** Indicates that expired authentication tokens should be cleared */
    const val X_REQUIRE_CLEN_AUTHENTICATION: String = "X-Require-Clean-Authentication"

    /** WeChat OpenID authorization custom identifier */
    const val X_WECHAT_AUTHORIZATION_ID: String = "X-Wechat-Authorization-Id"

    const val CONTENT_LENGTH: String = "Content-Length"
    const val CONTENT_TYPE: String = "Content-Type"
    const val CONNECTION: String = "Connection"
    const val CONTENT_DISPOSITION: String = "Content-Disposition"
    const val KEEP_ALIVE: String = "Keep-Alive"

    const val CORS_ALLOW_ORIGIN: String = "Access-Control-Allow-Origin"
    const val CORS_ALLOW_METHODS: String = "Access-Control-Allow-Methods"
    const val CORS_ALLOW_HEADERS: String = "Access-Control-Allow-Headers"
    const val CORS_ALLOW_CREDENTIALS: String = "Access-Control-Allow-Credentials"
  }
}
