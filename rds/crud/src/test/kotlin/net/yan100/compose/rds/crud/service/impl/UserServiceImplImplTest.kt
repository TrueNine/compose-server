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

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Resource
import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.core.generator.ISnowflakeGenerator
import net.yan100.compose.rds.crud.entities.jpa.RoleGroup
import net.yan100.compose.rds.crud.entities.jpa.UserAccount
import net.yan100.compose.rds.crud.entities.jpa.UserInfo
import net.yan100.compose.rds.crud.service.IUserInfoService
import net.yan100.compose.rds.crud.service.aggregator.IRbacAggregator
import net.yan100.compose.testtookit.log
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest
class UserServiceImplImplTest {
  lateinit var service: UserAccountServiceImpl @Resource set
  lateinit var snowflake: ISnowflakeGenerator @Resource set

  fun getUser() =
    UserAccount().apply {
      createUserId = IDbNames.Rbac.ROOT_ID
      account = snowflake.nextString()
      nickName = "ab + ${snowflake.nextString()}"
      pwdEnc = "qwer1234"
    }

  @Resource
  lateinit var infoService: IUserInfoService

  @Resource
  lateinit var roleGroupService: net.yan100.compose.rds.crud.service.IRoleGroupService

  @Resource
  lateinit var agg: IRbacAggregator

  @Resource
  lateinit var mapper: ObjectMapper

  @Test
  fun testFindUserByAccount() {
    val saved = service.post(getUser())
    val info = infoService.post(UserInfo().apply { userId = saved.id })
    val rg = roleGroupService.post(RoleGroup().apply { name = "PERMISSIONS:1" })
    agg.saveRoleGroupToUser(rg.id, saved.id)!!
    assertEquals(saved.id, info.userId)
    val acc = service.findFullUserByAccount(saved.account)!!
    assertEquals(acc.id, info.userId)
    log.info("json: {}", mapper.writeValueAsString(acc))
  }

  @Test
  fun testFindPwdEncByAccount() {
    val saved = service.post(getUser())
    val se = service.findPwdEncByAccount(saved.account)
    assertEquals(saved.pwdEnc, se)
  }

  @Test
  fun testExistsByAccount() {
    val saved = service.post(getUser())
    assertTrue { service.existsByAccount(saved.account) }
  }

  @Test
  fun testModifyUserBandTimeTo() {
    val saved = service.post(getUser())
    service.modifyUserBandTimeTo(saved.account, LocalDateTime.parse("2077-01-01T00:00:00"))

    val succ = service.fetchById(saved.id)!!
    assertTrue("用户没有被封禁") { succ.band }

    service.modifyUserBandTimeTo(saved.account, null)
    val unblock = service.fetchByAccount(saved.account)!!
    assertFalse { unblock.band }

    service.modifyUserBandTimeTo(saved.account, LocalDateTime.parse("2021-01-01T00:00:00"))

    assertFalse("用户被封禁到了之前的日期") { service.fetchByAccount(saved.account)!!.band }
  }
}
