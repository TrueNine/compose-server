package io.github.truenine.composeserver.psdk.wxpa.core

import io.github.truenine.composeserver.*
import io.github.truenine.composeserver.psdk.wxpa.exception.WxpaSignatureException
import io.github.truenine.composeserver.psdk.wxpa.model.WxpaSignature
import io.github.truenine.composeserver.psdk.wxpa.properties.WxpaProperties
import io.github.truenine.composeserver.security.crypto.CryptographicKeyManager
import io.github.truenine.composeserver.security.crypto.sha1

private val log = slf4j<WxpaSignatureGenerator>()

/**
 * WeChat Official Account signature generator.
 *
 * Responsible for generating JSAPI signatures and verifying server configuration signatures.
 *
 * @author TrueNine
 * @since 2025-08-08
 */
class WxpaSignatureGenerator(private val tokenManager: WxpaTokenManager, private val properties: WxpaProperties) {

  /**
   * Generate JSAPI signature.
   *
   * @param url current page URL, without the hash fragment
   * @param nonceStr random string; auto-generated when null
   * @param timestamp timestamp; uses the current time when null
   * @return JSAPI signature information
   * @throws WxpaSignatureException when signature generation fails
   */
  fun generateJsapiSignature(url: String, nonceStr: String? = null, timestamp: Long? = null): WxpaSignature {
    try {
      val appId = properties.appId ?: throw WxpaSignatureException("AppId is not configured")
      // Remove hash fragment from URL
      val cleanUrl = url.split("#")[0]
      val finalNonceStr = nonceStr ?: CryptographicKeyManager.generateRandomAsciiString(32)
      val finalTimestamp = timestamp ?: datetime.now().iso8601LongUtc

      log.debug("Generating JSAPI signature for URL: {}", cleanUrl)

      val jsapiTicket = tokenManager.getValidJsapiTicket()

      // Build the signature string according to the official WeChat documentation
      val signatureParams = mapOf("jsapi_ticket" to jsapiTicket, "noncestr" to finalNonceStr, "timestamp" to finalTimestamp.toString(), "url" to cleanUrl)

      val signatureString =
        signatureParams
          // Sort parameters by key
          .toSortedMap()
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
   * Verify server configuration signature.
   *
   * Used for WeChat Official Account server configuration verification.
   *
   * @param signature signature provided by WeChat
   * @param timestamp timestamp provided by WeChat
   * @param nonce random nonce provided by WeChat
   * @return whether the verification passes
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
   * Generate server verification response.
   *
   * @param signature signature provided by WeChat
   * @param timestamp timestamp provided by WeChat
   * @param nonce random nonce provided by WeChat
   * @param echostr random string provided by WeChat
   * @return echostr when verification passes, otherwise null
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
