package net.yan100.compose.rds.crud.repositories.jimmer

import net.yan100.compose.core.RefId
import net.yan100.compose.rds.core.IJimmerTreeRepo
import net.yan100.compose.rds.crud.entities.jimmer.Address
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Repository
interface IJimmerAddressRepo : IJimmerTreeRepo<Address, RefId>
