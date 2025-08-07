package io.github.truenine.composeserver.pay.wechat.service.impl

import io.github.truenine.composeserver.generator.IOrderCodeGenerator
import io.github.truenine.composeserver.pay.SinglePayService
import io.github.truenine.composeserver.testtoolkit.log
import jakarta.annotation.Resource
import java.math.BigDecimal
import kotlin.test.Ignore
import kotlin.test.Test
import org.springframework.boot.test.context.SpringBootTest

@Ignore
@SpringBootTest
class WeChatSinglePayServiceTest {
  lateinit var service: WeChatSinglePayService
    @Resource set

  lateinit var bizCodeGenerator: IOrderCodeGenerator
    @Resource set

  @Test
  fun testCreateOrder() {
    val customOrderId = bizCodeGenerator.nextString()
    val crp =
      SinglePayService.CreateMpPayDto(
        wechatUserOpenId = "oRYYL5H-IKKK0sHs1L0EOjZw1Ne4",
        amount = BigDecimal("0.01"),
        customOrderId = customOrderId,
        title = "一斤菠萝",
      )
    val order = service.createMpPayOrder(crp)
    log.info(order.toString())
  }
}
