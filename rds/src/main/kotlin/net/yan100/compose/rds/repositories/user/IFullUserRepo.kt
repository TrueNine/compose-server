package net.yan100.compose.rds.repositories.user

import net.yan100.compose.rds.entities.account.FullUsr
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.stereotype.Repository

@Repository
interface IFullUserRepo : IRepo<FullUsr> {
  fun findByAccount(account: String): FullUsr?
}
