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
package net.yan100.compose.rds.crud.service.impl

import jakarta.annotation.Resource
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.yan100.compose.rds.crud.entities.jpa.UserInfo
import net.yan100.compose.rds.crud.service.IUserInfoService
import net.yan100.compose.testtookit.RDBRollback
import net.yan100.compose.testtookit.assertNotEmpty
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@RDBRollback
@SpringBootTest
class UserInfoServiceImplTest {
  lateinit var userInfoService: UserInfoServiceImpl @Resource set

  @RDBRollback
  @BeforeTest
  fun setup() {
    userInfoService.postFound(UserInfo().apply {
      firstName = "R"
      lastName = "OOT"
    })
  }

  @Test
  @RDBRollback
  fun `query dsl fetch all by`() {
    val e = userInfoService.fetchAll()
    val result = userInfoService.fetchAllBy(
      IUserInfoService.UserInfoFetchParam(
        firstName = "R"
      )
    )
    assertNotNull(result)
    assertNotEmpty { result.d.toList() }
    log.info("result: {}", result)
  }

  @Test
  fun `test findIsRealPeopleById`() {
    runBlocking {
      launch {
        val r = userInfoService.findIsRealPeopleByUserId(0)
        log.info("r: {}", r)
      }
    }
  }
}
