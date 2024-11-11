package net.yan100.compose.rds.repositories

import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.entities.FullUsr
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Repository
interface IFullUserRepo : IRepo<FullUsr> {
  fun findByAccount(account: String): FullUsr?
}
