package net.yan100.compose.rds.crud.repositories.jimmer

import net.yan100.compose.core.RefId
import net.yan100.compose.rds.core.IJimmerRepo
import net.yan100.compose.rds.crud.entities.jimmer.UserAccount
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Repository
interface IJimmerUserAccountRepo : IJimmerRepo<UserAccount, RefId>
