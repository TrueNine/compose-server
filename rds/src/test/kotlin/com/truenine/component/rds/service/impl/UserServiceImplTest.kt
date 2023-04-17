package com.truenine.component.rds.service.impl

import com.truenine.component.core.id.Snowflake
import com.truenine.component.rds.entity.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import kotlin.test.assertEquals
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
}
