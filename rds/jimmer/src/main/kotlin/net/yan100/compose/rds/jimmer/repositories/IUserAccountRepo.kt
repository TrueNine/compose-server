package net.yan100.compose.rds.jimmer.repositories

import net.yan100.compose.core.RefId
import net.yan100.compose.rds.jimmer.IJimmerRepo
import net.yan100.compose.rds.jimmer.entities.UserAccount
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Repository
interface IUserAccountRepo : IJimmerRepo<UserAccount, RefId>
