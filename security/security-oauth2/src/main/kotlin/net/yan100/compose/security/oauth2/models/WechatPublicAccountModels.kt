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
package net.yan100.compose.security.oauth2.models

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.long

/**
 * ## 微信公众号开发者接入指南
 *
 * 开发者通过检验signature对请求进行校验（下面有校验方式）。若确认此次GET请求来自微信服务器，请原样返回echostr参数内容，则接入生效，成为开发者成功，否则接入失败。加密/校验流程如下：
 * 1. 将token、timestamp、nonce三个参数进行字典序排序
 * 2. 将三个参数字符串拼接成一个字符串进行sha1加密
 * 3. 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
 */
@Schema(title = "公众号验证请求参数")
class WxpaVerifyModel {
  /** 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。 */
  @Schema(title = "微信加密签名", description = "signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数")
  var signature: String? = null

  /** 时间戳 */
  @Schema(title = "时间戳") var timestamp: Long? = null

  /** 随机数 */
  @Schema(title = "随机数") var nonce: String? = null

  /** 随机字符串 */
  @Schema(title = "随机字符串") var echostr: String? = null
}

@Schema(title = "公众号获取 access_token 返回结果")
class WxpaGetAccessTokenResp : BaseWxpaResp() {
  @Schema(title = "公众号 access_token") @JsonProperty("access_token") var accessToken: String? = null
}

@Schema(title = "公众号获取 ticket 返回结果")
class WxpaGetTicketResp : BaseWxpaResp() {
  @Schema(title = "票证") var ticket: String? = null
}

@Schema(title = "公众号获取 ticket 返回结果")
class WxpaQuotaResp : BaseWxpaResp() {
  @Schema(title = "当天该账号可调用该接口的次数") @JsonProperty("daily_limit") var dailyLimit: long? = null

  @Schema(title = "当天已经调用的次数") var used: long? = null

  @Schema(title = "当天剩余调用次数") var remain: long? = null
}

@Schema(title = "微信公众号网页授权获取 access_token 响应")
class WxpaWebsiteAuthGetAccessTokenResp : BaseWxpaResp() {
  @Schema(title = "token") @JsonProperty("access_token") lateinit var accessToken: String

  @Schema(title = "过期时间") @JsonProperty("expires_in") var expireIn: Long? = null

  @JsonProperty("refresh_token") lateinit var refreshToken: String

  @Schema(title = "获取到的 openId") @JsonProperty("openid") lateinit var openId: String

  @Schema(title = "当前使用的 scope") lateinit var scope: String

  @Schema(title = "是否为快照页模式虚拟账号", description = "只有当用户是快照页模式虚拟账号时返回，值为1")
  @JsonProperty("is_snapshotuser")
  var isSnapshotUser: Int? = null

  @Schema(title = "用户全局 id") @JsonProperty("unionid") var unionId: String? = null
}

/**
 * ## 微信 access_token
 *
 * @author TrueNine
 * @since 2024-03-20
 */
@Schema(title = "微信用户信息回调结果")
class WxpaWebsiteUserInfoResp {
  @JsonProperty("openid") @Schema(title = "open id") lateinit var openId: RefId

  @JsonProperty("nickname") @Schema(title = "呢称") lateinit var nickName: String

  @Schema(title = "微信用户特权", description = "为 json 数组形式") var privilege: List<String>? = null

  @Deprecated("过时的接口数据") @Schema(title = "头像链接") var headimgurl: String? = null

  @Deprecated("过时的接口数据") @Schema(title = "城市") var country: String? = null

  @Deprecated("过时的接口数据") @Schema(title = "诚实", deprecated = true) var city: String? = null

  @Deprecated("过时的接口数据") @Schema(title = "省份", deprecated = true) var province: String? = null

  @Deprecated("过时的接口数据") @Schema(title = "性别", deprecated = true) var sex: Int? = null

  @JsonProperty("unionid") @Schema(title = "union id") var unionId: String? = null
}
