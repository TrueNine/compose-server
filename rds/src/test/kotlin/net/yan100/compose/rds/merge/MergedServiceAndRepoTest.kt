package net.yan100.compose.rds.merge

import jakarta.annotation.Resource
import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.repositories.IDbTestMergeTableRepo
import net.yan100.compose.rds.service.IDbTestMergeTableService
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


@Rollback
@SpringBootTest(classes = [RdsEntrance::class])
class MergedServiceAndRepoTest {
  lateinit var repo: IDbTestMergeTableRepo @Resource set
  lateinit var service: IDbTestMergeTableService @Resource set

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
