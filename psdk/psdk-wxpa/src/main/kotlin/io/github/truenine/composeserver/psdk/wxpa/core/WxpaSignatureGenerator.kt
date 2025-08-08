package io.github.truenine.composeserver.psdk.wxpa.core

import io.github.truenine.composeserver.datetime
import io.github.truenine.composeserver.iso8601LongUtc
import io.github.truenine.composeserver.psdk.wxpa.exception.WxpaSignatureException
import io.github.truenine.composeserver.psdk.wxpa.model.WxpaSignature
import io.github.truenine.composeserver.psdk.wxpa.properties.WxpaProperties
import io.github.truenine.composeserver.security.crypto.CryptographicKeyManager
import io.github.truenine.composeserver.security.crypto.sha1
import io.github.truenine.composeserver.slf4j

private val log = slf4j<WxpaSignatureGenerator>()

/**
 * # 微信公众号签名生成器
 *
 * 负责生成JSAPI签名和验证服务器配置签名
 *
 * @author TrueNine
 * @since 2025-08-08
 */
class WxpaSignatureGenerator(private val tokenManager: WxpaTokenManager, private val properties: WxpaProperties) {

  /**
   * ## 生成JSAPI签名
   *
   * @param url 当前网页的URL，不包含#及其后面部分
   * @param nonceStr 随机字符串，如果为空则自动生成
   * @param timestamp 时间戳，如果为空则使用当前时间
   * @return JSAPI签名信息
   * @throws WxpaSignatureException 签名生成失败
   */
  fun generateJsapiSignature(url: String, nonceStr: String? = null, timestamp: Long? = null): WxpaSignature {
    try {
      val appId = properties.appId ?: throw WxpaSignatureException("AppId is not configured")
      val cleanUrl = url.split("#")[0] // 移除URL中的锚点部分
      val finalNonceStr = nonceStr ?: CryptographicKeyManager.generateRandomAsciiString(32)
      val finalTimestamp = timestamp ?: datetime.now().iso8601LongUtc

      log.debug("Generating JSAPI signature for URL: {}", cleanUrl)

      val jsapiTicket = tokenManager.getValidJsapiTicket()

      // 按照微信官方文档要求的顺序和格式构建签名字符串
      val signatureParams = mapOf("jsapi_ticket" to jsapiTicket, "noncestr" to finalNonceStr, "timestamp" to finalTimestamp.toString(), "url" to cleanUrl)

      val signatureString =
        signatureParams
          .toSortedMap() // 按key排序
          .map { "${it.key}=${it.value}" }
          .joinToString("&")

      log.debug("Signature string: {}", signatureString)

      val signature = signatureString.sha1

      log.debug("Generated signature: {}", signature)

      return WxpaSignature(appId = appId, nonceStr = finalNonceStr, timestamp = finalTimestamp, url = cleanUrl, signature = signature)
    } catch (e: Exception) {
      log.error("Failed to generate JSAPI signature for URL: {}", url, e)
      when (e) {
        is WxpaSignatureException -> throw e
        else -> throw WxpaSignatureException("Failed to generate JSAPI signature: ${e.message}", e)
      }
    }
  }

  /**
   * ## 验证服务器配置签名
   *
   * 用于微信公众号服务器配置验证
   *
   * @param signature 微信传递的签名
   * @param timestamp 微信传递的时间戳
   * @param nonce 微信传递的随机数
   * @return 验证是否通过
   */
  fun verifyServerSignature(signature: String, timestamp: String, nonce: String): Boolean {
    return try {
      val token = properties.verifyToken
      if (token.isNullOrBlank()) {
        log.warn("Verify token is not configured")
        return false
      }

      log.debug("Verifying server signature with timestamp: {}, nonce: {}", timestamp, nonce)

      val sortedParams = listOf(token, timestamp, nonce).sorted()
      val signatureString = sortedParams.joinToString("")
      val expectedSignature = signatureString.sha1

      val isValid = expectedSignature.equals(signature, ignoreCase = true)

      if (isValid) {
        log.info("Server signature verification passed")
      } else {
        log.warn("Server signature verification failed. Expected: {}, Actual: {}", expectedSignature, signature)
      }

      isValid
    } catch (e: Exception) {
      log.error("Error during server signature verification", e)
      false
    }
  }

  /**
   * ## 生成服务器验证响应
   *
   * @param signature 微信传递的签名
   * @param timestamp 微信传递的时间戳
   * @param nonce 微信传递的随机数
   * @param echostr 微信传递的随机字符串
   * @return 如果验证通过返回echostr，否则返回null
   */
  fun generateServerVerificationResponse(signature: String, timestamp: String, nonce: String, echostr: String): String? {
    return if (verifyServerSignature(signature, timestamp, nonce)) {
      log.info("Server verification successful, returning echostr")
      echostr
    } else {
      log.warn("Server verification failed")
      null
    }
  }
}
