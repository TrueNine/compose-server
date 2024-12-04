package net.yan100.compose.rds.jimmer.repositories

import jakarta.annotation.Resource
import net.yan100.compose.rds.jimmer.dto.address.CodeAndNameAddressView
import net.yan100.compose.testtookit.RDBRollback
import net.yan100.compose.testtookit.assertNotEmpty
import net.yan100.compose.testtookit.log
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@RDBRollback
@SpringBootTest
class JimmerDtoViewQueryTest {
  lateinit var repo: IAddressRepo @Resource set

  @Test
  fun `test find all`() {
    val all = repo.viewer(CodeAndNameAddressView::class).findAll()
    assertNotEmpty { all }
    all.forEach {
      log.info("it: {}", it)
    }
  }
}
