package com.truenine.component.pay.service.impl

import cn.hutool.core.util.IdUtil
import com.truenine.component.core.lang.LogKt
import com.truenine.component.pay.PayEntrance
import com.truenine.component.pay.models.request.CreateOrderRequestParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import java.math.BigDecimal

@Rollback
@SpringBootTest(classes = [PayEntrance::class])
class WeChatPayServiceTest : AbstractTestNGSpringContextTests() {

  @Autowired
  private lateinit var weChatPayService: WeChatPayService;

  private val log = LogKt.getLog(this::class)

  @Test
  fun testCreateOrder() {
    val snowflake = IdUtil.getSnowflake()
    val createOrderRequestParam = CreateOrderRequestParam().apply {
      this.orderId = snowflake.nextIdStr()
      this.openId = snowflake.nextIdStr()
      this.title = "买一斤大菠萝"
      this.money = BigDecimal("0.01")
    }
    val createOrderResponseResult = weChatPayService.createOrder(createOrderRequestParam)
    log.debug(createOrderResponseResult.toString());
  }

}
