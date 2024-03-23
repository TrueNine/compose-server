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

import net.yan100.compose.core.alias.string
import net.yan100.compose.core.typing.PCB47
import net.yan100.compose.security.oauth2.models.*
import net.yan100.compose.security.oauth2.typing.wechat.WechatMpGrantTyping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

/**
 * # 微信小程序认证授权
 *
 * @author TrueNine
 * @since 2023-05-31
 */
@HttpExchange(url = "https://api.weixin.qq.com/")
interface IWxMpApi {

  /**
   * ## 小程序登录
   *
   * @param appId appId
   * @param secret secret
   * @param jsCode 验证令牌
   * @param grantType 验证类型
   */
  @ResponseBody
  @GetExchange(value = "sns/jscode2session", accept = ["application/json", "text/plain"])
  fun jsCodeToSession(
    @RequestParam(name = "appid") appId: String,
    @RequestParam(name = "secret") secret: String,
    @RequestParam(name = "js_code") jsCode: String,
    @RequestParam(name = "grant_type") grantType: WechatMpGrantTyping = WechatMpGrantTyping.AUTH_CODE,
  ): WxMpJsCodeToSessionResp
}

/**
 * # 微信公众号 API
 *
 * @author TrueNine
 * @since 2024-02-14
 */
@HttpExchange(url = "https://api.weixin.qq.com/")
interface IWxpaApi {
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
  fun getTicket(
    @RequestParam(name = "access_token") accessToken: String,
    @RequestParam(name = "type") type: String = "jsapi",
  ): WxpaGetTicketResp

  /**
   * ## 公众号每天的调用次数
   *
   * @param accessToken access_token
   * @param cgiPath 调用路径
   */
  @GetExchange("cgi-bin/openapi/quota/get")
  fun findApiQuota(
    @RequestParam("access_token") accessToken: string,
    @RequestParam("cgi_path") cgiPath: string,
  ): WxpaQuotaResp
}

fun IWxMpApi.jsCodeToSessionStandard(param: JsCodeToSessionApiReq): JsCodeToSessionResp =
  jsCodeToSession(appId = param.mpAppId, secret = param.mpSecret, jsCode = param.jsCode, grantType = WechatMpGrantTyping.AUTH_CODE).toStandard()
