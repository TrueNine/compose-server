package net.yan100.compose.rds.service.impl

import io.mockk.every
import io.mockk.mockk
import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.entity.User
import net.yan100.compose.rds.entity.UserInfo
import net.yan100.compose.rds.repository.UserInfoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testng.Assert.assertEquals
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@SpringBootTest(classes = [RdsEntrance::class])
class UserInfoServiceImplTest {
  @Autowired
  private lateinit var userInfoService: UserInfoServiceImpl

  @Autowired
  private lateinit var infoRepo: UserInfoRepository

  @BeforeMethod
  fun setUp() {
    infoRepo = mockk()
    userInfoService = UserInfoServiceImpl(infoRepo)
  }

  @Test
  fun testFindUserByWechatOpenId() {
    val openId = "123456"
    val user = User()
    every { infoRepo.findUserByWechatOpenId(openId) } returns user
    val result = userInfoService.findUserByWechatOpenId(openId)
    assertEquals(result, user)
  }

  @Test
  fun testFindUserByPhone() {
    val phone = "123456789"
    val user = User()
    every { infoRepo.findUserByPhone(phone) } returns user
    val result = userInfoService.findUserByPhone(phone)
    assertEquals(result, user)
  }

  @Test
  fun testFindByUserId() {
    val userId = "123456"
    val userInfoEntity = UserInfo()
    every { infoRepo.findByUserId(userId) } returns userInfoEntity
    val result = userInfoService.findByUserId(userId)
    assertEquals(result, userInfoEntity)
  }
}
