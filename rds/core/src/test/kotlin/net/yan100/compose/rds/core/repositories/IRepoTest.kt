package net.yan100.compose.rds.core.repositories

import jakarta.annotation.Resource
import net.yan100.compose.rds.core.RdsCoreEntrance
import net.yan100.compose.rds.core.entities.TestIEntity
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

@SpringBootTest(classes = [RdsCoreEntrance::class])
class IRepoTest {

  lateinit var repo: TestIEntityRepo @Resource set

  @Test
  fun `query persistence audit data`() {
    val saved = repo.save(TestIEntity())
    log.info("saved entity: {}", saved)
  }
}
