package com.truenine.component.rds.converters

import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.entity.DbTestPeriodConverterEntity
import com.truenine.component.rds.entity.UserInfoEntity
import com.truenine.component.rds.repository.DbTestPeriodConverterRepository
import com.truenine.component.rds.repository.UserInfoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test


@SpringBootTest(classes = [RdsEntrance::class])
class AesEncryptConverterTest : AbstractTestNGSpringContextTests() {

  @Autowired
  private lateinit var repo: DbTestPeriodConverterRepository
  private val log = LogKt.getLog(this::class)

  @Test
  fun bootConverter() {
    repo.save(
      DbTestPeriodConverterEntity()
    )
  }
}
