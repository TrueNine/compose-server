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
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.core.long
import net.yan100.compose.core.string
import net.yan100.compose.core.typing.PCB47
import net.yan100.compose.security.oauth2.models.*
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
interface IWxpaApi {
  @Schema(title = "公众号获取 ticket 返回结果")
  class WxpaGetTicketResp : BaseWxpaResp() {
    @Schema(title = "票证")
    var ticket: String? = null
  }

  @Schema(title = "公众号获取 ticket 返回结果")
  class WxpaQuotaResp : BaseWxpaResp() {
    @Schema(title = "当天该账号可调用该接口的次数")
    @JsonProperty("daily_limit")
    var dailyLimit: long? = null

    @Schema(title = "当天已经调用的次数")
    var used: long? = null

    @Schema(title = "当天剩余调用次数")
    var remain: long? = null
  }


  @Schema(title = "公众号获取 access_token 返回结果")
  class WxpaGetAccessTokenResp : BaseWxpaResp() {
    @Schema(title = "公众号 access_token")
    @JsonProperty("access_token")
    var accessToken: String? = null
  }

  @Schema(title = "微信公众号网页授权获取 access_token 响应")
  class WxpaWebsiteAuthGetAccessTokenResp : BaseWxpaResp() {
    @Schema(title = "token")
    @JsonProperty("access_token")
    lateinit var accessToken: String

    @Schema(title = "过期时间")
    @JsonProperty("expires_in")
    var expireIn: Long? = null

    @JsonProperty("refresh_token")
    lateinit var refreshToken: String

    @Schema(title = "获取到的 openId")
    @JsonProperty("openid")
    lateinit var openId: String

    @Schema(title = "当前使用的 scope")
    lateinit var scope: String

    @Schema(title = "是否为快照页模式虚拟账号", description = "只有当用户是快照页模式虚拟账号时返回，值为1")
    @JsonProperty("is_snapshotuser")
    var isSnapshotUser: Int? = null

    @Schema(title = "用户全局 id")
    @JsonProperty("unionid")
    var unionId: String? = null
  }


  /**
   * ## 微信 access_token
   *
   * @author TrueNine
   * @since 2024-03-20
   */
  @Schema(title = "微信用户信息回调结果")
  class WxpaWebsiteUserInfoResp {
    @JsonProperty("openid")
    @Schema(title = "open id")
    lateinit var openId: SerialCode

    @JsonProperty("nickname")
    @Schema(title = "呢称")
    lateinit var nickName: String

    @Schema(title = "微信用户特权", description = "为 json 数组形式")
    var privilege: List<String>? = null

    @Deprecated("过时的接口数据")
    @Schema(title = "头像链接")
    var headimgurl: String? = null

    @Deprecated("过时的接口数据")
    @Schema(title = "城市")
    var country: String? = null

    @Deprecated("过时的接口数据")
    @Schema(title = "诚实", deprecated = true)
    var city: String? = null

    @Deprecated("过时的接口数据")
    @Schema(title = "省份", deprecated = true)
    var province: String? = null

    @Deprecated("过时的接口数据")
    @Schema(title = "性别", deprecated = true)
    var sex: Int? = null

    @JsonProperty("unionid")
    @Schema(title = "union id")
    var unionId: String? = null
  }


  /**
   * ## 通过 openid 获取用户信息
   *
   * @param authAccessToken 特殊的 access_token
   * @param openId 用户 openId
   */
  @GetExchange("sns/userinfo")
  fun getUserInfo(
    @RequestParam("access_token") authAccessToken: String,
    @RequestParam("openid") openId: String,
    @RequestParam("lang") lang: String = PCB47.ZH_CN.underLineValue,
  ): WxpaWebsiteUserInfoResp

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
  fun webpageAuthGetAccessToken(
    @RequestParam("appid") appId: String,
    @RequestParam("secret") wxpaSecret: String,
    @RequestParam("code") code: String,
    @RequestParam("grant_type") grantType: WechatMpGrantTyping = WechatMpGrantTyping.AUTH_CODE,
  ): WxpaWebsiteAuthGetAccessTokenResp

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
  ): WxpaGetAccessTokenResp

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
  fun findApiQuota(@RequestParam("access_token") accessToken: string, @RequestParam("cgi_path") cgiPath: string): WxpaQuotaResp
}
