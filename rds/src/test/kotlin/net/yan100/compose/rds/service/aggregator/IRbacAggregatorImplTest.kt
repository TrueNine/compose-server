/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.rds.service.aggregator

import kotlin.test.*
import net.yan100.compose.core.ISnowflakeGenerator
import net.yan100.compose.rds.entities.Permissions
import net.yan100.compose.rds.entities.Role
import net.yan100.compose.rds.entities.RoleGroup
import net.yan100.compose.rds.entities.Usr
import net.yan100.compose.rds.repositories.AllRoleEntityRepository
import net.yan100.compose.rds.repositories.FullRoleGroupEntityRepo
import net.yan100.compose.rds.service.IPermissionsService
import net.yan100.compose.rds.service.IRoleGroupService
import net.yan100.compose.rds.service.IRoleService
import net.yan100.compose.rds.service.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class IRbacAggregatorImplTest {
  @Autowired lateinit var argRepo: FullRoleGroupEntityRepo

  @Autowired lateinit var aggregator: IRbacAggregator

  @Autowired lateinit var userService: IUserService

  @Autowired lateinit var roleService: IRoleService

  @Autowired lateinit var rgService: IRoleGroupService

  @Autowired lateinit var permissionsService: IPermissionsService

  @Autowired lateinit var snowflake: ISnowflakeGenerator

  private fun getUser() =
    Usr().apply {
      account = "name${snowflake.next()}"
      nickName = "abcd"
      pwdEnc = "aa${snowflake.next()}"
    }

  private fun getRoleGroup() = RoleGroup().apply { name = "ab${snowflake.next()}" }

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

  fun getAllRoleGroup() = List(20) { getRoleGroup() }

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
    val nu = userService.findFullUserByAccount(u.account)
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
      name = "测试权限${snowflake.next()}"
      doc = "nul"
    }

  @Test
  fun testSaveRoleToRoleGroup() {
    val r = roleService.save(getRole())
    val rg = rgService.save(getRoleGroup())
    aggregator.saveRoleToRoleGroup(r.id, rg.id)!!
    val srg = argRepo.findByIdOrNull(rg.id)!!
    assertContains(srg.roles.map { it.id }, r.id)
  }

  fun getRoles() =
    List(10) {
      Role().apply {
        name = "没有${snowflake.next()}"
        doc = "md = ${snowflake.next()}"
      }
    }

  @Test
  fun testSaveAllRoleToRoleGroup() {
    roleService.saveAll(getRoles()).let { rs ->
      rgService.save(getRoleGroup()).let { rg ->
        aggregator.saveAllRoleToRoleGroup(rs.map { it.id }, rg.id).let { sru ->
          argRepo.findByIdOrNull(rg.id)!!.let { nr -> assertEquals(nr.roles.size, rs.size) }
        }
      }
    }
  }

  @Test
  fun testRevokeRoleFromRoleGroup() {
    roleService.save(getRole()).let { r ->
      rgService.save(getRoleGroup()).let { rg ->
        aggregator.saveRoleToRoleGroup(r.id, rg.id)!!.let { rgr ->
          aggregator.revokeRoleFromRoleGroup(r.id, rg.id)
          val srg = argRepo.findByIdOrNull(rg.id)!!
          assertTrue { srg.roles.isEmpty() }
        }
      }
    }
  }

  @Test
  fun testRevokeAllRoleFromRoleGroup() {
    roleService.saveAll(getRoles()).let { rs ->
      rgService.save(getRoleGroup()).let { rg ->
        aggregator.saveAllRoleToRoleGroup(rs.map { it.id }, rg.id).let { rgr ->
          aggregator.revokeAllRoleFromRoleGroup(rs.map { it.id }, rg.id)
          argRepo.findByIdOrNull(rg.id)!!.let { assertTrue { it.roles.isEmpty() } }
        }
      }
    }
  }

  fun getPermissions() =
    Permissions().apply {
      name = "权限 ${snowflake.next()}"
      doc = "stra ${snowflake.next()}"
    }

  @Autowired lateinit var arRepo: AllRoleEntityRepository

  @Test
  fun testSavePermissionsToRole() {
    permissionsService.save(getPermissions()).let { p ->
      roleService.save(getRole()).let { r ->
        aggregator.savePermissionsToRole(p.id, r.id)!!
        val rl = arRepo.findByIdOrNull(r.id)!!
        assertContains(rl.permissions, p)
      }
    }
  }

  fun getAllPermissions() = List(10) { getPermissions() }

  @Test
  fun testSaveAllPermissionsToRole() {
    permissionsService.saveAll(getAllPermissions()).let { ps ->
      roleService.save(getRole()).let { r ->
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
    permissionsService.save(getPermissions()).let { p ->
      roleService.save(getRole()).let { r ->
        aggregator.savePermissionsToRole(p.id, r.id).let {
          aggregator.revokePermissionsFromRole(p.id, r.id)
          arRepo.findByIdOrNull(r.id)!!.let { sr ->
            repeat(sr.permissions.size) { assertFalse { sr.permissions.contains(p) } }
          }
        }
      }
    }
  }

  @Test
  fun testRevokeAllPermissionsFromRole() {
    roleService.save(getRole()).let { r ->
      permissionsService.saveAll(getAllPermissions()).let { ps ->
        aggregator.saveAllPermissionsToRole(ps.map { it.id }, r.id).let { srp ->
          aggregator.revokeAllPermissionsFromRole(ps.map { it.id }, r.id)
          arRepo.findByIdOrNull(r.id)!!.let { assertTrue { it.permissions.isEmpty() } }
        }
      }
    }
  }
}
