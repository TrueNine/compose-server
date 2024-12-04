package net.yan100.compose.rds.crud.annotations


import jakarta.annotation.Resource
import net.yan100.compose.rds.crud.repositories.jpa.IUserAccountRepo
import net.yan100.compose.testtookit.RDBRollback
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RDBRollback
@SpringBootTest
class ACIDAnnotationTest {
  lateinit var usrRepo: IUserAccountRepo @Resource set
  lateinit var launchBean: LaunchBean @Resource set

  @BeforeTest
  fun setup() {
    assertNotNull(launchBean, "确保测试类能够被注入")
    assertNotNull(usrRepo)
  }

  @Test
  fun `ensure transactional call after rollback`() {
    val all = usrRepo.findAll()
    assertEquals(2, all.size, "确保无任何其他数据")
    assertThrows<IllegalStateException> { launchBean.throwTransactionalSave() }
    val afterAllData = usrRepo.findAll()
    assertEquals(2, afterAllData.size, "确保数据被回滚")
  }

  @Test
  fun `launch transactional`() {
    val all = usrRepo.findAll()
    assertThrows<IllegalStateException> { launchBean.throwSaveAcid() }
    val after = usrRepo.findAll()
    assertEquals(all.size, after.size, "确保数据没有被回滚")
  }
}
