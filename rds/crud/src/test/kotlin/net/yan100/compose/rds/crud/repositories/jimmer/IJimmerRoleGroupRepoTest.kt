package net.yan100.compose.rds.crud.repositories.jimmer

import jakarta.annotation.Resource
import net.yan100.compose.rds.crud.entities.jimmer.RoleGroup
import net.yan100.compose.rds.crud.entities.jimmer.by
import net.yan100.compose.testtookit.assertNotEmpty
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest
class IJimmerRoleGroupRepoTest {
  lateinit var repo: IJimmerRoleGroupRepo @Resource set

  @Test
  fun `test find all`() {
    val all = repo.findAll(
      newFetcher(RoleGroup::class).by {
        allScalarFields()
        roles {
          allScalarFields()
          permissions {
            allScalarFields()
          }
        }
      }
    )
    assertNotNull(all)
    assertNotEmpty { all }
    all.forEach {
      assertNotEmpty { it.roles }
    }
  }
}
