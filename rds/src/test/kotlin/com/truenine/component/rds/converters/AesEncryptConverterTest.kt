package com.truenine.component.rds.converters

import com.truenine.component.core.lang.slf4j
import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.entity.DbTestPeriodConverterEntity
import com.truenine.component.rds.repository.DbTestPeriodConverterRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test


@SpringBootTest(classes = [RdsEntrance::class])
class AesEncryptConverterTest : AbstractTestNGSpringContextTests() {

  @Autowired
  private lateinit var repo: DbTestPeriodConverterRepository
  private val log = slf4j(this::class)

  @Test
  fun bootConverter() {
    repo.save(
      DbTestPeriodConverterEntity()
    )
  }
}
