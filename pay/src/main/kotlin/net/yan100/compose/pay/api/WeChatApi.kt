package net.yan100.compose.pay.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

@HttpExchange(url = "https://api.weixin.qq.com/")
interface WeChatApi {

  @GetExchange(value = "sns/jscode2session", accept = ["application/json"])
  fun token(
    @RequestParam appid: String?,
    @RequestParam secret: String?,
    @RequestParam(name = "js_code") jsCode: String?,
    @RequestParam(name = "grant_type") grantType: String?
  ): ResponseEntity<String?>?
}
