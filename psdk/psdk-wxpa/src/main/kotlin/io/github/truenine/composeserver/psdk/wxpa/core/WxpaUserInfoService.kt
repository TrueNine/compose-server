package io.github.truenine.composeserver.psdk.wxpa.core

import io.github.truenine.composeserver.psdk.wxpa.api.IWxpaWebClient
import io.github.truenine.composeserver.psdk.wxpa.model.WxpaUserInfo
import io.github.truenine.composeserver.psdk.wxpa.properties.WxpaProperties
import io.github.truenine.composeserver.slf4j

private val log = slf4j<WxpaUserInfoService>()

/**
 * # 微信公众号用户信息服务
 *
 * 负责获取用户信息相关功能
 *
 * @author TrueNine
 * @since 2025-08-08
 */
class WxpaUserInfoService(private val apiClient: IWxpaWebClient, private val properties: WxpaProperties) {

  /**
   * ## 通过授权码获取用户信息
   *
   * @param authCode 微信授权码
   * @return 用户信息，如果获取失败返回null
   */
  fun getUserInfoByAuthCode(authCode: String): WxpaUserInfo? {
    return try {
      log.info("Getting user info by auth code")

      val appId = properties.appId
      val appSecret = properties.appSecret

      if (appId.isNullOrBlank() || appSecret.isNullOrBlank()) {
        log.error("AppId or AppSecret is not configured")
        return null
      }

      // 第一步：通过code获取access_token
      val tokenResponse = apiClient.getWebsiteAccessToken(appId = appId, wxpaSecret = appSecret, code = authCode)

      if (tokenResponse == null) {
        log.warn("Failed to get access token: response is null")
        return null
      }

      if (tokenResponse.isError) {
        log.warn("Failed to get access token: code={}, message={}", tokenResponse.errorCode, tokenResponse.errorMessage)
        return null
      }

      val accessToken = tokenResponse.accessToken
      val openId = tokenResponse.openId

      if (accessToken.isNullOrBlank() || openId.isNullOrBlank()) {
        log.warn("Access token or openId is null in response")
        return null
      }

      log.debug("Got access token for openId: {}", openId)

      // 第二步：通过access_token获取用户信息
      val userInfoResponse = apiClient.getUserInfoByAccessToken(authAccessToken = accessToken, openId = openId)

      if (userInfoResponse == null) {
        log.warn("Failed to get user info: response is null")
        return null
      }

      val userOpenId = userInfoResponse.openId
      if (userOpenId.isNullOrBlank()) {
        log.warn("OpenId is null in user info response")
        return null
      }

      val userInfo =
        WxpaUserInfo(openId = userOpenId, nickname = userInfoResponse.nickName, privilege = userInfoResponse.privilege, unionId = userInfoResponse.unionId)

      log.info("Successfully got user info for openId: {}", userOpenId)
      return userInfo
    } catch (e: Exception) {
      log.error("Error getting user info by auth code", e)
      null
    }
  }

  /**
   * ## 检查用户授权状态
   *
   * @param accessToken 用户授权的access_token
   * @param openId 用户openId
   * @return 授权是否有效
   */
  fun checkUserAuthStatus(accessToken: String, openId: String): Boolean {
    return try {
      log.debug("Checking user auth status for openId: {}", openId)

      val userInfo = apiClient.getUserInfoByAccessToken(accessToken, openId)
      val isValid = userInfo != null && !userInfo.openId.isNullOrBlank()

      log.debug("User auth status for openId {}: {}", openId, if (isValid) "valid" else "invalid")
      return isValid
    } catch (e: Exception) {
      log.warn("Error checking user auth status for openId: {}", openId, e)
      false
    }
  }
}
