package com.truenine.component.rds.converters

import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.entity.UserInfoEntity
import com.truenine.component.rds.repo.UserInfoRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.transaction.annotation.Transactional
import org.testng.annotations.Test

@Rollback
@SpringBootTest
open class AesEncryptConverterTest : AbstractTestNGSpringContextTests() {

  @Autowired
  private lateinit var userInfoRepo: UserInfoRepo
  private val log = LogKt.getLog(this::class)

  @Test
  @Transactional
  open fun bootConverter() {
    log.warn("从方法没有实现")
    val phone = "000101199312123349"
    userInfoRepo.deleteByPhone(phone)

    userInfoRepo.saveAndFlush(
      UserInfoEntity().apply {
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
