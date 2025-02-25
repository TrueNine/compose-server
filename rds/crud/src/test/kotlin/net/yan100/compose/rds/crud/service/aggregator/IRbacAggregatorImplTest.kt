package net.yan100.compose.rds.crud.service.aggregator

import jakarta.annotation.Resource
import kotlin.test.*
import net.yan100.compose.core.generator.ISnowflakeGenerator
import net.yan100.compose.rds.crud.entities.jpa.Permissions
import net.yan100.compose.rds.crud.entities.jpa.Role
import net.yan100.compose.rds.crud.entities.jpa.RoleGroup
import net.yan100.compose.rds.crud.entities.jpa.UserAccount
import net.yan100.compose.rds.crud.repositories.jpa.IFullRoleGroupRepo
import net.yan100.compose.rds.crud.repositories.jpa.IFullRoleRepo
import net.yan100.compose.rds.crud.service.IPermissionsService
import net.yan100.compose.rds.crud.service.IRoleService
import net.yan100.compose.rds.crud.service.IUserAccountService
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class IRbacAggregatorImplTest {
  lateinit var argRepo: IFullRoleGroupRepo
    @Resource set

  lateinit var aggregator: IRbacAggregator
    @Resource set

  lateinit var userService: IUserAccountService
    @Resource set

  lateinit var roleService: IRoleService
    @Resource set

  lateinit var rgService: net.yan100.compose.rds.crud.service.IRoleGroupService
    @Resource set

  lateinit var permissionsService: IPermissionsService
    @Resource set

  lateinit var snowflake: ISnowflakeGenerator
    @Resource set

  private fun getUser() =
    UserAccount().apply {
      account = "name${snowflake.next()}"
      nickName = "abcd"
      pwdEnc = "aa${snowflake.next()}"
    }

  private fun getRoleGroup() =
    RoleGroup().apply { name = "ab${snowflake.next()}" }

  @Test
  fun testSaveRoleGroupToUser() {
    val user = userService.post(getUser())
    val rg = rgService.post(getRoleGroup())

    assertNotNull(user)
    assertNotNull(rg)

    val link = aggregator.saveRoleGroupToUser(rg.id, user.id)
    assertNotNull(link)

    assertEquals(link.roleGroupId, rg.id)
    assertEquals(link.userId, user.id)
  }

  fun getAllRoleGroup() = List(20) { getRoleGroup() }

  @Test
  fun testSaveAllRoleGroupToUser() {
    val rgs = rgService.postAll(getAllRoleGroup())
    val u = userService.post(getUser())

    assertNotNull(rgs)
    assertNotNull(u)
  }

  @Test
  fun testRevokeRoleGroupFromUser() {
    val rg = rgService.post(getRoleGroup())
    val u = userService.post(getUser())
    assertNotNull(rg)
    assertNotNull(u)
    val saved = aggregator.saveRoleGroupToUser(rg.id, u.id)
    assertNotNull(saved)

    aggregator.revokeRoleGroupFromUser(saved.roleGroupId, saved.userId)
    val nu = userService.findFullUserByAccount(u.account)
    assertNotNull(nu)
    assertTrue { nu.roleGroups.isEmpty() }
  }

  private fun getRoleGroups() = List(10) { getRoleGroup() }

  @Test
  fun testRevokeAllRoleGroupFromUser() {
    val rgs = rgService.postAll(getRoleGroups())
    val u = userService.post(getUser())
    assertNotNull(u)

    val urs = aggregator.saveAllRoleGroupToUser(rgs.map { it.id }, u.id)
    assertTrue { urs.size == rgs.size }

    val queryUser = userService.findFullUserByAccount(u.account)
    assertNotNull(queryUser)
    assertNotNull(queryUser.roleGroups)
    assertTrue { queryUser.roleGroups.isNotEmpty() }

    assertEquals(queryUser.roleGroups.size, rgs.size)

    aggregator.revokeAllRoleGroupFromUser(rgs.map { it.id }, u.id)
    val nu = userService.findFullUserByAccount(u.account)
    assertNotNull(nu)
    assertTrue { nu.roleGroups.isEmpty() }
  }

  fun getRole() =
    Role().apply {
      name = "TEST_PERMISSIONS:${snowflake.next()}"
      doc = "nul"
    }

  @Test
  fun testLinkRoleToRoleGroup() {
    val r = roleService.post(getRole())
    val rg = rgService.post(getRoleGroup())
    aggregator.linkRoleToRoleGroup(r.id, rg.id)!!
    val srg = argRepo.findByIdOrNull(rg.id)!!
    assertContains(srg.roles.map { it.id }, r.id)
  }

  fun getRoles() =
    List(10) {
      Role().apply {
        name = "NONE_PERMISSIONS:${snowflake.next()}"
        doc = "md = ${snowflake.next()}"
      }
    }

  @Test
  fun testLinkAllRoleToRoleGroup() {
    roleService.postAll(getRoles()).let { rs ->
      rgService.post(getRoleGroup()).let { rg ->
        aggregator.linkAllRoleToRoleGroup(rs.map { it.id }, rg.id).let {
          assertEquals(argRepo.findByIdOrNull(rg.id)!!.roles.size, rs.size)
        }
      }
    }
  }

  @Test
  fun testRevokeRoleFromRoleGroup() {
    roleService.post(getRole()).let { r ->
      rgService.post(getRoleGroup()).let { rg ->
        aggregator.linkRoleToRoleGroup(r.id, rg.id)!!.let {
          aggregator.revokeRoleFromRoleGroup(r.id, rg.id)
          val srg = argRepo.findByIdOrNull(rg.id)!!
          assertTrue { srg.roles.isEmpty() }
        }
      }
    }
  }

  @Test
  fun testRevokeAllRoleFromRoleGroup() {
    roleService.postAll(getRoles()).let { rs ->
      rgService.post(getRoleGroup()).let { rg ->
        aggregator.linkAllRoleToRoleGroup(rs.map { it.id }, rg.id).let {
          aggregator.revokeAllRoleFromRoleGroup(rs.map { it.id }, rg.id)
          argRepo.findByIdOrNull(rg.id)!!.let {
            assertTrue { it.roles.isEmpty() }
          }
        }
      }
    }
  }

  fun getPermissions() =
    Permissions().apply {
      name = "PERMISSIONS:${snowflake.next()}"
      doc = "stra ${snowflake.next()}"
    }

  @Resource lateinit var arRepo: IFullRoleRepo

  @Test
  fun testSavePermissionsToRole() {
    permissionsService.post(getPermissions()).let { p ->
      roleService.post(getRole()).let { r ->
        aggregator.savePermissionsToRole(p.id, r.id)!!
        val rl = arRepo.findByIdOrNull(r.id)!!
        assertContains(rl.permissions, p)
      }
    }
  }

  fun getAllPermissions() = List(10) { getPermissions() }

  @Test
  fun testSaveAllPermissionsToRole() {
    permissionsService.postAll(getAllPermissions()).let { ps ->
      roleService.post(getRole()).let { r ->
        aggregator.saveAllPermissionsToRole(ps.map { it.id }, r.id).let { all ->
          assertTrue("all$all") { all.isNotEmpty() }
          arRepo.findByIdOrNull(r.id)!!.let { sr ->
            ps.forEach { assertContains(sr.permissions, it) }
          }
        }
      }
    }
  }

  @Test
  fun testRevokePermissionsFromRole() {
    permissionsService.post(getPermissions()).let { p ->
      roleService.post(getRole()).let { r ->
        aggregator.savePermissionsToRole(p.id, r.id).let {
          aggregator.revokePermissionsFromRole(p.id, r.id)
          arRepo.findByIdOrNull(r.id)!!.let { sr ->
            repeat(sr.permissions.size) {
              assertFalse { sr.permissions.contains(p) }
            }
          }
        }
      }
    }
  }

  @Test
  fun testRevokeAllPermissionsFromRole() {
    roleService.post(getRole()).let { r ->
      permissionsService.postAll(getAllPermissions()).let { ps ->
        aggregator.saveAllPermissionsToRole(ps.map { it.id }, r.id).let {
          aggregator.revokeAllPermissionsFromRole(ps.map { it.id }, r.id)
          arRepo.findByIdOrNull(r.id)!!.let {
            assertTrue { it.permissions.isEmpty() }
          }
        }
      }
    }
  }
}
