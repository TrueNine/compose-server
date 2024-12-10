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
package net.yan100.compose.rds.crud.service.aggregator

import jakarta.annotation.Resource
import net.yan100.compose.core.generator.ISnowflakeGenerator
import net.yan100.compose.rds.crud.entities.jpa.UserInfo
import net.yan100.compose.rds.crud.service.IUserAccountService
import net.yan100.compose.rds.crud.service.IUserInfoService
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import java.time.LocalDateTime
import kotlin.test.*

@Rollback
@SpringBootTest
class IAccountAggregatorImplTest {
  lateinit var snowflake: ISnowflakeGenerator @Resource set
  lateinit var agg: AccountAggregatorImpl @Resource set
  lateinit var us: IUserAccountService @Resource set
  lateinit var ui: IUserInfoService @Resource set

  @Test
  fun `test assignAccountToUserInfo`() {
    val userInfo =
      ui.post(
        UserInfo().apply {
          firstName = "赵"
          lastName = "日天"
        }
      )
    val user = agg.assignAccountToUserInfo(123, userInfo.id)

    assertNotNull(user)
  }

  fun getRegisterParam() =
    IAccountAggregator.RegisterDto().apply {
      account = "abcd${snowflake.nextString()}"
      password = "qwer1234"
      nickName = "dawdawdawdawd${snowflake.nextString()}"
      description = "awdawdawdawdaw"
      createUserId = 0
    }

  @Test
  fun testRegisterAccount() {
    val param = getRegisterParam()
    val r = agg.registerAccount(param)
    assertTrue("账号没有注册成功 $r") { null != r }
    val u = us.fetchByAccount(param.account!!)!!

    assertNotNull(u, "账号注册后查询不到")
    assertNotEquals(u.pwdEnc, param.password, "用户密码保存后必须加密")

    val nr = agg.registerAccount(param)
    assertFalse("重复注册了一次账号") { null != nr }
  }

  fun regUser(): IAccountAggregator.RegisterDto {
    val regParam = getRegisterParam()
    agg.registerAccount(regParam)
    return regParam
  }

  @Test
  fun testLogin() {
    val regParam = regUser()

    val loginUser =
      agg.login(
        IAccountAggregator.LoginDto().apply {
          account = regParam.account
          password = regParam.password
        }
      )!!
    assertNotEquals(loginUser.pwdEnc, regParam.password, "密码必须加密保存")

    val errLoginUser =
      agg.login(
        IAccountAggregator.LoginDto().apply {
          account = regParam.account
          password = "abcdefg"
        }
      )
    assertNull(errLoginUser)
  }

  @Test
  fun testModifyPassword() {
    val regParam = regUser()
    val m =
      agg.modifyPassword(
        IAccountAggregator.ModifyPasswordDto().apply {
          account = regParam.account
          oldPassword = regParam.password
          newPassword = "aaccee3313"
        }
      )
    assertTrue("不能正常修改密码") { m }

    // 不可与之前密码相同
    val n =
      agg.modifyPassword(
        IAccountAggregator.ModifyPasswordDto().apply {
          account = regParam.account
          oldPassword = regParam.password
          newPassword = "aaccee3313"
        }
      )
    assertFalse("能修改与以前相同的密码") { n }
  }

  @Test
  fun testVerifyPassword() {
    val regParam = regUser()

    val v = agg.verifyPassword(regParam.account!!, regParam.password!!)
    assertTrue("正常密码校验不通过") { v }

    val err = agg.verifyPassword(regParam.account!!, "adawdawddawd")
    assertFalse("异常密码校验通过") { err }
  }

  @Test
  fun testBanWith() {
    val param = regUser()
    agg.banWith(param.account!!, LocalDateTime.parse("2100-12-01T01:01:01"))
    val bandUser = us.fetchByAccount(param.account!!)!!
    assertTrue("用户没有被封禁") { bandUser.band }
  }
}
