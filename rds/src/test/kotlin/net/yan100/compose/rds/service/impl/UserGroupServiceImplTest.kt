package net.yan100.compose.rds.service.impl


import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.entity.UserGroup
import net.yan100.compose.rds.repository.UserGroupRepo
import net.yan100.compose.rds.repository.UserRepo
import net.yan100.compose.rds.service.UserService
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
  lateinit var ur: UserRepo

  @Autowired
  lateinit var ugr: UserGroupRepo


  @Test
  fun testSaveUserToUserGroup() {
    val u = ur.save(net.yan100.compose.rds.entity.User().apply {
      this.account = "qwerq"
      this.pwdEnc = "qwbad"
      this.nickName = "abcd"
    })
    val ug = ugr.save(UserGroup().apply {
      this.userId = 13123124.toString()
      this.name = "readMe"
    })
    service.saveUserToUserGroup(u.id!!, ug.id!!)
    val ugs = service.findById(ug.id!!)
    assertNotNull(ugs)
    assertContains(ugs.users, u)
  }

  @Test
  fun testFindAllByLeaderUserId() {
    val saved = service.save(UserGroup().apply {
      userId = 133.toString()
      id = 1231241.toString()
      name = "我的"
    })
    val b = service.findAllByLeaderUserId(133.toString())
    assertNotNull(b)
    assertContains(b, saved)
  }

  @Autowired
  lateinit var userService: UserService

  @Test
  fun testFindAllByUserAccount() {
    val u = userService.save(net.yan100.compose.rds.entity.User().apply {
      account = "abcd"
      pwdEnc = "abcd1234"
    })
    val ug = service.save(UserGroup().apply {
      userId = 123124125.toString()
      name = "我的大海"
    })
    service.saveUserToUserGroup(u.id!!, ug.id!!)
    val foundUg = service.findAllByUserAccount(u.account!!)
    assertTrue { foundUg.isNotEmpty() }
    assertContains(foundUg, ug)
  }
}
