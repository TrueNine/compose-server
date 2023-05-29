package io.tnt.controller


import net.yan100.compose.core.encrypt.EncryptAlgorithmTyping
import net.yan100.compose.core.encrypt.Encryptors
import net.yan100.compose.core.encrypt.Keys
import net.yan100.compose.core.id.BizCodeGenerator
import net.yan100.compose.core.lang.encodeBase64String
import net.yan100.compose.pay.api.WechatPayJsApi
import net.yan100.compose.pay.models.request.CreateOrderApiRequestParam
import net.yan100.compose.pay.models.request.FindPayOrderRequestParam
import net.yan100.compose.pay.models.response.FindPayOrderResponseResult
import net.yan100.compose.pay.models.response.PullUpMpPayOrderResponseResult
import net.yan100.compose.pay.properties.WeChatPaySingleConfigProperty
import net.yan100.compose.pay.service.SinglePayService
import net.yan100.compose.pay.typing.WechatPayGrantTyping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping("v1/pay/single/wechat")
class WeChatController(
  private val wechatPayJsApi: WechatPayJsApi,
  private val payProperty: WeChatPaySingleConfigProperty,
  private val payService: SinglePayService,
  private val bizCodeGenerator: BizCodeGenerator
) {

  @GetMapping("userInfo")
  fun getUserInfo(code: String?): String? {
    return wechatPayJsApi.findUserToken(payProperty.mpAppId, payProperty.apiSecret, code, WechatPayGrantTyping.AUTH_CODE).body
  }

  @PostMapping("pullUpPayOrder")
  fun pullUpPayOrder(): PullUpMpPayOrderResponseResult {
    // TODO 此处已经写死
    val cop = CreateOrderApiRequestParam().apply {
      title = "一斤菠萝"
      amount = BigDecimal("0.01")
      customOrderId = bizCodeGenerator.nextCodeStr()
      wechatUserOpenId = "oRYYL5H-IKKK0sHs1L0EOjZw1Ne4"
    }

    return PullUpMpPayOrderResponseResult().apply {
      nonceStr = Keys.generateRandomAsciiString(32)
      // 拉起支付
      packageStr = "prepay_id=" + payService.pullUpMpPayOrder(cop)?.prePayId
      timeStamp = System.currentTimeMillis() / 1000

      signType = EncryptAlgorithmTyping.RSA.getValue()
      val signatureStr = "${payProperty.mpAppId}\n${timeStamp}\n${nonceStr}\n${packageStr}\n"
      val signature = Encryptors.signWithSha256WithRsaByRsaPrivateKey(
        signatureStr,
        Keys.readRsaPrivateKeyByBase64AndStandard(payProperty.privateKey)!!
      )
      paySign = signature.sign().encodeBase64String
    }
  }

  @GetMapping("payOrder")
  fun findPayOrder(findPayOrderRequestParam: FindPayOrderRequestParam): FindPayOrderResponseResult? {
    return payService.findPayOrder(findPayOrderRequestParam)
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
