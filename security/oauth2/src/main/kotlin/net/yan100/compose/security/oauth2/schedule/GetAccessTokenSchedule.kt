package net.yan100.compose.security.oauth2.schedule

import net.yan100.compose.security.oauth2.api.IWxpaWebClient
import net.yan100.compose.security.oauth2.property.WxpaProperty
import net.yan100.compose.slf4j
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
@Component
@EnableAsync
@EnableScheduling
class GetAccessTokenSchedule(
  private val ctx: ApplicationContext,
  @Lazy private val api: IWxpaWebClient,
) {
  init {
    log.trace("注册微信公众号 access_token 调度器")
  }

  @Scheduled(initialDelay = 1000, fixedRate = 7000 * 1000)
  fun getAccessToken() {
    val pp = ctx.getBean(WxpaProperty::class.java)
    checkNotNull(pp.appId) { "微信公众号 access_token 获取失败，appId 为空" }
    checkNotNull(pp.appSecret) { "微信公众号 access_token 获取失败，appSecret 为空" }
    log.trace(
      "ready update access_token appid = {},secret = {}",
      pp.appId,
      pp.appSecret,
    )
    val ae = api.getAccessToken(pp.appId!!, pp.appSecret!!)
    checkNotNull(ae) { "微信公众号 access_token 获取失败" }
    require(!ae.isError) { "微信公众号 access_token 获取失败，返回错误码 ${ae.errorCode}" }
    if (ae.accessToken == null) {
      log.error(
        "未兑换到 access_token code: {}, message: {}",
        ae.errorCode,
        ae.errorMessage,
      )
      error("未兑换到 access_token")
    }
    if (ae.isError) {
      log.error(
        "换取 access_token 时 发生错误 code: {}, message: {}",
        ae.errorCode,
        ae.errorMessage,
      )
      error("换取 access_token 时 发生错误")
    }
    val t = api.getTicket(ae.accessToken)
    if (t.isError) {
      log.error(
        "换取 ticket 时 发生错误 code: {}, message: {}",
        ae.errorCode,
        ae.errorMessage,
      )
      error("换取 ticket 时 发生错误")
    }
    log.trace("获取到 access_token: mask, exp: mask")
    log.trace("获取到 ticket: mask, exp: mask")

    checkNotNull(ae.expireInSecond) { "微信服务器返回了空的 时间戳" }
    checkNotNull(t.expireInSecond) { "微信服务器返回了空的 ticket 时间戳" }

    if (ae.expireInSecond!! >= pp.fixedExpiredSecond) {
      pp.accessToken = ae.accessToken
    } else
      error(
        "获取 access_token 时 微信公众号返回了一个大于 ${pp.fixedExpiredSecond} 的时间戳 ${ae.expireInSecond}"
      )

    if (t.expireInSecond!! >= pp.fixedExpiredSecond) {
      pp.jsapiTicket = t.ticket
    } else
      error(
        "获取 ticket 时 微信公众号返回了一个 大于 ${pp.fixedExpiredSecond} 的时间戳 ${t.expireInSecond}"
      )
  }
}
