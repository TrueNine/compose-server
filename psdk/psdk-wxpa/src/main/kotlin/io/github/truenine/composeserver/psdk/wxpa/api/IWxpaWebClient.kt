package io.github.truenine.composeserver.psdk.wxpa.api

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.truenine.composeserver.enums.PCB47
import io.github.truenine.composeserver.psdk.wxpa.enums.WechatMpGrantTyping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

/**
 * WeChat Official Account API client.
 *
 * @author TrueNine
 * @since 2024-02-14
 */
@HttpExchange(url = "https://api.weixin.qq.com/")
interface IWxpaWebClient {
  /**
   * JSAPI ticket response for Official Account.
   *
   * @param ticket ticket value
   */
  data class WxpaGetTicketResp(val ticket: String?) : BaseWxpaResponseEntity()

  /**
   * Quota response for a specific API.
   *
   * @param dailyLimit remaining daily invocation limit for this account
   * @param used number of calls already made today
   * @param remain remaining calls available today
   */
  data class WxpaQuotaResp(@JsonProperty("daily_limit") val dailyLimit: Long?, val used: Long?, val remain: Long?) : BaseWxpaResponseEntity()

  /**
   * Official Account access_token response.
   *
   * @param accessToken access_token for the Official Account
   */
  data class WxpaGetAccessTokenResp(@JsonProperty("access_token") val accessToken: String?) : BaseWxpaResponseEntity()

  /**
   * Web authorization access_token response for Official Account.
   *
   * @param accessToken access_token for the Official Account
   * @param expireIn access_token expiration time in seconds
   * @param refreshToken token used to refresh the access_token
   * @param openId obtained user openId
   * @param scope scope of the granted authorization
   * @param isSnapshotUser whether the user is a snapshot-mode virtual account (1 when true)
   * @param unionId user global union id
   */
  data class WxpaWebsiteAuthGetAccessTokenResp(
    @param:JsonProperty("access_token") val accessToken: String?,
    @param:JsonProperty("expires_in") val expireIn: Long?,
    @param:JsonProperty("refresh_token") val refreshToken: String?,
    @param:JsonProperty("openid") val openId: String?,
    val scope: String?,
    @param:JsonProperty("is_snapshotuser") val isSnapshotUser: Int?,
    @param:JsonProperty("unionid") val unionId: String?,
  ) : BaseWxpaResponseEntity()

  /**
   * Web authorization user-info response for a WeChat access_token.
   *
   * @param openId user openId
   * @param nickName user nickname
   * @param privilege WeChat user privileges as a JSON array
   * @param headimgurl avatar URL
   * @param country country name
   * @param city city name
   * @param province province name
   * @param sex gender value
   * @param unionId globally unique user identifier
   * @author TrueNine
   * @since 2024-03-20
   */
  data class WxpaWebsiteUserInfoResp(
    @param:JsonProperty("openid") val openId: String?,
    @param:JsonProperty("nickname") val nickName: String?,
    val privilege: List<String> = emptyList(),
    @Deprecated("Deprecated API field") val headimgurl: String?,
    @Deprecated("Deprecated API field") val country: String?,
    @Deprecated("Deprecated API field") val city: String?,
    @Deprecated("Deprecated API field") val province: String? = null,
    @Deprecated("Deprecated API field") val sex: Int? = null,
    @param:JsonProperty("unionid") val unionId: String?,
  )

  /**
   * Get user information by openId.
   *
   * @param authAccessToken special access_token from web authorization
   * @param openId user openId
   * @param lang language code
   */
  @GetExchange("sns/userinfo")
  fun getUserInfoByAccessToken(
    @RequestParam("access_token") authAccessToken: String,
    @RequestParam("openid") openId: String,
    @RequestParam("lang") lang: String = PCB47.ZH_CN.underLineValue,
  ): WxpaWebsiteUserInfoResp?

  /**
   * Obtain access_token for Official Account web authorization via code.
   * [Documentation](https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html#1)
   *
   * @param appId Official Account appId
   * @param wxpaSecret Official Account secret
   * @param code authorization code granted by the user
   * @param grantType grant type
   */
  @GetExchange("sns/oauth2/access_token")
  fun getWebsiteAccessToken(
    @RequestParam("appid") appId: String,
    @RequestParam("secret") wxpaSecret: String,
    @RequestParam("code") code: String,
    @RequestParam("grant_type") grantType: WechatMpGrantTyping = WechatMpGrantTyping.AUTH_CODE,
  ): WxpaWebsiteAuthGetAccessTokenResp?

  /**
   * Get access_token for an Official Account.
   *
   * @param appId appId
   * @param secret secret
   * @param grantType grant type
   * @return access_token
   */
  @GetExchange("cgi-bin/token")
  fun getAccessToken(
    @RequestParam(name = "appid") appId: String,
    @RequestParam(name = "secret") secret: String,
    @RequestParam(name = "grant_type") grantType: WechatMpGrantTyping = WechatMpGrantTyping.CLIENT_CREDENTIAL,
  ): WxpaGetAccessTokenResp?

  /**
   * Get JSAPI ticket.
   *
   * @param accessToken access_token
   * @param type ticket type, defaults to "jsapi"
   */
  @GetExchange("cgi-bin/ticket/getticket")
  fun getTicket(@RequestParam(name = "access_token") accessToken: String, @RequestParam(name = "type") type: String = "jsapi"): WxpaGetTicketResp?

  /**
   * Get the daily API quota for an Official Account.
   *
   * @param accessToken access_token
   * @param cgiPath API path being queried
   */
  @GetExchange("cgi-bin/openapi/quota/get")
  fun getApiQuota(@RequestParam("access_token") accessToken: String, @RequestParam("cgi_path") cgiPath: String): WxpaQuotaResp?
}

/** Base response entity for WeChat Official Account APIs. */
abstract class BaseWxpaResponseEntity {
  /** Error code. */
  @JsonProperty("errcode") var errorCode: Int? = null

  /** Error message. */
  @JsonProperty("errmsg") var errorMessage: String? = null

  /**
   * Expiration time limit (how long until the response expires).
   * > Value is expressed in seconds.
   */
  @JsonProperty("expires_in") var expireInSecond: Long? = null

  /** Whether this is an error response. */
  @get:JsonIgnore
  val isError: Boolean
    get() = errorCode != null && errorCode != 0
}
