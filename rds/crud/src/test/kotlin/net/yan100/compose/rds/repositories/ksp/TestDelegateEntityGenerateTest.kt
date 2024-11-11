package net.yan100.compose.rds.repositories.ksp

import jakarta.annotation.Resource
import net.yan100.compose.rds.repositories.IDbTestGetterAndSetterEntityRepo
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@Rollback
@SpringBootTest
class TestDelegateEntityGenerateTest {
  lateinit var repo: IDbTestGetterAndSetterEntityRepo @Resource set

  @BeforeTest
  fun setup() {
    assertNotNull(repo)
  }

  @Test
  fun `launch test`() {
    repo.existsByName("")
    repo.existsByAged(1)
  }
}
