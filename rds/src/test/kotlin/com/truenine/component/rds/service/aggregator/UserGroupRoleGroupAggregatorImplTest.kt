package com.truenine.component.rds.service.aggregator

import com.truenine.component.core.id.Snowflake
import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.entity.RoleGroupEntity
import com.truenine.component.rds.entity.UserGroupEntity
import com.truenine.component.rds.service.RoleGroupService
import com.truenine.component.rds.service.UserGroupService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

@SpringBootTest(classes = [RdsEntrance::class])
class UserGroupRoleGroupAggregatorImplTest : AbstractTestNGSpringContextTests() {

  @Autowired
  lateinit var ura: UserGroupRoleGroupAggregatorImpl

  @Autowired
  lateinit var snowflake: Snowflake

  fun getRoleGroup() = RoleGroupEntity().apply {
    name = "ab:${snowflake.nextId()}"
    doc = "123124"
  }

  @Autowired
  lateinit var roleGroupService: RoleGroupService

  @Autowired
  lateinit var ug: UserGroupService

  @Test
  fun testSaveRoleGroupToUserGroup() {
    roleGroupService.save(getRoleGroup())!!.let { rg ->
      ug.save(UserGroupEntity().apply {
        name = "我的${snowflake.nextId()}"
        doc = "bbc"
      })!!.let { sug ->
        ura.saveRoleGroupToUserGroup(rg.id, sug.id)
        ug.findById(sug.id)!!.let {
          assertContains(it.roleGroups, rg)
        }
      }
    }
  }

  @Test
  fun testRevokeRoleGroupFromUserGroup() {
    ug.save(UserGroupEntity().apply {
      name = "abc"
      doc = "dwad"
    })!!.let { sug ->
      roleGroupService.save(getRoleGroup())!!.let { rg ->
        ura.saveRoleGroupToUserGroup(rg.id, sug.id).let {
          ura.revokeRoleGroupFromUserGroup(rg.id, sug.id)
          ug.findById(sug.id)!!.let { dels ->
            assertTrue {
              dels.roleGroups == null || dels.roleGroups.isEmpty()
            }
          }
        }
      }
    }
  }
}
