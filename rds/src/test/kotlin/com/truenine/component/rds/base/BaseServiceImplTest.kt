package com.truenine.component.rds.base

import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.entity.DbTestBaseServiceEntity
import com.truenine.component.rds.service.BaseServiceTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import java.math.BigDecimal

@SpringBootTest(classes = [RdsEntrance::class])
@Rollback
class BaseServiceImplTest : AbstractTestNGSpringContextTests() {

  private val log = LogKt.getLog(this::class)

  @Autowired
  private lateinit var service: BaseServiceTester

  @Test
  @Rollback
  fun testFindAll() {
    service.save(DbTestBaseServiceEntity().apply {
      title = "wad"
      center = PointModel(BigDecimal("1.3"), BigDecimal("2.44"))
    })
    service.findAll()
  }
}
