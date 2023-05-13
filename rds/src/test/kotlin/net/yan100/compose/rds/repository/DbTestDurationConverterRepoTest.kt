package net.yan100.compose.rds.repository

import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.rds.entity.DbTestDurationConverterEntity
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
  private val log = slf4j(this::class)

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
