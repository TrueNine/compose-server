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
package net.yan100.compose.security.oauth2.schedule

import net.yan100.compose.core.exceptions.RemoteCallException
import net.yan100.compose.core.slf4j
import net.yan100.compose.security.oauth2.api.IWxpaApi
import net.yan100.compose.security.oauth2.property.WxpaProperty
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

private val log = slf4j(GetAccessTokenSchedule::class)

/**
 * 微信公众号 access_token 定时调度器
 *
 * @author TrueNine
 * @since 2024-01-03
 */
@EnableScheduling
@EnableAsync
@Component
class GetAccessTokenSchedule(private val ctx: ApplicationContext, @Lazy private val api: IWxpaApi) {
  init {
    log.trace("注册微信公众号 access_token 调度器")
  }

  @Scheduled(initialDelay = 1000, fixedRate = 7000 * 1000)
  fun getAccessToken() {
    val pp = ctx.getBean(WxpaProperty::class.java)

    log.trace("准备更新 access_token appid = {},secret = {}", pp.appId, pp.appSecret)
    val ae = api.getAccessToken(pp.appId, pp.appSecret)
    if (ae.accessToken == null) {
      log.error("未兑换到 access_token code: {}, message: {}", ae.errorCode, ae.errorMessage)
      throw RemoteCallException("未兑换到 access_token")
    }
    if (ae.isError) {
      log.error("换取 access_token 时 发生错误 code: {}, message: {}", ae.errorCode, ae.errorMessage)
      throw RemoteCallException("换取 access_token 时 发生错误")
    }
    val t = api.getTicket(ae.accessToken!!)
    if (t.isError) {
      log.error("换取 ticket 时 发生错误 code: {}, message: {}", ae.errorCode, ae.errorMessage)
      throw RemoteCallException("换取 ticket 时 发生错误")
    }
    log.trace("获取到 access_token: mask, exp: mask")
    log.trace("获取到 ticket: mask, exp: mask")

    checkNotNull(ae.expireInSecond) { "微信服务器返回了空的 access_token 时间戳" }
    checkNotNull(t.expireInSecond) { "微信服务器返回了空的 ticket 时间戳" }

    if (ae.expireInSecond!! >= pp.fixedExpiredSecond) {
      pp.accessToken = ae.accessToken
    } else throw RemoteCallException("获取 access_token 时 微信公众号返回了一个大于 ${pp.fixedExpiredSecond} 的时间戳 ${ae.expireInSecond}")

    if (t.expireInSecond!! >= pp.fixedExpiredSecond) {
      pp.jsapiTicket = t.ticket
    } else throw RemoteCallException("获取 ticket 时 微信公众号返回了一个 大于 ${pp.fixedExpiredSecond} 的时间戳 ${t.expireInSecond}")
  }
}
