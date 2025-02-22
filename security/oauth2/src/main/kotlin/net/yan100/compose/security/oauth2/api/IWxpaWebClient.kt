/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.security.oauth2.api

import com.fasterxml.jackson.annotation.JsonProperty
import net.yan100.compose.core.long
import net.yan100.compose.core.string
import net.yan100.compose.core.typing.PCB47
import net.yan100.compose.security.oauth2.models.BaseWxpaResponseEntity
import net.yan100.compose.security.oauth2.typing.WechatMpGrantTyping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange


/**
 * # 微信公众号 API
 *
 * @author TrueNine
 * @since 2024-02-14
 */
@HttpExchange(url = "https://api.weixin.qq.com/")
interface IWxpaWebClient {
  /**
   * ## 公众号获取 ticket 返回结果
   * @param ticket 票证
   */
  data class WxpaGetTicketResp(
    val ticket: String?
  ) : BaseWxpaResponseEntity()

  /**
   * ## 公众号获取 ticket 返回结果
   *
   * @param dailyLimit 当天该账号可调用该接口的次数
   * @param used 当天已经调用的次数
   * @param remain 当天剩余调用次数
   */
  data class WxpaQuotaResp(
    @JsonProperty("daily_limit")
    val dailyLimit: long?,
    val used: long?,
    val remain: long?,
  ) : BaseWxpaResponseEntity()

  /**
   * # 公众号获取 access_token 返回结果
   * @param accessToken 公众号 access_token
   */
  data class WxpaGetAccessTokenResp(
    @JsonProperty("access_token")
    val accessToken: String?
  ) : BaseWxpaResponseEntity()

  /**
   * # 微信公众号网页授权获取 access_token 响应
   * @param accessToken 公众号 access_token
   * @param expireIn access_token 过期时间
   * @param refreshToken 刷新 access_token 的 token
   * @param openId 获取到的 openId
   * @param scope 当前使用的 scope 授权范围
   * @param isSnapshotUser 是否为快照页模式虚拟账号 ，只有当用户是快照页模式虚拟账号时返回，值为1
   * @param unionId 用户全局 id
   */
  class WxpaWebsiteAuthGetAccessTokenResp(
    @JsonProperty("access_token")
    val accessToken: String?,
    @JsonProperty("expires_in")
    val expireIn: Long?,
    @JsonProperty("refresh_token")
    val refreshToken: String?,
    @JsonProperty("openid")
    val openId: String?,
    val scope: String?,
    @JsonProperty("is_snapshotuser")
    val isSnapshotUser: Int?,
    @JsonProperty("unionid")
    val unionId: String?,
  ) : BaseWxpaResponseEntity()


  /**
   * ## 微信 access_token 微信用户信息回调结果
   *
   * @param openId openId
   * @param nickName 呢称
   * @param privilege 微信用户特权 为 json 数组形式
   * @param headimgurl 头像链接
   * @param country 国家
   * @param city 城市
   * @param province 省份
   * @param sex 性别
   * @param unionId 全局唯一标识
   * @author TrueNine
   * @since 2024-03-20
   */
  data class WxpaWebsiteUserInfoResp(
    @JsonProperty("openid")
    val openId: string?,
    @JsonProperty("nickname")
    val nickName: String?,
    val privilege: List<String> = emptyList(),
    @Deprecated("过时的接口数据")
    val headimgurl: String?,
    @Deprecated("过时的接口数据")
    val country: String?,
    @Deprecated("过时的接口数据")
    val city: String?,
    @Deprecated("过时的接口数据")
    val province: String? = null,
    @Deprecated("过时的接口数据")
    val sex: Int? = null,
    @JsonProperty("unionid")
    val unionId: String?
  )

  /**
   * ## 通过 openid 获取用户信息
   *
   * @param authAccessToken 特殊的 access_token
   * @param openId 用户 openId
   * @param lang 语言
   */
  @GetExchange("sns/userinfo")
  fun getUserInfoByAccessToken(
    @RequestParam("access_token") authAccessToken: String,
    @RequestParam("openid") openId: String,
    @RequestParam("lang") lang: String = PCB47.ZH_CN.underLineValue,
  ): WxpaWebsiteUserInfoResp?

  /**
   * ## 公众号网页授权 通过 code 获取 access_token
   * [文档](https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html#1)
   *
   * @param appId 公众号 appId
   * @param wxpaSecret 公众号 secret
   * @param code 当前用户授权给予的令牌
   * @param grantType 验证类型
   */
  @GetExchange("sns/oauth2/access_token")
  fun getWebsiteAccessToken(
    @RequestParam("appid") appId: String,
    @RequestParam("secret") wxpaSecret: String,
    @RequestParam("code") code: String,
    @RequestParam("grant_type") grantType: WechatMpGrantTyping = WechatMpGrantTyping.AUTH_CODE,
  ): WxpaWebsiteAuthGetAccessTokenResp?

  /**
   * ## 公众号 获取 access_token
   *
   * @param appId appId
   * @param secret secret
   * @param grantType 验证类型
   * @return access_token
   */
  @GetExchange("cgi-bin/token")
  fun getAccessToken(
    @RequestParam(name = "appid") appId: String,
    @RequestParam(name = "secret") secret: String,
    @RequestParam(name = "grant_type") grantType: WechatMpGrantTyping = WechatMpGrantTyping.CLIENT_CREDENTIAL,
  ): WxpaGetAccessTokenResp?

  /**
   * ## jsapi 获取票证
   *
   * @param accessToken access_token
   * @param type 无需填写
   */
  @GetExchange("cgi-bin/ticket/getticket")
  fun getTicket(@RequestParam(name = "access_token") accessToken: String, @RequestParam(name = "type") type: String = "jsapi"): WxpaGetTicketResp

  /**
   * ## 公众号每天的调用次数
   *
   * @param accessToken access_token
   * @param cgiPath 调用路径
   */
  @GetExchange("cgi-bin/openapi/quota/get")
  fun getApiQuota(@RequestParam("access_token") accessToken: string, @RequestParam("cgi_path") cgiPath: string): WxpaQuotaResp
}
