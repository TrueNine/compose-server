package net.yan100.compose.rds.repository

import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.rds.entity.DbTestDurationConverterBaseEntity
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import java.time.Duration
import kotlin.test.assertNotNull

@Rollback
@SpringBootTest
class DbTestDurationConverterRepoTest {
  private val log = slf4j(this::class)

  @Autowired
  private lateinit var repo: DbTestDurationConverterRepository

  @Test
  @Rollback
  fun testSaveAndFind() {
    val entity = DbTestDurationConverterBaseEntity().apply {
      durations = Duration.parse("PT24H")
    }
    val saved = repo.save(entity)
    assertNotNull(saved)
    log.info("saved = {}", saved)
  }
}
