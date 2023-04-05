package com.truenine.component.rds.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.rds.RdsEntrance
import jakarta.annotation.Resource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests
import org.testng.annotations.Test

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Rollback
@SpringBootTest(classes = [RdsEntrance::class])
class TableRowDeleteRecordServiceImplTest :
  AbstractTransactionalTestNGSpringContextTests() {

  @Resource
  lateinit var delService: TableRowDeleteRecordServiceImpl

  @Resource
  lateinit var mapper: ObjectMapper

  @Test
  fun testSave() {
    TODO("重新编写测试用例")
  }
}
