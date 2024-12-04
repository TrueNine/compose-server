package net.yan100.compose.rds.crud.repositories.jimmer

import jakarta.annotation.Resource
import net.yan100.compose.rds.crud.dto.jimmer.address.CodeAndNameAddressView
import net.yan100.compose.testtookit.RDBRollback
import net.yan100.compose.testtookit.log
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals


@SpringBootTest
class JimmerDtoViewQueryTest {
  lateinit var repo: IJimmerAddressRepo @Resource set

  @Test
  @RDBRollback
  fun `test find all`() {
    val all = repo.viewer(CodeAndNameAddressView::class).findAll()
    assertEquals(1, all.size)
    all.forEach {
      log.info("it: {}", it)
    }
  }
}
