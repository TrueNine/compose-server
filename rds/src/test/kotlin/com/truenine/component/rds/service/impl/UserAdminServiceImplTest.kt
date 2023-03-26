package com.truenine.component.rds.service.impl

import com.truenine.component.core.db.Bf
import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.dao.RoleGroupDao
import com.truenine.component.rds.dao.UserInfoDao
import com.truenine.component.rds.dto.UserGroupRegisterDto
import com.truenine.component.rds.dto.UserRegisterDto
import jakarta.annotation.Resource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.time.LocalDate
import kotlin.test.*

@Rollback
@SpringBootTest(classes = [RdsEntrance::class])
class UserAdminServiceImplTest :
  AbstractTransactionalTestNGSpringContextTests() {

  @Resource
  lateinit var adminService: UserAdminServiceImpl

  @Resource
  private lateinit var rbacService: RbacServiceImpl

  private val regDto =
    UserRegisterDto(
      "001",
      "zliq",
      "qwer1234",
      "qwer1234",
      null
    )
  private val testPlainUser =
    UserRegisterDto(
      "303",
      "truenine",
      "qwer1234",
      "qwer1234",
      null
    )

  private lateinit var userRoleGroup: RoleGroupDao
  private lateinit var rootUserGroup: RoleGroupDao

  /**
   * 注册一个普通用户，
   * 以及获取普通权限
   */
  @BeforeMethod
  fun init() {
    userRoleGroup = rbacService.findPlainRoleGroup()
    rootUserGroup = rbacService.findRootRoleGroup()
    adminService.registerPlainUser(testPlainUser)
  }

  @AfterMethod
  fun destroy() {
    adminService.deleteUserByAccount(testPlainUser.account)
  }

  @Test
  fun testRegisterPlainUser() {
    val regUsr = adminService.registerPlainUser(regDto)
    assertNotNull(
      regUsr,
      "没有注册用户"
    )
    val allRoleGroup = adminService.findAllRoleGroupByUser(regUsr)
    assertContains(allRoleGroup, userRoleGroup, "没有分配权限")

    val a = regDto
    a.account = ""
    a.pwd = ""

    val failUser = adminService.registerPlainUser(a)
    assertNull(failUser, "注册了成功了空普通用户")

    val repeatUser = adminService.registerPlainUser(regDto)
    assertNull(repeatUser, "注册了重复的用户")
  }

  @Test
  fun testRegisterRootUser() {
    val rootUser = adminService.registerRootUser(testPlainUser)!!
    val allRoleGroup = adminService.findAllRoleGroupByUser(rootUser)
    assertContains(allRoleGroup, rootUserGroup, "没有分配权限")
  }

  @Test
  fun testCompletionUserInfo() {
    val saveInfo = adminService.completionUserInfo(
      UserInfoDao().apply {
        userId = "0"
        this.birthday = LocalDate.of(1997, 11, 4)
        this.phone = "15675292005"
        this.email = "truenine@qq.com"
        this.gender = 1
        this.firstName = "彭"
        this.lastName = "继工"
      }
    )
    assertNotNull(
      saveInfo,
      "没有添加信息成功"
    )
  }

  @Test
  fun testCompletionUserInfoByAccount() {
    val saveInfo = adminService
      .completionUserInfoByAccount("root",
        UserInfoDao()
          .apply {
            userId = "0"
            this.birthday = LocalDate.of(1997, 11, 4)
            this.phone = "15675292005"
            this.email = "truenine@qq.com"
            this.gender = 1
            this.firstName = "彭"
            this.lastName = "继工"
          })
    assertNotNull(
      saveInfo,
      "没有添加信息成功"
    )
  }

  @Test
  fun testUpdatePasswordByAccountAndOldPassword() {
    adminService.updatePasswordByAccountAndOldPassword(
      "root",
      "qwer1234",
      "abc123"
    )
    val pwdUpdated = adminService.verifyPassword("root", "abc123")
    assertTrue("密码没有更改或更改错误") {
      pwdUpdated
    }
  }

  @Test
  fun testFindUsrVoByAccount() {
    val c = adminService.findUsrVoByAccount(testPlainUser.account)
    log.debug("usrVo = {}", c)
    assertNotNull(c?.user, "没有此用户的信息")
  }

  @Test
  fun testFindAllRoleGroupByAccount() {
    val c = adminService.findAllRoleByAccount(testPlainUser.account)
    log.debug("usrVo = {}", c)
    assertNotNull(
      c.find { it.id == Bf.Rbac.USER_ID },
      "没有此用户的信息"
    )
  }

  @Test
  fun testFindAllRoleGroupByUser() {
    val testUser = adminService.findUserByAccount(testPlainUser.account)
    val roleGroups = adminService.findAllRoleGroupByUser(testUser!!)
    assertTrue("没有查询到权限") {
      roleGroups.isNotEmpty()
    }
  }

  @Test
  fun testFindAllRoleByAccount() {
    val testUser = adminService.findUserByAccount(testPlainUser.account)
    val roleGroups = adminService.findAllRoleByAccount(testUser!!.account)
    assertTrue("没有查询到权限") {
      roleGroups.isNotEmpty()
    }
  }

  @Test
  fun testFindAllRoleByUser() {
    val testUser = adminService.findUserByAccount(testPlainUser.account)
    val roleGroups = adminService.findAllRoleByUser(testUser!!)
    assertTrue("没有查询到权限") {
      roleGroups.isNotEmpty()
    }
  }

  @Test
  fun testFindAllPermissionsByAccount() {
    val testUser = adminService.findUserByAccount(testPlainUser.account)
    val roleGroups =
      adminService.findAllPermissionsByAccount(testUser!!.account)
    assertTrue("没有查询到权限") {
      roleGroups.isNotEmpty()
    }
  }

  @Test
  fun testFindAllPermissionsByUser() {
    val testUser = adminService.findUserByAccount(testPlainUser.account)
    val roleGroups = adminService.findAllPermissionsByUser(testUser!!)
    assertTrue("没有查询到权限") {
      roleGroups.isNotEmpty()
    }
  }

  @Test
  fun testRevokeRoleGroupByUser() {
    val testUser = adminService.findUserByAccount(testPlainUser.account)

    adminService.revokeRoleGroupByUser(
      testUser!!,
      userRoleGroup
    )

    assertFails("仍然包含普通用户") {
      assertContains(
        adminService.findAllRoleGroupByAccount(testUser.account),
        userRoleGroup,
        "没有包含 用户角色组"
      )
    }
  }

  @Test
  fun testFindAllUserGroupByUser() {
    val user = adminService.registerPlainUser(regDto)!!
    val g = adminService.registerUserGroup(
      UserGroupRegisterDto()
        .apply {
          this.leaderUserAccount = "root"
          this.name = "234"
          this.desc = "我的"
        })!!

    adminService.assignUserToUserGroupById(user.id, g.id)
    adminService.findAllUserGroupByUser(user).apply {
      assertTrue {
        this.isNotEmpty()
      }
    }
  }

  @Test
  fun testAssignUserToUserGroupById() {
    val regDto =
      UserRegisterDto()
        .apply {
          this.account = "wym"
          this.pwd = "qwer1234"
          this.againPwd = "qwer1234"
          this.nickName = "王阳明"
          this.doc = "233"
        }
    val plain = adminService.registerPlainUser(regDto)
    val regUser = assertNotNull(plain, "没有注册用户")

    adminService.assignUserToUserGroupById(regUser.id, Bf.Rbac.ROOT_ID)

    val checkList = adminService.findAllUserGroupByUser(regUser)
    val rootUserGroup = checkList.find { it.id == Bf.Rbac.ROOT_ID }
    assertNotNull(rootUserGroup, "没有分配用户组")
  }

  @Test
  fun findUserById() {
    val testUser = adminService.findUserByAccount(testPlainUser.account)
    assertNotNull(
      adminService.findUserById(testUser!!.id),
      "没有查询到用户"
    )
  }

  @Test
  fun testVerifyPassword() {
    assertTrue("校验密码失败") {
      adminService.verifyPassword(testPlainUser.account, testPlainUser.pwd)
    }
  }

  @Test
  fun testFindUserById() {
    assertNotNull(
      adminService.findUserById(
        adminService.findUserByAccount(testPlainUser.account)!!.id
      ), "没有查询到用户"
    )
  }

  @Test
  fun testFindUserByAccount() {
    assertNotNull(
      adminService.findUserByAccount(testPlainUser.account),
      "没有查询到用户"
    )
  }

  @Test
  fun testDeleteUserByAccount() {
    adminService.deleteUserByAccount(testPlainUser.account)
    assertNull(
      adminService.findUserByAccount(testPlainUser.account),
      "没有删除用户"
    )
  }

  @Test
  fun testRegisterUserGroup() {
    UserGroupRegisterDto()
      .apply {
        this.leaderUserAccount = "root"
        this.desc = "略"
        this.name = "SUDO 组"
        adminService.registerUserGroup(this).apply {
          assertNotNull(this)
        }
      }
  }

  companion object {
    private val log = LogKt.getLog(UserAdminServiceImplTest::class)
  }
}
