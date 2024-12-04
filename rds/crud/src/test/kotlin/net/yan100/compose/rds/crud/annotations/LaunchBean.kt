package net.yan100.compose.rds.crud.annotations

import net.yan100.compose.rds.core.annotations.ACID
import net.yan100.compose.rds.crud.entities.jpa.UserAccount
import net.yan100.compose.rds.crud.repositories.jpa.IUserAccountRepo
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class LaunchBean(
  private val repo: IUserAccountRepo
) {

  fun saveOk() {
    repo.save(UserAccount().apply {
      account = "abcdeferqwrqwrqwrqwreqweqw"
      pwdEnc = "abcdefghijk"
      nickName = "呢称名字"
    })
  }

  fun saveException() {
    saveOk()
    error("发出错误")
  }

  @Transactional(rollbackFor = [Exception::class])
  fun throwTransactionalSave() {
    saveException()
  }

  @ACID
  fun throwSaveAcid() {
    saveException()
  }
}
