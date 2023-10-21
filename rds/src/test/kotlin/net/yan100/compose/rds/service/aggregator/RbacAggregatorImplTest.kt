package net.yan100.compose.rds.service.aggregator

import net.yan100.compose.core.id.Snowflake
import net.yan100.compose.rds.entity.Permissions
import net.yan100.compose.rds.entity.Role
import net.yan100.compose.rds.entity.RoleGroup
import net.yan100.compose.rds.entity.UserGroup
import net.yan100.compose.rds.repository.AllRoleEntityRepository
import net.yan100.compose.rds.repository.FullRoleGroupEntityRepo
import net.yan100.compose.rds.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import kotlin.test.*

@SpringBootTest
class RbacAggregatorImplTest : AbstractTestNGSpringContextTests() {
  @Autowired
  lateinit var argRepo: FullRoleGroupEntityRepo

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

  private fun getUser() = net.yan100.compose.rds.entity.User().apply {
    account = "name:${snowflake.nextId()}"
    nickName = "abcd"
    pwdEnc = "aa${snowflake.nextId()}"
  }

  private fun getRoleGroup() = RoleGroup().apply {
    name = "ab${snowflake.nextId()}"
  }

  @Test
  fun testSaveRoleGroupToUser() {
    val user = userService.save(getUser())
    val rg = rgService.save(getRoleGroup())

    assertNotNull(user)
    assertNotNull(rg)

    val link = aggregator.saveRoleGroupToUser(rg.id!!, user.id!!)
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
    val saved = aggregator.saveRoleGroupToUser(rg.id!!, u.id!!)
    assertNotNull(saved)

    aggregator.revokeRoleGroupFromUser(saved.roleGroupId, saved.userId)
    val nu = userService.findFullUserByAccount(u.account!!)
    assertNotNull(nu)
    assertTrue { nu.roleGroups.isEmpty() }
  }

  private fun getRoleGroups() = List(10) { getRoleGroup() }

  @Test
  fun testRevokeAllRoleGroupFromUser() {
    val rgs = rgService.saveAll(getRoleGroups())
    val u = userService.save(getUser())
    assertNotNull(u)

    val urs = aggregator.saveAllRoleGroupToUser(rgs.map { it.id!! }, u.id!!)
    assertTrue { urs.size == rgs.size }

    val queryUser = userService.findFullUserByAccount(u.account!!)
    assertNotNull(queryUser)
    assertNotNull(queryUser.roleGroups)
    assertTrue { queryUser.roleGroups.isNotEmpty() }

    assertEquals(queryUser.roleGroups.size, rgs.size)

    aggregator.revokeAllRoleGroupFromUser(rgs.map { it.id!! }, u.id!!)
    val nu = userService.findFullUserByAccount(u.account!!)
    assertNotNull(nu)
    assertTrue {
      nu.roleGroups.isEmpty()
    }
  }

  fun getUserGroup() = UserGroup().apply {
    this.userId = snowflake.nextStringId()
    this.name = "abc" + snowflake.nextId()
  }

  @Test
  fun testSaveRoleGroupToUserGroup() {
    val ug = ugService.save(getUserGroup())
    val rg = rgService.save(getRoleGroup())

    assertNotNull(ug)
    assertNotNull(rg)

    val saved = aggregator.saveRoleGroupToUserGroup(rg.id!!, ug.id!!)
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

    val saved = aggregator.saveAllRoleGroupToUserGroup(rgs.map { it.id!! }, ug.id!!)
    assertNotNull(saved)
    assertEquals(saved.size, rgs.size, "saved $saved \n rgs$rgs")
    val su = ugService.findById(ug.id!!)
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

    val urg = aggregator.saveRoleGroupToUserGroup(rg.id!!, ug.id!!)
    assertNotNull(urg)

    val saved = ugService.findById(ug.id!!)
    assertNotNull(saved)
    assertContains(saved.roleGroups, rg)

    aggregator.revokeRoleGroupFromUserGroup(rg.id!!, ug.id!!)

    val cb = ugService.findById(ug.id!!)
    assertNotNull(cb)

    assertTrue {
      cb.roleGroups.isEmpty()
    }
  }

  @Test
  fun testRevokeAllRoleGroupFromUserGroup() {
    val ug = ugService.save(getUserGroup())
    val rgs = rgService.saveAll(getRoleGroups())

    val saved = aggregator.saveAllRoleGroupToUserGroup(rgs.map { it.id!! }, ug.id!!)
    assertEquals(saved.size, rgs.size)

    val su = ugService.findById(ug.id!!)
    assertNotNull(su)
    aggregator.revokeAllRoleGroupFromUserGroup(saved.map { it.roleGroupId }, ug.id!!)
    ugService.findById(ug.id!!).let {
      assertNotNull(it)
      assertTrue { it.roleGroups.isEmpty() }
    }
  }

  fun getRole() = Role().apply {
    name = "测试权限${snowflake.nextId()}"
    doc = "nul"
  }

  @Test
  fun testSaveRoleToRoleGroup() {
    val r = roleService.save(getRole())
    val rg = rgService.save(getRoleGroup())
    aggregator.saveRoleToRoleGroup(r.id!!, rg.id!!)!!
    val srg = argRepo.findByIdOrNull(rg.id!!)!!
    assertContains(srg.roles.map { it.id }, r.id)
  }

  fun getRoles() = List(10) {
    Role().apply {
      name = "没有${snowflake.nextId()}"
      doc = "md = ${snowflake.nextId()}"
    }
  }

  @Test
  fun testSaveAllRoleToRoleGroup() {
    roleService.saveAll(getRoles()).let { rs ->
      rgService.save(getRoleGroup()).let { rg ->
        aggregator.saveAllRoleToRoleGroup(rs.map { it.id!! }, rg.id!!).let { sru ->
          argRepo.findByIdOrNull(rg.id!!)!!.let { nr ->
            assertEquals(nr.roles.size, rs.size)
          }
        }
      }
    }
  }

  @Test
  fun testRevokeRoleFromRoleGroup() {
    roleService.save(getRole()).let { r ->
      rgService.save(getRoleGroup()).let { rg ->
        aggregator.saveRoleToRoleGroup(r.id!!, rg.id!!)!!.let { rgr ->
          aggregator.revokeRoleFromRoleGroup(r.id!!, rg.id!!)
          val srg = argRepo.findByIdOrNull(rg.id!!)!!
          assertTrue { srg.roles.isEmpty() }
        }
      }
    }
  }


  @Test
  fun testRevokeAllRoleFromRoleGroup() {
    roleService.saveAll(getRoles()).let { rs ->
      rgService.save(getRoleGroup()).let { rg ->
        aggregator.saveAllRoleToRoleGroup(rs.map { it.id!! }, rg.id!!).let { rgr ->
          aggregator.revokeAllRoleFromRoleGroup(rs.map { it.id!! }, rg.id!!)
          argRepo.findByIdOrNull(rg.id!!)!!.let {
            assertTrue {
              it.roles.isEmpty()
            }
          }
        }
      }
    }
  }

  fun getPermissions() = Permissions().apply {
    name = "权限 ${snowflake.nextId()}"
    doc = "stra ${snowflake.nextId()}"
  }

  @Autowired
  lateinit var arRepo: AllRoleEntityRepository

  @Test
  fun testSavePermissionsToRole() {
    permissionsService.save(getPermissions()).let { p ->
      roleService.save(getRole()).let { r ->
        aggregator.savePermissionsToRole(p.id!!, r.id!!)!!
        val rl = arRepo.findByIdOrNull(r.id!!)!!
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
      roleService.save(getRole()).let { r ->
        aggregator.saveAllPermissionsToRole(ps.map { it.id!! }, r.id!!).let { all ->
          assertTrue("all$all") { all.isNotEmpty() }
          arRepo.findByIdOrNull(r.id!!)!!.let { sr ->
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
    permissionsService.save(getPermissions()).let { p ->
      roleService.save(getRole()).let { r ->
        aggregator.savePermissionsToRole(p.id!!, r.id!!).let {
          aggregator.revokePermissionsFromRole(p.id!!, r.id!!)
          arRepo.findByIdOrNull(r.id!!)!!.let { sr ->
            repeat(sr.permissions.size) {
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
    roleService.save(getRole()).let { r ->
      permissionsService.saveAll(getAllPermissions()).let { ps ->
        aggregator.saveAllPermissionsToRole(ps.map { it.id!! }, r.id!!).let { srp ->
          aggregator.revokeAllPermissionsFromRole(ps.map { it.id!! }, r.id!!)
          arRepo.findByIdOrNull(r.id!!)!!.let {
            assertTrue { it.permissions.isEmpty() }
          }
        }
      }
    }
  }

  fun namePre(): net.yan100.compose.rds.entity.User {
    val user = userService.save(getUser())
    ugService.save(UserGroup().apply { name = "柱";userId = user.id })
    val subUserGroup = ugService.save(getUserGroup())
    ugService.saveUserToUserGroup(user.id!!, subUserGroup.id!!)

    // 权限
    val userRoleGroups = rgService.saveAll(getRoleGroups())
    val ugRoleGroups = rgService.saveAll(getRoleGroups())
    val aRole = roleService.save(getRole())
    val bRole = roleService.save(getRole())
    val aPer = permissionsService.save(getPermissions())
    val bPer = permissionsService.save(getPermissions())

    // 链接
    aggregator.savePermissionsToRole(aPer.id!!, aRole.id!!)!!
    aggregator.savePermissionsToRole(bPer.id!!, bRole.id!!)!!

    // 分别挂载
    userRoleGroups.map { it.id }.let {
      it.forEach { rid ->
        aggregator.saveRoleToRoleGroup(aRole.id!!, rid!!)
      }
      aggregator.saveAllRoleGroupToUser(it.map { ir -> ir!! }, user.id!!)
    }
    ugRoleGroups.map { it.id }.let {
      it.forEach { rid ->
        aggregator.saveRoleToRoleGroup(bRole.id!!, rid!!)
      }
      aggregator.saveAllRoleGroupToUserGroup(it.map { ir -> ir!! }, subUserGroup.id!!)
    }

    // 校验挂载是否通过
    val newUser = userService.findFullUserByAccount(user.account!!)!!
    val newUserGroup = ugService.findById(subUserGroup.id!!)!!
    assertTrue { newUser.roleGroups.containsAll(userRoleGroups) }
    assertTrue { newUserGroup.roleGroups.containsAll(ugRoleGroups) }
    return user
  }

  @Test
  fun testFindAllSecurityNameByUserId() {
    val user = namePre()
    val pUser = userService.findUserByAccount("usr")
    

    // 查询用户
    val all = aggregator.findAllSecurityNameByUserId(user.id!!)
    assertTrue { all.isNotEmpty() }
  }

  @Test
  fun testFindAllSecurityNameByAccount() {
    val user = namePre()
    val all = aggregator.findAllSecurityNameByAccount(user.account!!)
    assertTrue { all.isNotEmpty() }
  }

  @Test
  fun testFindAllRoleNameByUserAccount() {
    val user = namePre()
    val acl = aggregator.findAllRoleNameByUserAccount(user.account!!)
    assertTrue("查询不到权限 $acl") { acl.isNotEmpty() }
    assertTrue("权限数值不对 $acl") { acl.size == 1 }
  }

  @Test
  fun testFindAllPermissionsNameByUserAccount() {
    val user = namePre()
    val acl = aggregator.findAllPermissionsNameByUserAccount(user.account!!)
    assertTrue("查询不到权限 $acl") { acl.isNotEmpty() }
    assertTrue("权限数值不对 $acl") { acl.size == 1 }
  }
}
