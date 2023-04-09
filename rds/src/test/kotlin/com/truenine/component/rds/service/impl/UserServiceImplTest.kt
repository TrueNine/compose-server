package com.truenine.component.rds.service.impl

import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.entity.UserEntity
import com.truenine.component.rds.entity.UserInfoEntity
import jakarta.annotation.Resource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Rollback
@SpringBootTest(classes = [RdsEntrance::class])
class UserServiceImplTest : AbstractTransactionalTestNGSpringContextTests() {

  @Resource
  lateinit var userService: UserServiceImpl

  @Resource
  lateinit var passwordEncoder: PasswordEncoder

  private lateinit var testUser: UserEntity
  private lateinit var testUserInfo: UserInfoEntity

  @BeforeMethod
  fun init() {
    UserEntity().apply {
      this.account = "testUser"
      this.doc = "233"
      this.pwdEnc = passwordEncoder.encode("qwer1234")
      this.nickName = "阿日斯兰"
      this.lastLoginTime = LocalDateTime.now()
    }.apply {
      testUser = userService.saveUser(this)
    }

    UserInfoEntity().apply {
      this.userId = testUser.id
      this.birthday = LocalDate.of(2021, 3, 4)
      this.firstName = "阿日"
      this.lastName = "斯兰"
      this.email = "truenine@qq.com"
      this.idCard = "442331199503012231"
      this.phone = "13323412345"
      this.gender = 1
    }.apply {
      testUserInfo = userService.saveUserInfo(this)!!
    }
  }

  @AfterMethod
  fun destroy() {
    userService.deleteUser(this.testUser)
    userService.deleteUserInfo(this.testUserInfo)
  }

  @Test
  fun testFindUserById() {
    val u = userService.findUserById(testUser.id)
    assertEquals(testUser, u, "查询出来的数据不对")
  }

  @Test
  fun testFindUserByAccount() {
    val u = userService.findUserByAccount(testUser.account)
    assertEquals(testUser, u, "查询出来的数据不对")
  }

  @Test
  fun testFindPwdEncByAccount() {
    val u = userService.findPwdEncByAccount(testUser.account)
    assertEquals(testUser.pwdEnc, u, "查询出来的数据不对")
  }

  @Test
  fun testExistsByAccount() {
    assertTrue("没有查询到账户的用户") {
      userService.existsByAccount(testUser.account)
    }
    assertFalse("查询到不存在的用户") {
      userService.existsByAccount("我草泥马")
    }
  }

  @Test
  fun testFindUserInfoById() {
    val findInfo = userService.findUserInfoById(testUserInfo.userId)
    assertEquals(
      findInfo,
      testUserInfo,
      "没有查询到用户信息"
    )
  }

  @Test
  fun testFindUserInfoByAccount() {
    val info = userService.findUserInfoByAccount(testUser.account)
    assertEquals(testUserInfo, info, "用户信息不符")
    assertEquals(testUserInfo.userId, testUser.id, "用户 id 不一致")
  }

  @Test
  fun testSaveUser() {
    val a = UserEntity().apply {
      this.account = "qwer1234"
      this.nickName = "卧槽"
      this.pwdEnc = passwordEncoder.encode("qwerty1234")
    }
    val u = userService.saveUser(a)
    assertNotNull(u, "没有保存用户")
  }

  @Test
  fun testSaveUserInfo() {
    userService.deleteUserInfo(testUserInfo)
    UserInfoEntity().apply {
      userId = testUser.id
      phone = "123324240102"
      idCard = "123124010123232233"
      email = "truenine@163.com"
      assertNotNull(userService.saveUserInfo(this), "没有保存用户")
    }
  }

  @Test
  fun testSaveUserInfoByAccount() {
    val newUser =
      UserEntity().apply {
        account = "abtest"
        nickName = "我日了狗"
        pwdEnc = passwordEncoder.encode("abc123")
      }

    val newUserInfo =
      UserInfoEntity().apply {
        userId = newUser.id
        phone = "123324240102"
        idCard = "123124010123232233"
        email = "truenine@163.com"
      }

    val u = userService.saveUser(newUser)
    val info = userService.saveUserInfoByAccount(u.account, newUserInfo)

    assertNotNull(u, "没有保存用户")
    assertNotNull(info, "没有保存用户信息")
    assertEquals(u.id, info.userId, "用户信息不匹配")
  }

  @Test
  fun testDeleteUser() {
    log.debug("destroy 已执行删除")
  }

  @Test
  fun testDeleteUserInfo() {
    log.warn("destroy 已执行删除")
  }

  companion object {
    val log = LogKt.getLog(UserServiceImplTest::class)
  }
}
