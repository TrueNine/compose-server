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
 * # 微信公众号服务
 *
 * 提供微信公众号相关功能的统一入口
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

  /** ## 服务器配置验证DTO */
  data class ServerVerificationRequest(val signature: String, val timestamp: String, val nonce: String, val echostr: String)

  /**
   * ## 验证微信服务器配置
   *
   * @param request 验证请求参数
   * @return 验证成功返回echostr，失败返回null
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
   * ## 生成JSAPI签名
   *
   * @param url 当前网页的URL
   * @param nonceStr 随机字符串（可选）
   * @param timestamp 时间戳（可选）
   * @return JSAPI签名信息
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
   * ## 通过授权码获取用户信息
   *
   * @param authCode 微信授权码
   * @return 用户信息，获取失败返回null
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
   * ## 获取当前Access Token状态
   *
   * @return Token状态信息
   */
  fun getTokenStatus(): Map<String, Any> {
    return tokenManager.getTokenStatus()
  }

  /** ## 强制刷新所有Token */
  fun forceRefreshTokens() {
    log.info("Force refreshing all tokens")
    tokenManager.forceRefreshAll()
  }

  /**
   * ## 检查用户授权状态
   *
   * @param accessToken 用户授权的access_token
   * @param openId 用户openId
   * @return 授权是否有效
   */
  fun checkUserAuthStatus(accessToken: String, openId: String): Boolean {
    return userInfoService.checkUserAuthStatus(accessToken, openId)
  }
}
