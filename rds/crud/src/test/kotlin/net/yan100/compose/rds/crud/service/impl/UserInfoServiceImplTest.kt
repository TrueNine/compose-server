package net.yan100.compose.rds.crud.service.impl

import jakarta.annotation.Resource
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.yan100.compose.rds.crud.entities.jpa.UserInfo
import net.yan100.compose.testtookit.RDBRollback
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest

@RDBRollback
@SpringBootTest
class UserInfoServiceImplTest {
  lateinit var userInfoService: UserInfoServiceImpl
    @Resource set

  @RDBRollback
  @BeforeTest
  fun setup() {
    userInfoService.postFound(
      UserInfo().apply {
        firstName = "R"
        lastName = "OOT"
      }
    )
  }

  @Test
  fun `test findIsRealPeopleById`() {
    runBlocking {
      launch {
        val r = userInfoService.findIsRealPeopleByUserId(0)
        log.info("r: {}", r)
      }
    }
  }
}
