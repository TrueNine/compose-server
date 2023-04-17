package com.truenine.component.rds.repository

import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.entity.DbTestDurationConverterEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import java.time.Duration
import kotlin.test.assertNotNull

@Rollback
@SpringBootTest
class DbTestDurationConverterRepoTest : AbstractTestNGSpringContextTests() {
  private val log = LogKt.getLog(this::class)

  @Autowired
  private lateinit var repo: DbTestDurationConverterRepository

  @Test
  @Rollback
  fun testSaveAndFind() {
    val entity = DbTestDurationConverterEntity().apply {
      durations = Duration.parse("PT24H")
    }
    val saved = repo.save(entity)
    assertNotNull(saved)
    log.info("saved = {}", saved)
  }
}
