package net.yan100.compose.rds.crud.annotations

import net.yan100.compose.rds.annotations.ACID
import net.yan100.compose.rds.crud.entities.jpa.UserAccount
import net.yan100.compose.rds.crud.repositories.jpa.IUserAccountRepo
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertNotNull

@Component
class LaunchBean(private val repo: IUserAccountRepo) {

  fun saveOk() {
    val account =
      UserAccount(
        account = "abcdeferqwrqwrqwrqwreqweqw",
        pwdEnc = "abcdefghijk",
        nickName = "呢称名字",
      )
    assertNotNull(account.account)
    assertNotNull(account.pwdEnc)
    repo.save(account)
  }

  fun saveException() {
    saveOk()
    error("发出错误")
  }

  @Transactional(
    rollbackFor = [Exception::class],
    isolation = Isolation.SERIALIZABLE,
    propagation = Propagation.REQUIRES_NEW
  )
  fun throwTransactionalSave() {
    saveException()
  }

  @ACID
  fun throwSaveAcid() {
    saveException()
  }
}
