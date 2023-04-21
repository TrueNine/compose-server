package com.truenine.component.rds.service.impl

import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.entity.UserEntity
import com.truenine.component.rds.entity.UserGroupEntity
import com.truenine.component.rds.repository.UserGroupRepository
import com.truenine.component.rds.repository.UserRepository
import com.truenine.component.rds.service.UserService
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import kotlin.test.assertContains
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


@SpringBootTest(classes = [RdsEntrance::class])
class UserGroupServiceImplTest : AbstractTestNGSpringContextTests() {

  @Autowired
  lateinit var service: UserGroupServiceImpl

  @Autowired
  lateinit var ur: UserRepository

  @Autowired
  lateinit var ugr: UserGroupRepository

  @Autowired
  lateinit var em: EntityManager

  @Test
  fun testSaveUserToUserGroup() {
    val u = ur.save(UserEntity().apply {
      this.account = "qwerq"
      this.pwdEnc = "qwbad"
      this.nickName = "abcd"
    })
    val ug = ugr.save(UserGroupEntity().apply {
      this.userId = 13123124
      this.name = "readMe"
    })
    service.saveUserToUserGroup(u.id, ug.id)
    val ugs = service.findById(ug.id)
    assertNotNull(ugs)
    assertContains(ugs.users, u)
  }

  @Test
  fun testFindAllByLeaderUserId() {
    val saved = service.save(UserGroupEntity().apply {
      userId = 133
      id = 1231241
      name = "我的"
    })
    val b = service.findAllByLeaderUserId(133)
    assertNotNull(b)
    assertContains(b, saved)
  }

  @Autowired
  lateinit var userService: UserService

  @Test
  fun testFindAllByUserAccount() {
    val u = userService.save(UserEntity().apply {
      account = "abcd"
      pwdEnc = "abcd1234"
    })!!
    val ug = service.save(UserGroupEntity().apply {
      userId = 123124125
      name = "我的大海"
    })!!
    service.saveUserToUserGroup(u.id, ug.id)
    val foundUg = service.findAllByUserAccount(u.account)
    assertTrue { foundUg.isNotEmpty() }
    assertContains(foundUg, ug)
  }
}
