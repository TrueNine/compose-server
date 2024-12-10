package net.yan100.compose.rds.jimmer.repositories

import jakarta.annotation.Resource
import net.yan100.compose.rds.jimmer.entities.Address
import net.yan100.compose.testtookit.assertNotEmpty
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import kotlin.jvm.optionals.getOrNull
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@Rollback
@SpringBootTest
class JimmerTreeEntityRepoTest {
  lateinit var repo: IAddressRepo @Resource set

  @BeforeTest
  fun setup() {
    assertNotNull(repo)
  }

  @Test
  @Rollback
  fun `test find parent`() {
    val parent = repo.insertIfAbsent(
      Address {
        parentAddr = repo.findById(0).getOrNull()
        code = "11"
        name = "北京市"
        yearVersion = "2023"
        level = 1
      }
    )
    val child = repo.insertIfAbsent(
      Address {
        parentAddr = parent
        code = "01"
        name = "市辖区"
        yearVersion = "2023"
        level = 2
      }
    )
    val all = repo.findAll()
    assertNotEmpty { all }
    log.info("all paths: {}", all)


  }

  @AfterTest
  @Rollback
  fun destroy() {
    repo.deleteAll()
    repo.insert(Address {
      id = 0
      level = 0
      name = ""
      rln = 1
      rrn = 2
      tgi = "0"
      center = null
      code = "000000000000"
    })
  }
}
