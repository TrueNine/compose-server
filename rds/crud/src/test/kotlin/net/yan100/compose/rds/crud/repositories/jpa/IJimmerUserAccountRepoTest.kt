package net.yan100.compose.rds.crud.repositories.jpa

import jakarta.annotation.Resource
import net.yan100.compose.testtookit.log
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback

@Rollback
@SpringBootTest
class IJimmerUserAccountRepoTest {
  lateinit var repo: IUserAccountRepo
    @Resource set

  @Test
  @Rollback
  fun testFindAll() {
    val users = repo.findAll()
    log.info("users = {}", users)
  }
}
