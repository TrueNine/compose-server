package com.truenine.component.rds.service.aggregator

import com.truenine.component.core.id.Snowflake
import com.truenine.component.rds.entity.RoleGroupEntity
import com.truenine.component.rds.entity.UserEntity
import com.truenine.component.rds.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
class RbacAggregatorImplTest : AbstractTestNGSpringContextTests() {

  @Autowired
  lateinit var aggregator: RbacAggregator

  @Autowired
  lateinit var userService: UserService

  @Autowired
  lateinit var userGroupService: UserGroupService

  @Autowired
  lateinit var roleService: RoleService

  @Autowired
  lateinit var roleGroupService: RoleGroupService

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
    val rg = roleGroupService.save(getRoleGroup())

    assertNotNull(user)
    assertNotNull(rg)

    val link = aggregator.saveRoleGroupToUser(rg.id, user.id)
    assertNotNull(link)

    assertEquals(link.roleGroupId, rg.id)
    assertEquals(link.userId, user.id)
  }

  @Test
  fun testSaveAllRoleGroupToUser() {
  }

  @Test
  fun testRevokeRoleGroupFromUser() {
  }

  @Test
  fun testRevokeAllRoleGroupFromUser() {
  }

  @Test
  fun testSaveRoleGroupToUserGroup() {
  }

  @Test
  fun testSaveAllRoleGroupToUserGroup() {
  }

  @Test
  fun testRevokeRoleGroupFromUserGroup() {
  }

  @Test
  fun testRevokeAllRoleGroupFromUserGroup() {
  }

  @Test
  fun testSaveRoleToRoleGroup() {
  }

  @Test
  fun testSaveAllRoleToRoleGroup() {
  }

  @Test
  fun testRevokeRoleFromRoleGroup() {
  }

  @Test
  fun testRevokeAllRoleFromRoleGroup() {
  }

  @Test
  fun testSavePermissionsToRole() {
  }

  @Test
  fun testSaveAllPermissionsToRole() {
  }

  @Test
  fun testRevokePermissionsFromRole() {
  }

  @Test
  fun testRevokeAllPermissionsFromRole() {
  }
}
