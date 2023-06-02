package net.yan100.compose.pay.service.impl

import net.yan100.compose.core.id.BizCodeGenerator
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.pay.models.request.CreateOrderApiRequestParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import java.math.BigDecimal


@SpringBootTest
class WeChatSinglePayServiceTest : AbstractTestNGSpringContextTests() {
  private val log = slf4j(this::class)

  @Autowired
  lateinit var service: WeChatSinglePayService

  @Autowired
  lateinit var bizCodeGenerator: BizCodeGenerator

  @Test
  fun testCreateOrder() {
    val crp = CreateOrderApiRequestParam().apply {
      customOrderId = bizCodeGenerator.nextCodeStr()
      wechatUserOpenId = "oRYYL5H-IKKK0sHs1L0EOjZw1Ne4"
      amount = BigDecimal("0.01")
      title = "一斤菠萝"
    }
    log.info("测试商户订单号 = {}", crp.customOrderId)
    val order = service.pullUpMpPayOrder(crp)
    log.info(order.toString())
  }
}
