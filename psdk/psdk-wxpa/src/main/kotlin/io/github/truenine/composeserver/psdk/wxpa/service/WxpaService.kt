package io.github.truenine.composeserver.psdk.wxpa.service

import io.github.truenine.composeserver.logger
import io.github.truenine.composeserver.psdk.wxpa.core.WxpaSignatureGenerator
import io.github.truenine.composeserver.psdk.wxpa.core.WxpaTokenManager
import io.github.truenine.composeserver.psdk.wxpa.core.WxpaUserInfoService
import io.github.truenine.composeserver.psdk.wxpa.model.WxpaSignature
import io.github.truenine.composeserver.psdk.wxpa.model.WxpaUserInfo
import org.springframework.stereotype.Service

private val log = logger(WxpaService::class)

/**
 * WeChat Official Account service.
 *
 * Provides a unified entry point for features related to WeChat Official Accounts.
 *
 * @author TrueNine
 * @since 2025-08-08
 */
@Service
class WxpaService(
  private val tokenManager: WxpaTokenManager,
  private val signatureGenerator: WxpaSignatureGenerator,
  private val userInfoService: WxpaUserInfoService,
) {

  /** Server configuration verification DTO. */
  data class ServerVerificationRequest(val signature: String, val timestamp: String, val nonce: String, val echostr: String)

  /**
   * Verifies WeChat server configuration.
   *
   * @param request verification request parameters
   * @return echostr on success, or null on failure
   */
  fun verifyServerConfiguration(request: ServerVerificationRequest): String? {
    log.info("Verifying server configuration")

    return try {
      signatureGenerator.generateServerVerificationResponse(
        signature = request.signature,
        timestamp = request.timestamp,
        nonce = request.nonce,
        echostr = request.echostr,
      )
    } catch (e: Exception) {
      log.error("Error during server configuration verification", e)
      null
    }
  }

  /**
   * Generates a JSAPI signature.
   *
   * @param url current page URL
   * @param nonceStr random string (optional)
   * @param timestamp timestamp (optional)
   * @return JSAPI signature information
   */
  fun generateJsapiSignature(url: String, nonceStr: String? = null, timestamp: Long? = null): WxpaSignature? {
    log.info("Generating JSAPI signature for URL: {}", url)

    return try {
      signatureGenerator.generateJsapiSignature(url, nonceStr, timestamp)
    } catch (e: Exception) {
      log.error("Error generating JSAPI signature", e)
      null
    }
  }

  /**
   * Gets user information by authorization code.
   *
   * @param authCode WeChat authorization code
   * @return user information, or null if retrieval fails
   */
  fun getUserInfoByAuthCode(authCode: String): WxpaUserInfo? {
    log.info("Getting user info by auth code")

    return try {
      userInfoService.getUserInfoByAuthCode(authCode)
    } catch (e: Exception) {
      log.error("Error getting user info by auth code", e)
      null
    }
  }

  /**
   * Gets the current access token status.
   *
   * @return token status information
   */
  fun getTokenStatus(): Map<String, Any> {
    return tokenManager.getTokenStatus()
  }

  /** Force refreshes all tokens. */
  fun forceRefreshTokens() {
    log.info("Force refreshing all tokens")
    tokenManager.forceRefreshAll()
  }

  /**
   * Checks user authorization status.
   *
   * @param accessToken user access token
   * @param openId user openId
   * @return true if the authorization is valid, false otherwise
   */
  fun checkUserAuthStatus(accessToken: String, openId: String): Boolean {
    return userInfoService.checkUserAuthStatus(accessToken, openId)
  }
}
