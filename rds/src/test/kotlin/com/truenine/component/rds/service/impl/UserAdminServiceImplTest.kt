package com.truenine.component.rds.service.impl

import com.truenine.component.core.consts.DataBaseBasicFieldNames
import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.entity.RoleGroupEntity
import com.truenine.component.rds.entity.UserInfoEntity
import com.truenine.component.rds.models.req.PostUserGroupRequestParam
import com.truenine.component.rds.models.req.PostUserRequestParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.time.LocalDate
import kotlin.test.*

@Rollback
@SpringBootTest(classes = [RdsEntrance::class])
class UserAdminServiceImplTest :
  AbstractTransactionalTestNGSpringContextTests() {

  @Autowired
  lateinit var adminService: UserAdminServiceImpl

  @Autowired
  private lateinit var rbacService: RbacServiceImpl

  private val regReq =
    PostUserRequestParam(
      "001",
      "zliq",
      "qwer1234",
      "qwer1234",
      null
    )
  private val testPlainUser =
    PostUserRequestParam(
      "303",
      "truenine",
      "qwer1234",
      "qwer1234",
      null
    )

  private lateinit var userRoleGroup: RoleGroupEntity
  private lateinit var rootUserGroup: RoleGroupEntity

  /**
   * 注册一个普通用户，
   * 以及获取普通权限
   */
  @BeforeMethod
  @Rollback
  fun init() {
    userRoleGroup = rbacService.findPlainRoleGroup()
    rootUserGroup = rbacService.findRootRoleGroup()
    adminService.registerPlainUser(testPlainUser)
  }

  @Test
  @Rollback
  fun testRegisterPlainUser() {
    val regUsr = adminService.registerPlainUser(regReq)
    assertNotNull(
      regUsr,
      "没有注册用户"
    )
    val allRoleGroup = adminService.findAllRoleGroupByUser(regUsr)
    assertContains(allRoleGroup, userRoleGroup, "没有分配权限")

    val illegalParameters = PostUserRequestParam().apply {
      account = ""
      pwd = ""
    }

    assertFailsWith<IllegalArgumentException>("保存了不该保存的参数") {
      adminService.registerPlainUser(illegalParameters)
    }

    assertFailsWith<IllegalArgumentException>("参数校验不通过，保存了同一个用户") {
      adminService.registerPlainUser(regReq)
    }
  }

  @Test
  @Rollback
  fun testRegisterRootUser() {
    val newUser = PostUserRequestParam().apply {
      this.account = "1qweqwe"
      this.pwd = "qwer1234"
      this.againPwd = "qwer1234"
      this.nickName = "王麻子"
    }
    val rootUser = adminService.registerRootUser(newUser)!!
    val allRoleGroup = adminService.findAllRoleGroupByUser(rootUser)
    assertContains(allRoleGroup, rootUserGroup, "没有分配权限")
  }

  @Test
  @Rollback
  fun testCompletionUserInfo() {
    val saveInfo = adminService.completionUserInfo(
      UserInfoEntity().apply {
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
  @Rollback
  fun testCompletionUserInfoByAccount() {
    val saveInfo = adminService
      .completionUserInfoByAccount("root",
        UserInfoEntity()
          .apply {
            userId = "0"
            this.birthday = LocalDate.of(1997, 11, 4)
            this.phone = "15675292005"
            this.email = "truenine@qq.com"
            this.gender = 1
            this.firstName = "c"
            this.lastName = "bb"
          })
    assertNotNull(
      saveInfo,
      "添加信息没有成功"
    )
  }

  @Test
  @Rollback
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
  @Rollback
  fun testFindUsrVoByAccount() {
    val c =
      adminService.findUserAuthorizationModelByAccount(testPlainUser.account)
    log.debug("usrVo = {}", c)
    assertNotNull(c?.user, "没有此用户的信息")
  }

  @Test
  @Rollback
  fun testFindAllRoleGroupByAccount() {
    val c = adminService.findAllRoleByAccount(testPlainUser.account)
    log.debug("usrVo = {}", c)
    assertNotNull(
      c.find { it.id == DataBaseBasicFieldNames.Rbac.USER_ID },
      "没有此用户的信息"
    )
  }

  @Test
  @Rollback
  fun testFindAllRoleGroupByUser() {
    val testUser = adminService.findUserByAccount(testPlainUser.account)
    val roleGroups = adminService.findAllRoleGroupByUser(testUser!!)
    assertTrue("没有查询到权限") {
      roleGroups.isNotEmpty()
    }
  }

  @Test
  @Rollback
  fun testFindAllRoleByAccount() {
    val testUser = adminService.findUserByAccount(testPlainUser.account)
    val roleGroups = adminService.findAllRoleByAccount(testUser!!.account)
    assertTrue("没有查询到权限") {
      roleGroups.isNotEmpty()
    }
  }

  @Test
  @Rollback
  fun testFindAllRoleByUser() {
    val testUser = adminService.findUserByAccount(testPlainUser.account)
    val roleGroups = adminService.findAllRoleByUser(testUser!!)
    assertTrue("没有查询到权限") {
      roleGroups.isNotEmpty()
    }
  }

  @Test
  @Rollback
  fun testFindAllPermissionsByAccount() {
    val testUser = adminService.findUserByAccount(testPlainUser.account)
    val roleGroups =
      adminService.findAllPermissionsByAccount(testUser!!.account)
    assertTrue("没有查询到权限") {
      roleGroups.isNotEmpty()
    }
  }

  @Test
  @Rollback
  fun testFindAllPermissionsByUser() {
    val testUser = adminService.findUserByAccount(testPlainUser.account)
    val roleGroups = adminService.findAllPermissionsByUser(testUser!!)
    assertTrue("没有查询到权限") {
      roleGroups.isNotEmpty()
    }
  }

  @Test
  @Rollback
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
  @Rollback
  fun testFindAllUserGroupByUser() {
    val user = adminService.registerPlainUser(regReq)!!
    val g = adminService.registerUserGroup(
      PostUserGroupRequestParam()
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
  @Rollback
  fun testAssignUserToUserGroupById() {
    val regDto =
      PostUserRequestParam()
        .apply {
          this.account = "wym"
          this.pwd = "qwer1234"
          this.againPwd = "qwer1234"
          this.nickName = "王阳明"
          this.doc = "233"
        }
    val plain = adminService.registerPlainUser(regDto)
    val regUser = assertNotNull(plain, "没有注册用户")

    adminService.assignUserToUserGroupById(regUser.id, DataBaseBasicFieldNames.Rbac.ROOT_ID)

    val checkList = adminService.findAllUserGroupByUser(regUser)
    val rootUserGroup = checkList.find { it.id == DataBaseBasicFieldNames.Rbac.ROOT_ID }
    assertNotNull(rootUserGroup, "没有分配用户组")
  }

  @Test
  @Rollback
  fun findUserById() {
    val testUser = adminService.findUserByAccount(testPlainUser.account)
    assertNotNull(
      adminService.findUserById(testUser!!.id),
      "没有查询到用户"
    )
  }

  @Test
  @Rollback
  fun testVerifyPassword() {
    assertTrue("校验密码失败") {
      adminService.verifyPassword(testPlainUser.account, testPlainUser.pwd)
    }
  }

  @Test
  @Rollback
  fun testFindUserById() {
    assertNotNull(
      adminService.findUserById(
        adminService.findUserByAccount(testPlainUser.account)!!.id
      ), "没有查询到用户"
    )
  }

  @Test
  @Rollback
  fun testFindUserByAccount() {
    assertNotNull(
      adminService.findUserByAccount(testPlainUser.account),
      "没有查询到用户"
    )
  }

  @Test
  @Rollback
  fun testDeleteUserByAccount() {
    adminService.deleteUserByAccount(testPlainUser.account)
    assertNull(
      adminService.findUserByAccount(testPlainUser.account),
      "没有删除用户"
    )
  }

  @Test
  @Rollback
  fun testRegisterUserGroup() {
    PostUserGroupRequestParam()
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
