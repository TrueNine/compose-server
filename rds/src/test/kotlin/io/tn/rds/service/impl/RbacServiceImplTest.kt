package io.tn.rds.service.impl

import io.tn.core.db.Bf
import io.tn.core.lang.LogKt
import io.tn.rds.RdsRunner
import io.tn.rds.dao.*
import jakarta.annotation.Resource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import kotlin.test.*

@Rollback
@SpringBootTest(classes = [RdsRunner::class])
class RbacServiceImplTest : AbstractTransactionalTestNGSpringContextTests() {

  @Resource
  lateinit var rbacService: RbacServiceImpl

  @Resource
  lateinit var userGroupService: UserGroupServiceImpl

  @Resource
  lateinit var userService: UserServiceImpl

  private lateinit var testUserGroup: UserGroupDao
  private lateinit var testPlainRoleGroup: RoleGroupDao
  private lateinit var testRole: RoleDao
  private lateinit var testUser: UserDao

  @BeforeMethod
  fun init() {
    UserGroupDao().apply {
      this.userId = "0"
      this.name = "fff团"
      testUserGroup = userGroupService.saveUserGroup(this)!!
    }
    testUser = userService.findUserById(Bf.Rbac.ROOT_ID)!!
    testPlainRoleGroup = rbacService.findPlainRoleGroup()
    testRole = rbacService.findRoleById(Bf.Rbac.USER_ID)!!
    rbacService.assignRoleGroupToUserGroup(testPlainRoleGroup, testUserGroup)
  }

  @AfterMethod
  fun destroy() {
    rbacService.revokeAllRoleGroupByUserGroup(testUserGroup)
    userGroupService.deleteUserGroupById(testUserGroup.id)
  }

  @Test
  fun testFindRoleGroupByUserGroup() {
    rbacService.findAllRoleGroupByUserGroup(testUserGroup)
      .apply {
        assertTrue {
          this.isNotEmpty()
        }
      }
  }

  @Test
  fun testFindAllRoleByRoleGroup() {
    rbacService.findRootRoleGroup()
      .apply {
        val a = rbacService.findAllRoleByRoleGroup(this)
        assertTrue {
          a.isNotEmpty()
        }
      }
  }

  @Test
  fun testFindAllPermissionsByRole() {
    rbacService.findAllPermissionsByRole(testRole).apply {
      assertTrue {
        this.isNotEmpty()
      }
    }
  }

  @Test
  fun testFindAllRoleGroupByName() {
    rbacService.findAllRoleGroupByName("ROOT")
      .apply {
        assertTrue {
          this.isNotEmpty()
            && this.size == 1
        }
      }
  }

  @Test
  fun testFindPlainRoleGroup() {
    rbacService.findPlainRoleGroup()
      .apply {
        assertTrue {
          this.id == "1"
        }
      }
  }

  @Test
  fun testFindRootRoleGroup() {
    rbacService.findRootRoleGroup()
      .apply {
        assertTrue {
          this.id == "0"
        }
      }
  }

  @Test
  fun testFindAllRoleByName() {
    rbacService.findAllRoleByName("ROOT")
      .apply {
        assertTrue {
          this.isNotEmpty()
            && this.size == 1
        }
      }
  }

  @Test
  fun testFindAllPermissionsByName() {
    rbacService.findAllPermissionsByName("ROOT")
      .apply {
        assertTrue {
          this.isNotEmpty()
        }
      }
  }

  @Test
  fun testFindAllRoleGroupByUser() {
    rbacService.findAllRoleGroupByUser(testUser)
      .apply {
        assertTrue {
          this.isNotEmpty()
            && this.size == 1
        }
      }
  }

  @Test
  fun testFindAllRoleByUser() {
    rbacService.findAllRoleByUser(testUser)
      .apply {
        assertTrue {
          this.isNotEmpty()
            && this.size == 1
        }
      }
  }

  @Test
  fun testFindAllPermissionsByUser() {
    rbacService.findAllPermissionsByUser(testUser)
      .apply {
        assertTrue("当前大小 = ${this.size}") {
          this.isNotEmpty()
            && this.size == 2
        }
      }
  }

  @Test
  fun testAssignRoleGroupToUser() {
    rbacService.saveRoleGroup(RoleGroupDao().apply {
      this.name = "傻逼组"
      this.doc = "没有描述"
    }).apply {
      rbacService.assignRoleGroupToUser(this, testUser)
      assertContains(
        rbacService.findAllRoleGroupByUser(testUser), this
      )
    }
  }

  @Test
  fun testRevokeRoleGroupByUser() {
    rbacService.revokeRoleGroupByUser(testPlainRoleGroup, testUser)
    rbacService.findAllRoleGroupByUser(testUser).apply {
      assertFails {
        assertContains(
          this,
          testPlainRoleGroup
        )
      }
    }
  }

  @Test
  fun testRevokeAllRoleGroupByUser() {
    rbacService.revokeAllRoleGroupByUser(testUser)
    rbacService.findAllRoleGroupByUser(testUser).apply {
      assertTrue {
        this.isEmpty()
      }
    }
  }

  @Test
  fun testAssignRoleGroupToUserGroup() {
    rbacService.revokeAllRoleGroupByUserGroup(testUserGroup)
    rbacService.assignRoleGroupToUserGroup(testPlainRoleGroup, testUserGroup)
    rbacService.findAllRoleGroupByUserGroup(testUserGroup).apply {
      assertEquals(this.first(), testPlainRoleGroup, "没有分配权限")
    }
  }

  @Test
  fun testRevokeRoleGroupForUserGroup() {
    rbacService.revokeRoleGroupForUserGroup(testPlainRoleGroup, testUserGroup)
    rbacService.findAllRoleGroupByUserGroup(testUserGroup).apply {
      assertFails("仍然包含删除权限") {
        assertContains(this, testPlainRoleGroup)
      }
    }
  }

  @Test
  fun testSaveRoleGroup() {
    RoleGroupDao().apply {
      this.name = "二狗子权限组"
      this.doc = "233"
      val saved = rbacService.saveRoleGroup(this)
      rbacService.findRoleGroupById(saved.id)
    }
  }

  @Test
  fun testAssignRoleToRoleGroup() {
    RoleGroupDao().apply {
      this.name = "二狗子"
      val that = rbacService.saveRoleGroup(this)
      RoleDao().apply {
        this.name = "四狗子"
        val thatRole = rbacService.saveRole(this)
        rbacService.assignRoleToRoleGroup(that, thatRole)
        rbacService.findAllRoleByRoleGroup(that).apply {
          assertContains(this, thatRole, "没有分配权限")
        }
      }
    }
  }

  @Test
  fun testRevokeRoleForRoleGroup() {
    rbacService.revokeRoleForRoleGroup(
      rbacService.findPlainRoleGroup(),
      rbacService.findRoleById(Bf.Rbac.USER_ID)!!
    )
    rbacService.findAllRoleByRoleGroup(rbacService.findPlainRoleGroup())
      .apply {
        assertFails {
          assertContains(
            this,
            rbacService.findRoleById(Bf.Rbac.USER_ID)!!
          )
        }
      }
  }

  @Test
  fun testSaveRole() {
    RoleDao().apply {
      this.name = "二狗子"
      val t = rbacService.saveRole(this)
      assertEquals(t.name, this.name)
      assertNotNull(t.id, "id 查询 为 null")
    }
  }

  @Test
  fun testAssignPermissionsToRole() {
    PermissionsDao().apply {
      this.name = "二狗子"
      val role = rbacService.findRoleById(Bf.Rbac.ROOT_ID)!!
      val per = rbacService.savePermissions(this)
      rbacService.assignPermissionsToRole(
        role,
        per
      )

      rbacService.findAllPermissionsByRole(role).apply {
        assertContains(this, per, "没有分配到权限")
      }
    }
  }

  @Test
  fun testRevokePermissionsForRole() {
    rbacService.findRoleById(Bf.Rbac.ROOT_ID)!!
      .apply {
        val per = rbacService.findPermissionsById(Bf.Rbac.ROOT_ID)!!
        rbacService.revokePermissionsForRole(this, per)
        rbacService.findAllPermissionsByRole(this).apply {
          assertFails("仍然包含分配权限") {
            assertContains(this, per)
          }
        }
      }
  }

  @Test
  fun testSavePermissions() {
    PermissionsDao().apply {
      this.name = "二狗子"
      val t = rbacService.savePermissions(this)
      assertEquals(t.name, this.name)
      assertNotNull(t.id, "id 查询 为 null")
    }
  }

  @Test
  fun testDeleteRoleGroup() {
    val roleGroup = rbacService.findRoleGroupById(Bf.Rbac.ROOT_ID)!!
    rbacService.deleteRoleGroup(
      roleGroup
    )
    assertNull(rbacService.findRoleGroupById(Bf.Rbac.ROOT_ID))
  }

  @Test
  fun testDeleteRole() {
    val role = rbacService.findRoleById(Bf.Rbac.ROOT_ID)!!
    rbacService.deleteRole(
      role
    )
    assertNull(rbacService.findRoleById(Bf.Rbac.ROOT_ID))
  }

  @Test
  fun testDeletePermissions() {
    val per = rbacService.findPermissionsById(Bf.Rbac.ROOT_ID)!!
    rbacService.deletePermissions(
      per
    )
    assertNull(rbacService.findPermissionsById(Bf.Rbac.ROOT_ID))
  }

  @Test
  fun testRevokeAllRoleGroupByUserGroup() {
    rbacService.revokeAllRoleGroupByUserGroup(testUserGroup)
    rbacService.findAllRoleGroupByUserGroup(testUserGroup).apply {
      assertTrue {
        this.isEmpty()
      }
    }
  }

  @Test
  fun testFindRoleById() {
    assertNotNull(rbacService.findRoleById(Bf.Rbac.ROOT_ID))
  }

  @Test
  fun testFindPermissionsById() {
    assertNotNull(rbacService.findPermissionsById(Bf.Rbac.ROOT_ID))
  }

  @Test
  fun testFindRoleGroupById() {
    assertNotNull(rbacService.findRoleGroupById(Bf.Rbac.ROOT_ID))
  }

  companion object {
    private val log = LogKt.getLog(RbacServiceImplTest::class)
  }
}
