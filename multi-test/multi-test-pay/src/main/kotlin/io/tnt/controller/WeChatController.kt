package io.tnt.controller


import com.wechat.pay.java.core.util.PemUtil
import net.yan100.compose.core.id.BizCodeGenerator
import net.yan100.compose.core.lang.encodeBase64String
import net.yan100.compose.pay.api.WechatPayJsApi
import net.yan100.compose.pay.models.request.CreateOrderApiRequestParam
import net.yan100.compose.pay.models.request.FindPayOrderRequestParam
import net.yan100.compose.pay.models.response.CreateOrderResponseResult
import net.yan100.compose.pay.models.response.QueryOrderApiResponseResult
import net.yan100.compose.pay.properties.WeChatPaySingleConfigProperty
import net.yan100.compose.pay.service.SinglePayService
import net.yan100.compose.pay.typing.WechatPayGrantTyping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.security.Signature
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

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
    return wechatPayJsApi.token(payProperty.mpAppId, payProperty.apiSecret, code, WechatPayGrantTyping.AUTH_CODE.getValue()).body
  }

  @PostMapping("pullUpPayOrder")
  fun pullUpPayOrder(): CreateOrderResponseResult {
    // TODO 此处已经写死
    val cop = CreateOrderApiRequestParam().apply {
      title = "一斤菠萝"
      amount = BigDecimal("0.01")
      customOrderId = bizCodeGenerator.nextCodeStr()
      wechatUserOpenId = "oRYYL5H-IKKK0sHs1L0EOjZw1Ne4"
    }

    val createOrderApiResponseResult = payService.pullUpMpPayOrder(cop)
    val createOrderResponseResult = CreateOrderResponseResult().apply {
      // TODO 确定此处是否必须为 32 字符串
      nonceStr = "12300402307060504293450697039607"
      packageStr = "prepay_id=" + createOrderApiResponseResult?.prePayId
      timeStamp = System.currentTimeMillis() / 1000
    }

    val signatureStr = Stream.of(
      payProperty.mpAppId,
      Objects.requireNonNull(createOrderResponseResult.timeStamp).toString(),
      createOrderResponseResult.nonceStr,
      createOrderResponseResult.packageStr
    ).collect(Collectors.joining("\n", "", "\n"))

    // TODO 封装签名方法
    val signature = Signature.getInstance("SHA256withRSA")
    signature.initSign(PemUtil.loadPrivateKeyFromString(payProperty.privateKey))
    signature.update(signatureStr.toByteArray(StandardCharsets.UTF_8))
    createOrderResponseResult.signType = "RSA"
    createOrderResponseResult.paySign = signature.sign().encodeBase64String
    return createOrderResponseResult
  }

  @GetMapping("payOrder")
  fun findPayOrder(findPayOrderRequestParam: FindPayOrderRequestParam): QueryOrderApiResponseResult? {
    return payService.findPayOrder(findPayOrderRequestParam)
  }

  @PostMapping("refundOrder")
  fun refundOrder(): String {
    // TODO 此处金额已经写死
    payService.refundPayOrder(
      BigDecimal("0.01"),
      BigDecimal("0.01")
    )
    return ""
  }
}
