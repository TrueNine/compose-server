package io.tnt.controller


import net.yan100.compose.core.id.BizCodeGenerator
import net.yan100.compose.core.lang.ISO4217
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.pay.models.req.FindPayOrderReq
import net.yan100.compose.pay.models.req.CreateMpPayOrderReq
import net.yan100.compose.pay.models.resp.FindPayOrderResp
import net.yan100.compose.pay.models.resp.CreateMpPayOrderResp
import net.yan100.compose.pay.properties.WeChatPaySingleConfigProperty
import net.yan100.compose.pay.service.SinglePayService
import net.yan100.compose.security.oauth2.api.WechatMpAuthApi
import net.yan100.compose.security.oauth2.api.jsCodeToSessionStandard
import net.yan100.compose.security.oauth2.models.api.JsCodeToSessionApiReq
import net.yan100.compose.security.oauth2.models.api.JsCodeToSessionResp
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping("v1/pay/single/wechat")
class WeChatController(
  private val wechatMpAuthApi: WechatMpAuthApi,
  private val payProperty: WeChatPaySingleConfigProperty,
  private val payService: SinglePayService,
  private val bizCodeGenerator: BizCodeGenerator
) {
  private val log = slf4j(this::class)

  @GetMapping("userInfo")
  fun getUserInfo(code: String): JsCodeToSessionResp? {
    val uInfo = wechatMpAuthApi.jsCodeToSessionStandard(JsCodeToSessionApiReq().apply {
      mpAppId = payProperty.mpAppId
      mpSecret = payProperty.apiSecret
      jsCode = code
    })

    log.info("获取到 uInfo = {}", uInfo)
    return uInfo
  }

  @PostMapping("pullUpPayOrder")
  fun pullUpPayOrder(openId: String): CreateMpPayOrderResp? {
    // TODO 此处已经写死
    val cop = CreateMpPayOrderReq().apply {
      title = "一斤菠萝"
      amount = BigDecimal("0.01")
      currency = ISO4217.CNY
      wechatUserOpenId = "oRYYL5Bjwp3Qy4B7nEYBxrrP2yno"
    }
    return payService.createMpPayOrder(cop)
  }

  @GetMapping("payOrder")
  fun findPayOrder(findPayOrderReq: FindPayOrderReq): FindPayOrderResp? {
    return payService.findPayOrder(findPayOrderReq)
  }

  @PostMapping("refundOrder")
  fun refundOrder(): String {
    // TODO 此处金额已经写死
    payService.applyRefundPayOrder(
      BigDecimal("0.01"),
      BigDecimal("0.01")
    )
    return ""
  }
}
