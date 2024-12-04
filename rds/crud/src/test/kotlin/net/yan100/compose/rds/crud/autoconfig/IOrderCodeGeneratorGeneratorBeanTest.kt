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
package net.yan100.compose.rds.crud.autoconfig

import jakarta.annotation.Resource
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
class IOrderCodeGeneratorGeneratorBeanTest {

  lateinit var bizId: BizCodeGeneratorBean @Resource set

  @Test
  fun testGenerate() {
    val id = bizId.generate(null, null)
    assertNotNull(id)
    println(id)
    assertTrue("生成的订单号不满足位数") { (id as? String)?.length == 21 }

    val batchIds = List(100) { bizId.generate(null, null) }

    println(batchIds.reduce { a, b -> "$a\n$b" })

    assertTrue("生成包含了重复ID") { batchIds.size == batchIds.toSet().size }
  }
}
