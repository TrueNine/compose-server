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
package net.yan100.compose.rds.service.impl

import kotlin.test.Test
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.entities.account.Usr
import net.yan100.compose.rds.repositories.IUserInfoRepo
import net.yan100.compose.rds.repositories.IUsrRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [RdsEntrance::class])
class UserInfoServiceImplImplTest {
  @Autowired private lateinit var userInfoService: UserInfoServiceImpl

  @Autowired private lateinit var infoRepo: IUserInfoRepo

  @Autowired private lateinit var userRepo: IUsrRepo

  @Test
  fun `test findIsRealPeopleById`() {
    runBlocking {
      launch {
        val r = userInfoService.findIsRealPeopleByUserId("0")
        println(r)
      }
    }
  }
}
