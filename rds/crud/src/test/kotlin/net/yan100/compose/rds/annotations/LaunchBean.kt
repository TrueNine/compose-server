package net.yan100.compose.rds.annotations

import net.yan100.compose.rds.core.annotations.ACID
import net.yan100.compose.rds.entities.Usr
import net.yan100.compose.rds.repositories.IUsrRepo
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class LaunchBean(
  private val repo: IUsrRepo
) {

  fun saveOk() {
    repo.save(Usr().apply {
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
  fun throwSave() {
    saveException()
  }

  @ACID
  fun throwSaveAcid() {
    saveException()
  }
}
