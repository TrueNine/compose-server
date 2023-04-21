package com.truenine.component.rds.service.impl

import com.truenine.component.core.id.Snowflake
import com.truenine.component.rds.entity.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest
class UserServiceImplTest : AbstractTestNGSpringContextTests() {

  @Autowired
  lateinit var service: UserServiceImpl

  @Autowired
  lateinit var snowflake: Snowflake

  fun getUser() = UserEntity().apply {
    this.account = snowflake.nextStr()
    this.nickName = "ab + ${snowflake.nextStr()}"
    this.pwdEnc = "qwer1234"
  }

  @Test
  fun testFindUserByAccount() {
    val saved = service.save(getUser())!!
    val acc = service.findUserByAccount(saved.account)!!
    assertEquals(saved, acc)
  }

  @Test
  fun testFindPwdEncByAccount() {
    val saved = service.save(getUser())!!
    val se = service.findPwdEncByAccount(saved.account)
    assertEquals(saved.pwdEnc, se)
  }

  @Test
  fun testExistsByAccount() {
    val saved = service.save(getUser())!!
    assertTrue { service.existsByAccount(saved.account) }
  }

  @Test
  fun testModifyUserBandTimeTo() {
    val saved = service.save(getUser())!!
    service.modifyUserBandTimeTo(saved.account, LocalDateTime.parse("2025-01-01T00:00:00"))

    val succ = service.findById(saved.id)!!
    assertTrue("用户没有被封禁") { succ.isBand }

    service.modifyUserBandTimeTo(saved.account, null)
    val unblock = service.findUserByAccount(saved.account)!!
    assertFalse { unblock.isBand }

    service.modifyUserBandTimeTo(saved.account, LocalDateTime.parse("2021-01-01T00:00:00"))

    assertFalse("用户被封禁到了之前的日期") {
      service.findUserByAccount(saved.account)!!.isBand
    }
  }
}
