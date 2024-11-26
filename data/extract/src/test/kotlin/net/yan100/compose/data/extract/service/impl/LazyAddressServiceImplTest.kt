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
package net.yan100.compose.data.extract.service.impl

import jakarta.annotation.Resource
import net.yan100.compose.core.exceptions.RemoteCallException
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.*

@Ignore
@SpringBootTest
class LazyAddressServiceImplTest {
  lateinit var lazys: LazyAddressServiceImpl @Resource set
  private val testCode = "433127103101"

  @Test
  fun `find all provinces`() {
    val all = lazys.findAllProvinces()
    assertNotNull(all)
    assertTrue("all = $all") { all.isNotEmpty() }
  }

  @Test
  fun testFindAllCityByCode() {
    val all = lazys.findAllCityByCode(testCode)
    assertNotNull(all)
    assertTrue("all = $all") { all.isNotEmpty() }
  }

  @Test
  fun testFindAllCountyByCode() {
    val all = lazys.findAllCountyByCode(testCode)
    assertNotNull(all)
    assertTrue("当前 all = $all") { all.isNotEmpty() }
  }

  @Test
  fun testFindAllCountyByCodeNotExists() {
    val all = lazys.findAllCountyByCode("330100000000")
    assertNotNull(all)
    assertTrue("当前 all = $all") { all.isNotEmpty() }
  }

  @Test
  fun testFindAllTownByCode() {
    val all = lazys.findAllTownByCode(testCode)
    assertNotNull(all)
    assertTrue("当前 all = $all") { all.isNotEmpty() }
  }

  @Test
  fun testFindAllVillageByCode() {
    val all = lazys.findAllVillageByCode(testCode)
    assertNotNull(all)
    assertTrue("all$all") { all.isNotEmpty() }

    assertFailsWith<RemoteCallException> {
      val nullables = lazys.findAllVillageByCode("430000000000")
      assertNull(nullables)
    }
  }
}
