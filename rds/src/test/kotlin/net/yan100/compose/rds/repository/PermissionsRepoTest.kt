package net.yan100.compose.rds.repository

import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.rds.RdsEntrance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

@Rollback
@SpringBootTest(classes = [RdsEntrance::class])
class PermissionsRepoTest : AbstractTestNGSpringContextTests() {
  @Autowired
  private lateinit var repo: PermissionsRepo
  private val log = slf4j(this::class)

  @Test
  @Rollback
  fun testFindAll() {
    val all = repo.findAll()
    log.info("permissions = {}", all)
  }
}
