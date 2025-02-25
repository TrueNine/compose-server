package net.yan100.compose.rds.crud.repositories.jpa

import jakarta.annotation.Resource
import net.yan100.compose.core.slf4j
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback

@Rollback
@SpringBootTest
class IRoleGroupRepoTest {
  lateinit var repo: IRoleGroupRepo
    @Resource set

  val log = slf4j(this::class)

  @Test
  @Rollback
  fun testFindAll() {
    val all = repo.findAll()
    log.info("roleGroup = {}", all)
  }
}
