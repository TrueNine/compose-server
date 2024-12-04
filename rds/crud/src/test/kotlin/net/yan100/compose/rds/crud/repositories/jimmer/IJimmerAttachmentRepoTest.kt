package net.yan100.compose.rds.crud.repositories.jimmer

import jakarta.annotation.Resource
import net.yan100.compose.testtookit.RDBRollback
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@RDBRollback
@SpringBootTest
class IJimmerAttachmentRepoTest {
  lateinit var attRepo: IJimmerAttachmentRepo @Resource set

  @BeforeTest
  fun setup() {
    /*attRepo.insertIfAbsent(
      Attachment {
        attType = AttachmentTyping.BASE_URL
      }
    )*/
  }

  @Test
  @RDBRollback
  fun `ensure linked url`() {
    val atts = attRepo.findFilesBy()
    log.info("selected att size: {}", atts.size)
    assertNotNull(atts)
  }
}
