package net.yan100.compose.pay.controller

import cn.hutool.core.codec.Base64
import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.IdUtil
import cn.hutool.core.util.RandomUtil
import com.wechat.pay.java.core.util.PemUtil
import lombok.SneakyThrows
import net.yan100.compose.pay.api.WeChatApi
import net.yan100.compose.pay.models.request.CreateOrderApiRequestParam
import net.yan100.compose.pay.models.request.QueryOrderApiRequestParam
import net.yan100.compose.pay.models.response.CreateOrderResponseResult
import net.yan100.compose.pay.models.response.QueryOrderApiResponseResult
import net.yan100.compose.pay.properties.WeChatProperties
import net.yan100.compose.pay.service.impl.WeChatPayService
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
@RequestMapping("v1/wechat")
class WeChatController(
  private val weChatApi: WeChatApi,
  private val weChatProperties: WeChatProperties,
  private val weChatPayService: WeChatPayService
) {
  @GetMapping(value = ["/getUserInfo"])
  fun getUserInfo(code: String?): String? {
    return weChatApi.token(weChatProperties.appId, weChatProperties.appSecret, code, "authorization_code")!!.body
  }

  @SneakyThrows
  @GetMapping(value = ["/createOrder"])
  fun createOrder(): CreateOrderResponseResult {
    val snowflake = IdUtil.getSnowflake()
    val cop = CreateOrderApiRequestParam()
    cop.orderId = snowflake.nextIdStr()
    cop.openId = "oRYYL5H-IKKK0sHs1L0EOjZw1Ne4"
    cop.money = BigDecimal("0.01")
    cop.title = "一斤菠萝"

    val createOrderApiResponseResult = weChatPayService.createOrder(cop)
    val createOrderResponseResult = CreateOrderResponseResult()
    createOrderResponseResult.nonceStr = RandomUtil.randomString(32)
    createOrderResponseResult.packageStr = "prepay_id=" + createOrderApiResponseResult?.prepayId
    createOrderResponseResult.timeStamp = System.currentTimeMillis() / 1000
    val signatureStr = Stream.of(
      weChatProperties.appId,
      Objects.requireNonNull(createOrderResponseResult.timeStamp).toString(),
      createOrderResponseResult.nonceStr,
      createOrderResponseResult.packageStr
    )
      .collect(Collectors.joining("\n", "", "\n"))
    val privateKey = FileUtil.readString(weChatProperties.privateKeyPath, StandardCharsets.UTF_8)
    val signature = Signature.getInstance("SHA256withRSA")
    signature.initSign(PemUtil.loadPrivateKeyFromString(privateKey))
    signature.update(signatureStr.toByteArray(StandardCharsets.UTF_8))
    createOrderResponseResult.signType = "RSA"
    createOrderResponseResult.paySign = Base64.encode(signature.sign())
    return createOrderResponseResult
  }

  @GetMapping("queryOrder")
  fun queryOrder(queryOrderApiRequestParam: QueryOrderApiRequestParam): QueryOrderApiResponseResult? {
    return weChatPayService.queryOrder(queryOrderApiRequestParam)
  }

  @PostMapping("refundOrder")
  fun refundOrder(): String {
    weChatPayService.refundOrder()
    return ""
  }
}
