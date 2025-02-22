package net.yan100.compose.security.oauth2.service

import net.yan100.compose.core.slf4j
import net.yan100.compose.security.oauth2.api.IWxpaWebClient
import net.yan100.compose.security.oauth2.property.WxpaProperty
import org.springframework.stereotype.Service

private val log = slf4j<WxpaService>()

/**
 * # 微信公众号 服务
 *
 * @author TrueNine
 * @since 2025-02-23
 */
@Service
class WxpaService(
  private val client: IWxpaWebClient,
  private val property: WxpaProperty
) {
  /**
   * ## 获取公众号用户信息
   * @param jsCode jsCode
   */
  fun fetchUserInfoByAccessToken(jsCode: String): IWxpaWebClient.WxpaWebsiteUserInfoResp? {
    if (property.appId == null) {
      log.warn("appId is null")
      return null
    }
    if (property.appSecret == null) {
      log.warn("appSecret is null")
      return null
    }
    val codeToken = client.getWebsiteAccessToken(
      property.appId!!,
      property.appSecret!!,
      jsCode
    )
    if (null == codeToken) return null
    if (!codeToken.isError) {
      log.warn("codeToken is error, token: {}", codeToken)
      return null
    }
    if (codeToken.accessToken == null) {
      log.warn("codeToken.accessToken is null, token: {}", codeToken)
      return null
    }
    if (codeToken.openId == null) {
      log.warn("codeToken.openId is null, token: {}", codeToken)
      return null
    }
    val userInfo = client.getUserInfoByAccessToken(codeToken.accessToken, codeToken.openId)
    if (userInfo?.openId == null) {
      log.warn("userInfo is null, token: {}", codeToken)
      return null
    } else {
      return userInfo
    }
  }
}
