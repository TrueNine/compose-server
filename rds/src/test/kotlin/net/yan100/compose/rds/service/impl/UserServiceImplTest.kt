package net.yan100.compose.rds.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.id.Snowflake
import net.yan100.compose.rds.entity.RoleGroup
import net.yan100.compose.rds.service.RoleGroupService
import net.yan100.compose.rds.service.UserInfoService
import net.yan100.compose.rds.service.aggregator.RbacAggregator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest
class UserServiceImplTest {

  @Autowired
  lateinit var service: UserServiceImpl

  @Autowired
  lateinit var snowflake: Snowflake

  fun getUser() = net.yan100.compose.rds.entity.User().apply {
    this.account = snowflake.nextStringId()
    this.nickName = "ab + ${snowflake.nextStringId()}"
    this.pwdEnc = "qwer1234"
  }

  @Autowired
  lateinit var infoService: UserInfoService

  @Autowired
  lateinit var roleGroupService: RoleGroupService

  @Autowired
  lateinit var agg: RbacAggregator

  @Autowired
  lateinit var mapper: ObjectMapper

  @Test
  fun testFindUserByAccount() {
    val saved = service.save(getUser())
    val info = infoService.save(net.yan100.compose.rds.entity.UserInfo().apply {
      userId = saved.id
    })
    val rg = roleGroupService.save(RoleGroup().apply {
      name = "权限1"
    })
    agg.saveRoleGroupToUser(rg.id!!, saved.id!!)!!
    assertEquals(saved.id, info.userId)
    val acc = service.findFullUserByAccount(saved.account!!)!!
    assertEquals(acc.id, info.userId)
    println(mapper.writeValueAsString(acc))
  }

  @Test
  fun testFindPwdEncByAccount() {
    val saved = service.save(getUser())
    val se = service.findPwdEncByAccount(saved.account!!)
    assertEquals(saved.pwdEnc, se)
  }

  @Test
  fun testExistsByAccount() {
    val saved = service.save(getUser())
    assertTrue { service.existsByAccount(saved.account!!) }
  }

  @Test
  fun testModifyUserBandTimeTo() {
    val saved = service.save(getUser())
    service.modifyUserBandTimeTo(saved.account!!, LocalDateTime.parse("2025-01-01T00:00:00"))

    val succ = service.findById(saved.id!!)!!
    assertTrue("用户没有被封禁") { succ.band!! }

    service.modifyUserBandTimeTo(saved.account!!, null)
    val unblock = service.findUserByAccount(saved.account!!)!!
    assertFalse { unblock.band!! }

    service.modifyUserBandTimeTo(saved.account!!, LocalDateTime.parse("2021-01-01T00:00:00"))

    assertFalse("用户被封禁到了之前的日期") {
      service.findUserByAccount(saved.account!!)!!.band!!
    }
  }
}
