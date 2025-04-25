package net.yan100.compose.rds.crud.repositories.jpa

import jakarta.annotation.Resource
import net.yan100.compose.testtoolkit.log
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback

@Rollback
@SpringBootTest
class IRoleRepoTest {
  lateinit var repo: IRoleRepo
    @Resource set

  @Test
  @Rollback
  fun testFindAll() {
    val all = repo.findAll()
    log.info("role = {}", all)
  }
}
