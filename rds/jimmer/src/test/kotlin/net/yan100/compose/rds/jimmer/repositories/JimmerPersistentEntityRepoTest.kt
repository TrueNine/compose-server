package net.yan100.compose.rds.jimmer.repositories

import jakarta.annotation.Resource
import net.yan100.compose.rds.jimmer.entities.UserAccount
import net.yan100.compose.rds.jimmer.generators.JimmerSnowflakeStringIdGenerator
import net.yan100.compose.testtookit.RDBRollback
import net.yan100.compose.testtookit.assertNotEmpty
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest
class JimmerPersistentEntityRepoTest {
  lateinit var repo: IUserAccountRepo @Resource set
  lateinit var snowflakeGenerator: JimmerSnowflakeStringIdGenerator @Resource(name = JimmerSnowflakeStringIdGenerator.JIMMER_SNOWFLAKE_STRING_ID_GENERATOR_NAME) set

  @BeforeTest
  fun setup() {
    assertNotNull(repo)
    assertNotNull(snowflakeGenerator)
  }


  @Test
  @RDBRollback
  fun `test save`() {
    val saved = repo.insert(UserAccount {
      account = "gust"
      nickName = "gust"
      pwdEnc = "123456"
    })
    assertNotNull(saved)
    val founded = repo.findById(saved.id)
    assertNotNull(founded)
  }

  @Test
  fun `test find all`() {
    val all = repo.findAll()
    assertNotEmpty { all }
  }
}
