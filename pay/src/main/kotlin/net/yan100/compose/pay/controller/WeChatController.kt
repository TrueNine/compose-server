package net.yan100.compose.pay.controller

import cn.hutool.core.codec.Base64
import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.RandomUtil
import com.wechat.pay.java.core.util.PemUtil
import net.yan100.compose.core.id.BizCodeGenerator
import net.yan100.compose.pay.api.WeChatApi
import net.yan100.compose.pay.models.request.CreateOrderApiRequestParam
import net.yan100.compose.pay.models.request.FindPayOrderRequestParam
import net.yan100.compose.pay.models.response.CreateOrderResponseResult
import net.yan100.compose.pay.models.response.QueryOrderApiResponseResult
import net.yan100.compose.pay.properties.WeChatProperties
import net.yan100.compose.pay.service.impl.WeChatSinglePayService
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
  private val weChatApi: WeChatApi,
  private val weChatProperties: WeChatProperties,
  private val weChatPayService: WeChatSinglePayService,
  private val bizCodeGenerator: BizCodeGenerator
) {

  @GetMapping("userInfo")
  fun getUserInfo(code: String?): String? {
    return weChatApi.token(weChatProperties.mpAppId, weChatProperties.apiSecret, code, WechatPayGrantTyping.AUTH_CODE.getValue())!!.body
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

    val createOrderApiResponseResult = weChatPayService.pullUpPayOrder(cop)
    val createOrderResponseResult = CreateOrderResponseResult().apply {
      nonceStr = RandomUtil.randomString(32)
      packageStr = "prepay_id=" + createOrderApiResponseResult?.prePayId
      timeStamp = System.currentTimeMillis() / 1000
    }

    val signatureStr = Stream.of(
      weChatProperties.mpAppId,
      Objects.requireNonNull(createOrderResponseResult.timeStamp).toString(),
      createOrderResponseResult.nonceStr,
      createOrderResponseResult.packageStr
    ).collect(Collectors.joining("\n", "", "\n"))

    val privateKey = FileUtil.readString(weChatProperties.privateKeyPath, StandardCharsets.UTF_8)
    val signature = Signature.getInstance("SHA256withRSA")
    signature.initSign(PemUtil.loadPrivateKeyFromString(privateKey))
    signature.update(signatureStr.toByteArray(StandardCharsets.UTF_8))
    createOrderResponseResult.signType = "RSA"
    createOrderResponseResult.paySign = Base64.encode(signature.sign())
    return createOrderResponseResult
  }

  @GetMapping("payOrder")
  fun findPayOrder(findPayOrderRequestParam: FindPayOrderRequestParam): QueryOrderApiResponseResult? {
    return weChatPayService.findPayOrder(findPayOrderRequestParam)
  }

  @PostMapping("refundOrder")
  fun refundOrder(): String {
    // TODO 此处金额已经写死
    weChatPayService.refundPayOrder(
      BigDecimal("0.01"),
      BigDecimal("0.01")
    )
    return ""
  }
}
