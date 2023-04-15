package com.truenine.component.rds.repository

import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.RdsEntrance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

@Rollback
@SpringBootTest(classes = [RdsEntrance::class])
class RoleRepositoryTest : AbstractTestNGSpringContextTests() {

  @Autowired
  private lateinit var repo: RoleRepository
  val log = LogKt.getLog(this::class)

  @Test
  @Rollback
  fun testFindAll() {
    val all = repo.findAll()
    log.info("role = {}", all)
  }
}
