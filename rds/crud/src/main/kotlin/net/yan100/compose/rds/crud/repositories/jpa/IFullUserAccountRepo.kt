package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.crud.entities.jpa.FullUserAccount
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Repository
@Deprecated("关联过于复杂")
interface IFullUserAccountRepo : IRepo<FullUserAccount> {
  fun findByAccount(account: String): FullUserAccount?
}
