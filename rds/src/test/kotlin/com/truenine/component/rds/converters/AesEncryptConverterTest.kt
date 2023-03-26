package com.truenine.component.rds.converters

import com.truenine.component.rds.dao.UserInfoDao
import com.truenine.component.rds.repo.UserInfoRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

@Rollback
@SpringBootTest
class AesEncryptConverterTest : AbstractTestNGSpringContextTests() {
  @Autowired
  private lateinit var userInfoRepo: UserInfoRepo

  @Test
  fun bootConverter() {
    val phone = "000101199312123349"
    userInfoRepo.deleteByPhone(phone)

    userInfoRepo.saveAndFlush(
      UserInfoDao().apply {
        this.email = "truenine@qq.com"
        this.firstName = "赵"
        this.lastName = "日天"
        this.gender = 1
        this.idCard = "000101199312123349"
        this.phone = phone
        this.userId = "0"
      }
    )
    println(userInfoRepo.findAll())
    userInfoRepo.deleteByPhone(phone)
  }
}
