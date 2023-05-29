package net.yan100.compose.rds.service.aggregator


import net.yan100.compose.core.id.Snowflake
import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.models.request.LoginAccountRequestParam
import net.yan100.compose.rds.models.request.ModifyAccountPasswordRequestParam
import net.yan100.compose.rds.models.request.RegisterAccountRequestParam
import net.yan100.compose.rds.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import java.time.LocalDateTime
import kotlin.test.*

@SpringBootTest(classes = [RdsEntrance::class])
class AccountAggregatorImplTest : AbstractTestNGSpringContextTests() {

  @Autowired
  lateinit var snowflake: Snowflake

  @Autowired
  lateinit var agg: AccountAggregatorImpl

  @Autowired
  lateinit var us: UserService

  fun getRegisterParam() = object : RegisterAccountRequestParam {
    override var account = "abcd${snowflake.nextStringId()}"
    override var password = "qwer1234"
    override var nickName = "我艹${snowflake.nextStringId()}"
    override var description: String? = "我命由我不白天"
  }

  @Test
  fun testRegisterAccount() {
    val param = getRegisterParam()
    val r = agg.registerAccount(param)
    assertTrue("账号没有注册成功 $r") { r }
    val u = us.findUserByAccount(param.account)!!

    assertNotNull(u, "账号注册后查询不到")
    assertNotEquals(u.pwdEnc, param.password, "用户密码保存后必须加密")

    val nr = agg.registerAccount(param)
    assertFalse("重复注册了一次账号") { nr }
  }

  fun regUser(): RegisterAccountRequestParam {
    val regParam = getRegisterParam()
    agg.registerAccount(regParam)
    return regParam
  }

  @Test
  fun testLogin() {
    val regParam = regUser()

    val loginUser = agg.login(object : LoginAccountRequestParam {
      override val account = regParam.account
      override val password = regParam.password
    })!!
    assertNotEquals(loginUser.pwdEnc, regParam.password, "密码必须加密保存")

    val errLoginUser = agg.login(object : LoginAccountRequestParam {
      override val account = regParam.account
      override val password = "abcdefg"
    })
    assertNull(errLoginUser)
  }

  @Test
  fun testModifyPassword() {
    val regParam = regUser()
    val m = agg.modifyPassword(object : ModifyAccountPasswordRequestParam {
      override val account = regParam.account
      override var oldPassword = regParam.password
      override var newPassword = "aaccee3313"
    })
    assertTrue("不能正常修改密码") { m }

    // 不可与之前密码相同
    val n = agg.modifyPassword(object : ModifyAccountPasswordRequestParam {
      override val account = regParam.account
      override var oldPassword = regParam.password
      override var newPassword = "aaccee3313"
    })
    assertFalse("能修改与以前相同的密码") { n }
  }

  @Test
  fun testVerifyPassword() {
    val regParam = regUser()

    val v = agg.verifyPassword(regParam.account, regParam.password)
    assertTrue("正常密码校验不通过") { v }

    val err = agg.verifyPassword(regParam.account, "adawdawddawd")
    assertFalse("异常密码校验通过") { err }
  }

  @Test
  fun testBannedAccountTo() {
    val param = regUser()
    agg.bannedAccountTo(param.account, LocalDateTime.parse("2100-12-01T01:01:01"))
    val bandUser = us.findUserByAccount(param.account)!!
    assertTrue("用户没有被封禁") { bandUser.band!! }
  }
}
