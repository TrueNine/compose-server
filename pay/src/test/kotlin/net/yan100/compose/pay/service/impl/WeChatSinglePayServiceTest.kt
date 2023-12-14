package net.yan100.compose.pay.service.impl

import net.yan100.compose.core.id.BizCodeGenerator
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.pay.models.req.CreateMpPayOrderReq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal


@SpringBootTest
class WeChatSinglePayServiceTest {
  private val log = slf4j(this::class)

  @Autowired
  lateinit var service: WeChatSinglePayService

  @Autowired
  lateinit var bizCodeGenerator: BizCodeGenerator

  //@Test
  fun testCreateOrder() {
    val customOrderId = bizCodeGenerator.nextCodeStr()
    val crp = CreateMpPayOrderReq().apply {
      wechatUserOpenId = "oRYYL5H-IKKK0sHs1L0EOjZw1Ne4"
      amount = BigDecimal("0.01")
      this.customOrderId = customOrderId
      title = "一斤菠萝"
    }
    val order = service.createMpPayOrder(crp)
    log.info(order.toString())
  }
}
