/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.pay.service.impl

import jakarta.annotation.Resource
import net.yan100.compose.core.generator.IOrderCodeGenerator
import net.yan100.compose.pay.models.req.CreateMpPayOrderReq
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
@SpringBootTest
class WeChatSinglePayServiceTest {
  lateinit var service: WeChatSinglePayService @Resource set
  lateinit var bizCodeGenerator: IOrderCodeGenerator @Resource set

  @Test
  fun testCreateOrder() {
    val customOrderId = bizCodeGenerator.nextString()
    val crp =
      CreateMpPayOrderReq().apply {
        wechatUserOpenId = "oRYYL5H-IKKK0sHs1L0EOjZw1Ne4"
        amount = BigDecimal("0.01")
        this.customOrderId = customOrderId
        title = "一斤菠萝"
      }
    val order = service.createMpPayOrder(crp)
    log.info(order.toString())
  }
}
