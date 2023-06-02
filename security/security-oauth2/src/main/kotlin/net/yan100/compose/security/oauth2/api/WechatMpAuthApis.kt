package net.yan100.compose.security.oauth2.api

import net.yan100.compose.core.typing.wechat.WechatMpGrantTyping
import net.yan100.compose.security.oauth2.models.api.JsCodeToSessionApiReq
import net.yan100.compose.security.oauth2.models.api.JsCodeToSessionApiResp
import net.yan100.compose.security.oauth2.models.api.JsCodeToSessionResp
import net.yan100.compose.security.oauth2.models.api.toStandard
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

/**
 * # 微信小程序认证授权
 * @author TrueNine
 * @since 2023-05-31
 */
@HttpExchange(url = "https://api.weixin.qq.com/")
interface WechatMpAuthApi {

  /**
   * # 小程序登录
   *
   * @param appId appId
   * @param secret secret
   * @param jsCode 验证令牌
   * @param grantType 验证类型
   */
  @ResponseBody
  @GetExchange(value = "sns/jscode2session", accept = ["application/json", "text/plain"])
  fun jsCodeToSession(
    @RequestParam appId: String,
    @RequestParam secret: String,
    @RequestParam(name = "js_code") jsCode: String,
    @RequestParam(name = "grant_type") grantType: WechatMpGrantTyping = WechatMpGrantTyping.AUTH_CODE
  ): JsCodeToSessionApiResp?
}

fun WechatMpAuthApi.jsCodeToSessionStandard(param: JsCodeToSessionApiReq): JsCodeToSessionResp? = this.jsCodeToSession(
  appId = param.mpAppId,
  secret = param.mpSecret,
  jsCode = param.jsCode,
  grantType = WechatMpGrantTyping.AUTH_CODE
)?.toStandard()

