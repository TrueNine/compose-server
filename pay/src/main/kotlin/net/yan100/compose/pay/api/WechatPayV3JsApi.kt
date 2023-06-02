package net.yan100.compose.pay.api

import net.yan100.compose.core.typing.wechat.WechatPayGrantTyping
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

/**
 * # 微信支付 js API
 *
 * @author shanghua
 * @since 2023-05-31
 */
@HttpExchange(url = "https://api.weixin.qq.com/")
interface WechatPayV3JsApi {

  /**
   * # 获取微信用户 id
   * @param appid appId
   * @param secret secret
   * @param jsCode 验证令牌
   * @param grantType 验证类型
   */
  @GetExchange(value = "sns/jscode2session", accept = ["application/json"])
  fun findUserToken(
    @RequestParam appid: String?,
    @RequestParam secret: String?,
    @RequestParam(name = "js_code") jsCode: String?,
    @RequestParam(name = "grant_type") grantType: WechatPayGrantTyping? = WechatPayGrantTyping.AUTH_CODE
  ): ResponseEntity<String?>
}
