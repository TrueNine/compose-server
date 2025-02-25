package net.yan100.compose.rds.crud.merge

import jakarta.annotation.Resource
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import net.yan100.compose.rds.crud.repositories.jpa.IDbTestMergeTableRepo
import net.yan100.compose.rds.crud.service.IDbTestMergeTableService
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback

@Rollback
@SpringBootTest
class MergedServiceAndRepoTest {
  lateinit var repo: IDbTestMergeTableRepo
    @Resource set

  lateinit var service: IDbTestMergeTableService
    @Resource set

  @BeforeTest
  fun setup() {
    assertNotNull(repo)
    assertNotNull(service)
  }

  @Test
  fun `crud operation`() {
    val l1 = service.findAllByName("")
    assertTrue { l1.isEmpty() }
  }
}
