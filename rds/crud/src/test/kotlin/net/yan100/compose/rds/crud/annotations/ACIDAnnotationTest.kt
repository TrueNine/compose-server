package net.yan100.compose.rds.crud.annotations

import jakarta.annotation.Resource
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import net.yan100.compose.rds.crud.repositories.jpa.IUserAccountRepo
import net.yan100.compose.testtookit.RDBRollback
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest

@RDBRollback
@SpringBootTest
class ACIDAnnotationTest {
  lateinit var usrRepo: IUserAccountRepo
    @Resource set

  lateinit var launchBean: LaunchBean
    @Resource set

  @BeforeTest
  fun setup() {
    assertNotNull(launchBean, "确保测试类能够被注入")
    assertNotNull(usrRepo)
  }

  @Test
  @RDBRollback
  fun `ensure transactional call after rollback`() {
    val all = usrRepo.findAll()
    val allSize = all.size
    assertThrows<IllegalStateException> { launchBean.throwTransactionalSave() }
    val afterAllData = usrRepo.findAll()
    assertEquals(allSize, afterAllData.size, "确保数据被回滚")
  }

  @Test
  fun `launch transactional`() {
    val all = usrRepo.findAll()
    assertThrows<IllegalStateException> { launchBean.throwSaveAcid() }
    val after = usrRepo.findAll()
    assertEquals(all.size, after.size, "确保数据没有被回滚")
  }
}
