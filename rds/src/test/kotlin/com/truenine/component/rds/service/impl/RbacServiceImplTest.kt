package com.truenine.component.rds.service.impl

import com.truenine.component.core.consts.DataBaseBasicFieldNames
import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.entity.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import kotlin.test.*


@Rollback
@SpringBootTest(classes = [RdsEntrance::class])
class RbacServiceImplTest : AbstractTransactionalTestNGSpringContextTests() {

  @Autowired
  lateinit var rbacService: RbacServiceImpl

  @Autowired
  lateinit var userGroupService: UserGroupServiceImpl

  @Autowired
  lateinit var userService: UserServiceImpl

  private lateinit var testUserGroup: UserGroupEntity
  private lateinit var testPlainRoleGroup: RoleGroupEntity
  private lateinit var testRole: RoleEntity
  private lateinit var testUser: UserEntity

  @BeforeMethod
  @Rollback
  fun init() {
    UserGroupEntity().apply {
      this.userId = "0"
      this.name = "fff团"
      testUserGroup = userGroupService.saveUserGroup(this)!!
    }
    testUser = userService.findUserById(DataBaseBasicFieldNames.Rbac.ROOT_ID)!!
    testPlainRoleGroup = rbacService.findPlainRoleGroup()
    testRole = rbacService.findRoleById(DataBaseBasicFieldNames.Rbac.USER_ID)!!
    rbacService.assignRoleGroupToUserGroup(testPlainRoleGroup, testUserGroup)
  }

  @Test
  @Rollback
  fun testFindRoleGroupByUserGroup() {
    rbacService.findAllRoleGroupByUserGroup(testUserGroup)
      .apply {
        assertTrue("值不为空 $this") {
          this.isNotEmpty()
        }
      }
  }

  @Test
  @Rollback
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
  @Rollback
  fun testFindAllPermissionsByRole() {
    rbacService.findAllPermissionsByRole(testRole).apply {
      assertTrue {
        this.isNotEmpty()
      }
    }
  }

  @Test
  @Rollback
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
  @Rollback
  fun testFindPlainRoleGroup() {
    rbacService.findPlainRoleGroup()
      .apply {
        assertTrue {
          this.id == "1"
        }
      }
  }

  @Test
  @Rollback
  fun testFindRootRoleGroup() {
    rbacService.findRootRoleGroup()
      .apply {
        assertTrue {
          this.id == "0"
        }
      }
  }

  @Test
  @Rollback
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
  @Rollback
  fun testFindAllPermissionsByName() {
    rbacService.findAllPermissionsByName("ROOT")
      .apply {
        assertTrue {
          this.isNotEmpty()
        }
      }
  }

  @Test
  @Rollback
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
  @Rollback
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
  @Rollback
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
  @Rollback
  fun testAssignRoleGroupToUser() {
    rbacService.saveRoleGroup(
      RoleGroupEntity().apply {
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
  @Rollback
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
  @Rollback
  fun testRevokeAllRoleGroupByUser() {
    rbacService.revokeAllRoleGroupByUser(testUser)
    rbacService.findAllRoleGroupByUser(testUser).apply {
      assertTrue {
        this.isEmpty()
      }
    }
  }

  @Test
  @Rollback
  fun testAssignRoleGroupToUserGroup() {
    rbacService.revokeAllRoleGroupByUserGroup(testUserGroup)
    rbacService.assignRoleGroupToUserGroup(testPlainRoleGroup, testUserGroup)
    rbacService.findAllRoleGroupByUserGroup(testUserGroup).apply {
      assertEquals(this.first(), testPlainRoleGroup, "没有分配权限")
    }
  }

  @Test
  @Rollback
  fun testRevokeRoleGroupForUserGroup() {
    rbacService.revokeRoleGroupForUserGroup(testPlainRoleGroup, testUserGroup)
    rbacService.findAllRoleGroupByUserGroup(testUserGroup).apply {
      assertFails("仍然包含删除权限") {
        assertContains(this, testPlainRoleGroup)
      }
    }
  }

  @Test
  @Rollback
  fun testSaveRoleGroup() {
    RoleGroupEntity().apply {
      this.name = "二狗子权限组"
      this.doc = "233"
      val saved = rbacService.saveRoleGroup(this)
      rbacService.findRoleGroupById(saved.id)
    }
  }

  @Test
  @Rollback
  fun testAssignRoleToRoleGroup() {
    RoleGroupEntity().apply {
      this.name = "二狗子"
      val that = rbacService.saveRoleGroup(this)
      RoleEntity().apply {
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
  @Rollback
  fun testRevokeRoleForRoleGroup() {
    rbacService.revokeRoleForRoleGroup(
      rbacService.findPlainRoleGroup(),
      rbacService.findRoleById(DataBaseBasicFieldNames.Rbac.USER_ID)!!
    )
    rbacService.findAllRoleByRoleGroup(rbacService.findPlainRoleGroup())
      .apply {
        assertFails {
          assertContains(
            this,
            rbacService.findRoleById(DataBaseBasicFieldNames.Rbac.USER_ID)!!
          )
        }
      }
  }

  @Test
  @Rollback
  fun testSaveRole() {
    RoleEntity().apply {
      this.name = "二狗子"
      val t = rbacService.saveRole(this)
      assertEquals(t.name, this.name)
      assertNotNull(t.id, "id 查询 为 null")
    }
  }

  @Test
  @Rollback
  fun testAssignPermissionsToRole() {
    PermissionsEntity().apply {
      this.name = "二狗子"
      val role = rbacService.findRoleById(DataBaseBasicFieldNames.Rbac.ROOT_ID)!!
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
  @Rollback
  fun testRevokePermissionsForRole() {
    rbacService.findRoleById(DataBaseBasicFieldNames.Rbac.ROOT_ID)!!
      .apply {
        val per = rbacService.findPermissionsById(DataBaseBasicFieldNames.Rbac.ROOT_ID)!!
        rbacService.revokePermissionsForRole(this, per)
        rbacService.findAllPermissionsByRole(this).apply {
          assertFails("仍然包含分配权限") {
            assertContains(this, per)
          }
        }
      }
  }

  @Test
  @Rollback
  fun testSavePermissions() {
    PermissionsEntity().apply {
      this.name = "二狗子"
      val t = rbacService.savePermissions(this)
      assertEquals(t.name, this.name)
      assertNotNull(t.id, "id 查询 为 null")
    }
  }

  @Test
  @Rollback
  fun testDeleteRoleGroup() {
    val roleGroup = rbacService.findRoleGroupById(DataBaseBasicFieldNames.Rbac.ROOT_ID)!!
    rbacService.deleteRoleGroup(
      roleGroup
    )
    assertNull(rbacService.findRoleGroupById(DataBaseBasicFieldNames.Rbac.ROOT_ID))
  }

  @Test
  @Rollback
  fun testDeleteRole() {
    val role = rbacService.findRoleById(DataBaseBasicFieldNames.Rbac.ROOT_ID)!!
    rbacService.deleteRole(
      role
    )
    assertNull(rbacService.findRoleById(DataBaseBasicFieldNames.Rbac.ROOT_ID))
  }

  @Test
  @Rollback
  fun testDeletePermissions() {
    val per = rbacService.findPermissionsById(DataBaseBasicFieldNames.Rbac.ROOT_ID)!!
    rbacService.deletePermissions(
      per
    )
    assertNull(rbacService.findPermissionsById(DataBaseBasicFieldNames.Rbac.ROOT_ID))
  }

  @Test
  @Rollback
  fun testRevokeAllRoleGroupByUserGroup() {
    rbacService.revokeAllRoleGroupByUserGroup(testUserGroup)
    rbacService.findAllRoleGroupByUserGroup(testUserGroup).apply {
      assertTrue {
        this.isEmpty()
      }
    }
  }

  @Test
  @Rollback
  fun testFindRoleById() {
    assertNotNull(rbacService.findRoleById(DataBaseBasicFieldNames.Rbac.ROOT_ID))
  }

  @Test
  @Rollback
  fun testFindPermissionsById() {
    assertNotNull(rbacService.findPermissionsById(DataBaseBasicFieldNames.Rbac.ROOT_ID))
  }

  @Test
  @Rollback
  fun testFindRoleGroupById() {
    assertNotNull(rbacService.findRoleGroupById(DataBaseBasicFieldNames.Rbac.ROOT_ID))
  }

  companion object {
    private val log = LogKt.getLog(RbacServiceImplTest::class)
  }
}
