package com.truenine.component.rds.service.aggregator

import com.truenine.component.core.id.Snowflake
import com.truenine.component.rds.entity.*
import com.truenine.component.rds.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import kotlin.test.*

@SpringBootTest
class RbacAggregatorImplTest : AbstractTestNGSpringContextTests() {

  @Autowired
  lateinit var aggregator: RbacAggregator

  @Autowired
  lateinit var userService: UserService

  @Autowired
  lateinit var ugService: UserGroupService

  @Autowired
  lateinit var roleService: RoleService

  @Autowired
  lateinit var rgService: RoleGroupService

  @Autowired
  lateinit var permissionsService: PermissionsService

  @Autowired
  lateinit var snowflake: Snowflake

  private fun getUser() = UserEntity().apply {
    account = "name:${snowflake.nextId()}"
    nickName = "abcd"
    pwdEnc = "aa${snowflake.nextId()}"
  }

  private fun getRoleGroup() = RoleGroupEntity().apply {
    name = "ab${snowflake.nextId()}"
  }

  @Test
  fun testSaveRoleGroupToUser() {
    val user = userService.save(getUser())
    val rg = rgService.save(getRoleGroup())

    assertNotNull(user)
    assertNotNull(rg)

    val link = aggregator.saveRoleGroupToUser(rg.id, user.id)
    assertNotNull(link)

    assertEquals(link.roleGroupId, rg.id)
    assertEquals(link.userId, user.id)
  }

  fun getAllRoleGroup() = List(20) {
    getRoleGroup()
  }

  @Test
  fun testSaveAllRoleGroupToUser() {
    val rgs = rgService.saveAll(getAllRoleGroup())
    val u = userService.save(getUser())

    assertNotNull(rgs)
    assertNotNull(u)
  }

  @Test
  fun testRevokeRoleGroupFromUser() {
    val rg = rgService.save(getRoleGroup())
    val u = userService.save(getUser())
    assertNotNull(rg)
    assertNotNull(u)
    val saved = aggregator.saveRoleGroupToUser(rg.id, u.id)
    assertNotNull(saved)

    aggregator.revokeRoleGroupFromUser(saved.roleGroupId, saved.userId)
    val nu = userService.findById(u.id)
    assertNotNull(nu)
    assertTrue { nu.roleGroups.isEmpty() }
  }

  private fun getRoleGroups() = List(10) { getRoleGroup() }

  @Test
  fun testRevokeAllRoleGroupFromUser() {
    val rgs = rgService.saveAll(getRoleGroups())
    val u = userService.save(getUser())
    assertNotNull(u)

    val urs = aggregator.saveAllRoleGroupToUser(rgs.map { it.id }, u.id)
    assertTrue { urs.size == rgs.size }

    val queryUser = userService.findById(u.id)
    assertNotNull(queryUser)
    assertNotNull(queryUser.roleGroups)
    assertTrue { queryUser.roleGroups.size > 0 }

    assertEquals(queryUser.roleGroups.size, rgs.size)

    aggregator.revokeAllRoleGroupFromUser(rgs.map { it.id }, u.id)
    val nu = userService.findById(u.id)
    assertNotNull(nu)
    assertTrue {
      nu.roleGroups.isEmpty()
    }
  }

  fun getUserGroup() = UserGroupEntity().apply {
    this.userId = snowflake.nextId()
    this.name = "abc" + snowflake.nextId()
  }

  fun getUserGroups() = List(10) {
    getUserGroup()
  }

  @Test
  fun testSaveRoleGroupToUserGroup() {
    val ug = ugService.save(getUserGroup())
    val rg = rgService.save(getRoleGroup())

    assertNotNull(ug)
    assertNotNull(rg)

    val saved = aggregator.saveRoleGroupToUserGroup(rg.id, ug.id)
    assertNotNull(saved)

    assertEquals(saved.roleGroupId, rg.id)
    assertEquals(saved.userGroupId, ug.id)
  }

  @Test
  fun testSaveAllRoleGroupToUserGroup() {
    var rgs = rgService.saveAll(getRoleGroups())
    val ug = ugService.save(getUserGroup())
    assertNotNull(rgs)
    assertNotNull(ug)

    val saved = aggregator.saveAllRoleGroupToUserGroup(rgs.map { it.id }, ug.id)
    assertNotNull(saved)
    assertEquals(saved.size, rgs.size)
    val su = ugService.findById(ug.id)
    assertNotNull(su)
    assertNotNull(su.roleGroups)
    su.roleGroups.sortBy { it.id }
    rgs = rgs.sortedBy { it.id }
    su.roleGroups.forEachIndexed { idx, it ->
      assertEquals(it, rgs[idx])
    }
  }

  @Test
  fun testRevokeRoleGroupFromUserGroup() {
    val rg = rgService.save(getRoleGroup())
    val ug = ugService.save(getUserGroup())

    assertNotNull(rg)
    assertNotNull(ug)

    val urg = aggregator.saveRoleGroupToUserGroup(rg.id, ug.id)
    assertNotNull(urg)

    val saved = ugService.findById(ug.id)
    assertNotNull(saved)
    assertContains(saved.roleGroups, rg)

    aggregator.revokeRoleGroupFromUserGroup(rg.id, ug.id)

    val cb = ugService.findById(ug.id)
    assertNotNull(cb)

    assertTrue {
      cb.roleGroups.isEmpty()
    }
  }

  @Test
  fun testRevokeAllRoleGroupFromUserGroup() {
    val ug = ugService.save(getUserGroup())
    val rgs = rgService.saveAll(getRoleGroups())

    assertNotNull(ug)
    assertNotNull(rgs)

    val saved = aggregator.saveAllRoleGroupToUserGroup(rgs.map { it.id }, ug.id)
    assertEquals(saved.size, rgs.size)

    val su = ugService.findById(ug.id)
    assertNotNull(su)
    aggregator.revokeAllRoleGroupFromUserGroup(saved.map { it.roleGroupId }, ug.id)
    ugService.findById(ug.id).let {
      assertNotNull(it)
      assertTrue { it.roleGroups.isEmpty() }
    }
  }

  fun getRole() = RoleEntity().apply {
    name = "测试权限${snowflake.nextId()}"
    doc = "nul"
  }

  @Test
  fun testSaveRoleToRoleGroup() {
    val r = roleService.save(getRole())!!
    val rg = rgService.save(getRoleGroup())!!
    aggregator.saveRoleToRoleGroup(r.id, rg.id)!!
    val srg = rgService.findById(rg.id)!!
    assertContains(srg.roles, r)
  }

  fun getRoles() = List(10) {
    RoleEntity().apply {
      name = "没有${snowflake.nextId()}"
      doc = "md = ${snowflake.nextId()}"
    }
  }

  @Test
  fun testSaveAllRoleToRoleGroup() {
    roleService.saveAll(getRoles()).let { rs ->
      rgService.save(getRoleGroup())!!.let { rg ->
        aggregator.saveAllRoleToRoleGroup(rs.map { it.id }, rg.id).let { sru ->
          rgService.findById(rg.id)!!.let { nr ->
            assertEquals(nr.roles.size, rs.size)
          }
        }
      }
    }
  }

  @Test
  fun testRevokeRoleFromRoleGroup() {
    roleService.save(getRole())!!.let { r ->
      rgService.save(getRoleGroup())!!.let { rg ->
        aggregator.saveRoleToRoleGroup(r.id, rg.id)!!.let { rgr ->
          aggregator.revokeRoleFromRoleGroup(r.id, rg.id)
          val srg = rgService.findById(rg.id)!!
          assertTrue { srg.roles.isEmpty() }
        }
      }
    }
  }


  @Test
  fun testRevokeAllRoleFromRoleGroup() {
    roleService.saveAll(getRoles()).let { rs ->
      rgService.save(getRoleGroup())!!.let { rg ->
        aggregator.saveAllRoleToRoleGroup(rs.map { it.id }, rg.id).let { rgr ->
          aggregator.revokeAllRoleFromRoleGroup(rs.map { it.id }, rg.id)
          rgService.findById(rg.id)!!.let {
            assertTrue {
              it.roles == null
                || it.roles.isEmpty()
            }
          }
        }
      }
    }
  }

  fun getPermissions() = PermissionsEntity().apply {
    name = "权限 ${snowflake.nextId()}"
    doc = "stra ${snowflake.nextId()}"
  }

  @Test
  fun testSavePermissionsToRole() {
    permissionsService.save(getPermissions())!!.let { p ->
      roleService.save(getRole())!!.let { r ->
        aggregator.savePermissionsToRole(p.id, r.id)!!
        val rl = roleService.findById(r.id)!!
        assertContains(rl.permissions, p)
      }
    }
  }

  fun getAllPermissions() = List(10) {
    getPermissions()
  }

  @Test
  fun testSaveAllPermissionsToRole() {
    permissionsService.saveAll(getAllPermissions()).let { ps ->
      roleService.save(getRole())!!.let { r ->
        aggregator.saveAllPermissionsToRole(ps.map { it.id }, r.id).let { all ->
          assertTrue { all.isNotEmpty() }
          roleService.findById(r.id)!!.let { sr ->
            ps.forEach {
              assertContains(sr.permissions, it)
            }
          }
        }
      }
    }
  }

  @Test
  fun testRevokePermissionsFromRole() {
    permissionsService.save(getPermissions())!!.let { p ->
      roleService.save(getRole())!!.let { r ->
        aggregator.savePermissionsToRole(p.id, r.id).let {
          aggregator.revokePermissionsFromRole(p.id, r.id)
          roleService.findById(r.id)!!.let { sr ->
            sr.permissions.forEach {
              assertFalse {
                sr.permissions.contains(p)
              }
            }
          }
        }
      }
    }
  }

  @Test
  fun testRevokeAllPermissionsFromRole() {
    roleService.save(getRole())!!.let { r ->
      permissionsService.saveAll(getAllPermissions()).let { ps ->
        aggregator.saveAllPermissionsToRole(ps.map { it.id }, r.id).let { srp ->
          aggregator.revokeAllPermissionsFromRole(ps.map { it.id }, r.id)
          roleService.findById(r.id)!!.let {
            assertTrue { it.permissions.isEmpty() }
          }
        }
      }
    }
  }
}