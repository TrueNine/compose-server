package com.truenine.component.rds.service.impl

import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.entity.UserGroupEntity
import jakarta.annotation.Resource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Rollback
@SpringBootTest(classes = [RdsEntrance::class])
class UserGroupServiceImplTest :
  AbstractTransactionalTestNGSpringContextTests() {

  @Resource
  lateinit var userGroupService: UserGroupServiceImpl

  private lateinit var testUserGroup: UserGroupEntity

  @BeforeMethod
  fun init() {
    val u =
      UserGroupEntity()
    u.name = "来宾"
    u.doc = "略"
    testUserGroup = userGroupService.saveUserGroup(u)!!
  }

  @Test
  fun testSaveUserGroup() {
    UserGroupEntity().apply {
      this.userId = "0"
      this.name = "二狗子"
      this.doc = "我日你娘"
      val s = userGroupService.saveUserGroup(this)
      assertEquals(s, this, "未保存成功")
    }
  }

  @Test
  fun testFindAllUserGroupByUserId() {
    userGroupService.findAllUserGroupByUserId("0").apply {
      assertTrue {
        this.isNotEmpty()
      }
    }
    val u = userGroupService.saveUserGroup(testUserGroup.apply {
      userId = "0"
      name = "二狗子组"
      doc = "略"
    })
    assertNotNull(u)
    userGroupService.findAllUserGroupByUserId("0").apply {
      assertTrue {
        this.isNotEmpty()
      }
    }
    userGroupService.assignUserToUserGroup("1", u.id)
    userGroupService.findAllUserGroupByUserId("1").apply {
      assertTrue { this.isNotEmpty() }
    }
  }

  @Test
  fun testAssignUserToUserGroup() {
    userGroupService.assignUserToUserGroup("1", testUserGroup.id)
    userGroupService.findAllUserGroupByUserId("1").apply {
      assertTrue { this.isNotEmpty() }
    }
  }

  @Test
  fun testDeleteUserGroupById() {
    userGroupService.deleteUserGroupById(testUserGroup.id)
    val f = userGroupService.findUserGroupById(testUserGroup.id)
    assertNull(f, "未删除")
  }

  @Test
  fun testFindUserGroupById() {
    userGroupService.findUserGroupById(testUserGroup.id).apply {
      assertNotNull(this)
    }
  }
}
