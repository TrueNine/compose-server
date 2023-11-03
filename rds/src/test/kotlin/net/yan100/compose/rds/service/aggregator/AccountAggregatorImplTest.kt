package net.yan100.compose.rds.service.aggregator


import net.yan100.compose.core.id.Snowflake
import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.models.req.LoginAccountReq
import net.yan100.compose.rds.models.req.ModifyAccountPasswordReq
import net.yan100.compose.rds.models.req.RegisterAccountReq
import net.yan100.compose.rds.service.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import kotlin.test.*

@SpringBootTest(classes = [RdsEntrance::class])
class AccountAggregatorImplTest  {

  @Autowired
  lateinit var snowflake: Snowflake

  @Autowired
  lateinit var agg: AccountAggregatorImpl

  @Autowired
  lateinit var us: IUserService

  fun getRegisterParam() = RegisterAccountReq().apply {
    account = "abcd${snowflake.nextStringId()}"
    password = "qwer1234"
    nickName = "我艹${snowflake.nextStringId()}"
    description = "我命由我不白天"
  }

  @Test
  fun testRegisterAccount() {
    val param = getRegisterParam()
    val r = agg.registerAccount(param)
    assertTrue("账号没有注册成功 $r") { null != r }
    val u = us.findUserByAccount(param.account!!)!!

    assertNotNull(u, "账号注册后查询不到")
    assertNotEquals(u.pwdEnc, param.password, "用户密码保存后必须加密")

    val nr = agg.registerAccount(param)
    assertFalse("重复注册了一次账号") { null != nr }
  }

  fun regUser(): RegisterAccountReq {
    val regParam = getRegisterParam()
    agg.registerAccount(regParam)
    return regParam
  }

  @Test
  fun testLogin() {
    val regParam = regUser()

    val loginUser = agg.login(LoginAccountReq().apply {
      account = regParam.account
      password = regParam.password
    })!!
    assertNotEquals(loginUser.pwdEnc, regParam.password, "密码必须加密保存")

    val errLoginUser = agg.login(LoginAccountReq().apply {
      account = regParam.account
      password = "abcdefg"
    })
    assertNull(errLoginUser)
  }

  @Test
  fun testModifyPassword() {
    val regParam = regUser()
    val m = agg.modifyPassword(ModifyAccountPasswordReq().apply {
      account = regParam.account
      oldPassword = regParam.password
      newPassword = "aaccee3313"
    })
    assertTrue("不能正常修改密码") { m }

    // 不可与之前密码相同
    val n = agg.modifyPassword(ModifyAccountPasswordReq().apply {
      account = regParam.account
      oldPassword = regParam.password
      newPassword = "aaccee3313"
    })
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
  fun testBannedAccountTo() {
    val param = regUser()
    agg.bannedAccountTo(param.account!!, LocalDateTime.parse("2100-12-01T01:01:01"))
    val bandUser = us.findUserByAccount(param.account!!)!!
    assertTrue("用户没有被封禁") { bandUser.band!! }
  }
}
