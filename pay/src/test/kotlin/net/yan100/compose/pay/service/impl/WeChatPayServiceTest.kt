package net.yan100.compose.pay.service.impl

import cn.hutool.core.util.IdUtil
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.pay.models.request.CreateOrderApiRequestParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import java.math.BigDecimal


@SpringBootTest
class WeChatPayServiceTest : AbstractTestNGSpringContextTests() {
  private val log = slf4j(this::class)

  @Autowired
  private val service: WeChatPayService? = null

  @Test
  fun testCreateOrder() {
    val snowflake = IdUtil.getSnowflake()
    val crp = CreateOrderApiRequestParam()
    crp.orderId = snowflake.nextIdStr()
    crp.openId = "oRYYL5H-IKKK0sHs1L0EOjZw1Ne4"
    crp.money = BigDecimal("0.01")
    crp.title = "一斤菠萝"
    val order = service!!.createOrder(crp)
    log.debug(order.toString())
  }
}
