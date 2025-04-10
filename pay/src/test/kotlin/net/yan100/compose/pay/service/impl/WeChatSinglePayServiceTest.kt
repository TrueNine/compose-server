package net.yan100.compose.pay.service.impl

import jakarta.annotation.Resource
import net.yan100.compose.generator.IOrderCodeGenerator
import net.yan100.compose.pay.service.SinglePayService
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import kotlin.test.Ignore
import kotlin.test.Test

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
