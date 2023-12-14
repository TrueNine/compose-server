package net.yan100.compose.rds.repositories

import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.rds.RdsEntrance
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback

@Rollback
@SpringBootTest(classes = [RdsEntrance::class])
class RoleGroupRepoTest {
  @Autowired
  private lateinit var repo: RoleGroupRepo
  val log = slf4j(this::class)

  @Test
  @Rollback
  fun testFindAll() {
    val all = repo.findAll()
    log.info("roleGroup = {}", all)
  }
}
