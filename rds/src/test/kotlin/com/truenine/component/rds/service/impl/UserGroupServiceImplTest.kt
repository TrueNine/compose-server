package com.truenine.component.rds.service.impl

import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.entity.UserGroupEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Rollback
@SpringBootTest(classes = [RdsEntrance::class])
class UserGroupServiceImplTest :
  AbstractTransactionalTestNGSpringContextTests() {

  @Autowired
  lateinit var userGroupService: UserGroupServiceImpl

  private lateinit var testUserGroup: UserGroupEntity

  @BeforeMethod
  @Rollback
  fun init() {
    val u = UserGroupEntity().apply {
      name = "来宾"
      doc = "略"
    }
    testUserGroup = userGroupService.save(u)!!
  }


  @Test
  @Rollback
  fun testFindAllUserGroupByUserId() {
    userGroupService.findAllUserGroupByUserId(0L).apply {
      assertTrue {
        this.isNotEmpty()
      }
    }
    val u = userGroupService.save(testUserGroup.apply {
      userId = 0L
      name = "二狗子组"
      doc = "略"
    })
    assertNotNull(u)
    userGroupService.findAllUserGroupByUserId(0L).apply {
      assertTrue {
        this.isNotEmpty()
      }
    }
    userGroupService.assignUserToUserGroup(1L, u.id)
    userGroupService.findAllUserGroupByUserId(1L).apply {
      assertTrue { this.isNotEmpty() }
    }
  }

  @Test
  @Rollback
  fun testAssignUserToUserGroup() {
    userGroupService.assignUserToUserGroup(1L, testUserGroup.id)
    userGroupService.findAllUserGroupByUserId(1L).apply {
      assertTrue { this.isNotEmpty() }
    }
  }
}
