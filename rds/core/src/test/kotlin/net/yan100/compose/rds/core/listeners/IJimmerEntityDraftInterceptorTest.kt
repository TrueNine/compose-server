package net.yan100.compose.rds.core.listeners

import jakarta.annotation.Resource
import net.yan100.compose.core.datetime
import net.yan100.compose.rds.core.entities.JimmerUserAccount
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.*

@SpringBootTest
class IJimmerEntityDraftInterceptorTest {
  lateinit var client: KSqlClient @Resource set

  /**
   * 确保拦截器启动并设置数据
   */
  @Test
  fun `ensure interceptor worked`() {
    val startDt = datetime.now()
    val saved = client.insert(JimmerUserAccount {
      account = "test_jimmer_account_1"
      pwdEnc = "wadawd1243124"
      nickName = "test_jimmer_nickname_1"
    }).modifiedEntity
    assertNotNull(saved)
    assertNotNull(saved.databaseMetadata)
    assertNotNull(saved.databaseMetadata?.crd)
    assertNull(saved.databaseMetadata?.mrd)
    assertNull(saved.databaseMetadata?.ldf)
    assertEquals(0, saved.databaseMetadata?.rlv)

    val endDt = datetime.now()
    assertTrue {
      startDt.isBefore(saved.databaseMetadata?.crd) &&
        (saved.databaseMetadata?.crd?.isBefore(endDt) ?: false)
    }
  }
}
