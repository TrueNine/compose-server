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
package net.yan100.compose.data.extract.service

import jakarta.annotation.Resource
import net.yan100.compose.data.extract.DataExtractEntrance
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(classes = [DataExtractEntrance::class])
class ILazyAddressServiceTest {
  lateinit var lazys: ILazyAddressService @Resource set

  @Test
  fun `test find children`() {
    val a = lazys.findAllChildrenByCode("4331")
    assertFalse(a.isEmpty())
    assertFailsWith<IllegalArgumentException> { lazys.findAllChildrenByCode("4") }

    assertFailsWith<IllegalArgumentException> { lazys.findAllChildrenByCode("433") }
    val b = lazys.findAllChildrenByCode("")
    println(b)
  }

  @Test
  fun `test lookupByCode`() {
    val a =
      lazys.lookupByCode("433127103", firstFind = { null }, deepCondition = { false }) {
        println("触发保存 =$it")
        it.result.find { ir -> ir.code.code == "433127103" }
      }
    assertNotNull(a)

    val b = lazys.lookupByCode("433127103221", firstFind = { null }, deepCondition = { false }) { it.result }
    assertNotNull(b)
  }

  @Test
  fun `test lookupAllChildrenByCode`() {
    val a = lazys.lookupAllChildrenByCode("433127103", firstFind = { null }, deepCondition = { false }) { it.result }
    assertTrue(a.isNotEmpty())
    val b = lazys.lookupAllChildrenByCode("433127103221", firstFind = { null }, deepCondition = { false }) { it.result }
    assertFalse(b.isNotEmpty())
  }
}
