package net.yan100.compose.security.oauth2.service

import net.yan100.compose.core.hasText
import net.yan100.compose.core.slf4j
import net.yan100.compose.security.crypto.sha1
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
  data class WxpaVerifyDto(
    var preValidToken: String,
    var timestamp: Int,
    var nonce: String,
    var signature: String,
    var echostr: String
  )

  /**
   * ## 公众号配置信息
   */
  val wxpaConfigInfo get() = property

  /**
   * ## 验证微信公众平台的基本配置信息
   * > 如果验证失败则抛出异常。
   *
   * @param body 包含微信公众平台验证请求的DTO对象
   * @return 返回验证成功后的响应字符串
   * @throws IllegalStateException 如果验证失败则抛出异常
   */
  fun verifyBasicConfigOrThrow(
    body: WxpaVerifyDto
  ): String {
    return verifyBasicConfig(body) ?: error("verifyBasicConfig failed")
  }

  /**
   * ## 验证微信公众平台的基本配置信息
   * > 如果验证失败则返回 null
   *
   * @param body 包含微信公众平台验证请求的DTO对象
   * @return 返回验证成功后的响应字符串
   */
  fun verifyBasicConfig(
    body: WxpaVerifyDto
  ): String? {
    val a = listOf(wxpaConfigInfo.preValidToken!!, body.timestamp.toString(), body.nonce).sorted().joinToString("").sha1
    log.trace("gen sig = {}, body.sign = {}", a, body.signature)
    return body.echostr
  }

  /**
   * ## 为 js api 签名
   *
   * @param encodedUrl 编码后的 url
   * @param nonceString 随机字符串
   */
  fun fetchJsApiSignature(
    encodedUrl: String,
    nonceString: String?
  ): WxpaProperty.WxpaSignatureResp {
    return if (nonceString.hasText()) wxpaConfigInfo.signature(encodedUrl, nonceString) else wxpaConfigInfo.signature(encodedUrl)
  }

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
