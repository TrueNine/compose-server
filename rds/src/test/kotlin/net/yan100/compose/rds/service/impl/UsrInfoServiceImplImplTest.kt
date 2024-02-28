/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.rds.service.impl

import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.entities.UserInfo
import net.yan100.compose.rds.entities.Usr
import net.yan100.compose.rds.repositories.IUserInfoRepo
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [RdsEntrance::class])
class UsrInfoServiceImplImplTest {
  @Autowired private lateinit var userInfoService: UserInfoServiceImpl

  @Autowired private lateinit var infoRepo: IUserInfoRepo

  @BeforeEach
  fun setUp() {
    infoRepo = mockk()
    userInfoService = UserInfoServiceImpl(infoRepo)
  }

  @Test
  fun testFindUserByWechatOpenId() {
    val openId = "123456"
    val usr = Usr()
    every { infoRepo.findUserByWechatOpenId(openId) } returns usr
    val result = userInfoService.findUserByWechatOpenId(openId)
    assertEquals(result, usr)
  }

  @Test
  fun testFindUserByPhone() {
    val phone = "123456789"
    val usr = Usr()
    every { infoRepo.findUserByPhone(phone) } returns usr
    val result = userInfoService.findUserByPhone(phone)
    assertEquals(result, usr)
  }

  @Test
  fun testFindByUserId() {
    val userId = "123456"
    val userInfoEntity = UserInfo()
    every { infoRepo.findByUserId(userId) } returns userInfoEntity
    val result = userInfoService.findByUserId(userId)
    assertEquals(result, userInfoEntity)
  }
}
