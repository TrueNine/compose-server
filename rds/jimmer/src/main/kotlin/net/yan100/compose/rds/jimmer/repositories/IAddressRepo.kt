package net.yan100.compose.rds.jimmer.repositories

import net.yan100.compose.core.RefId
import net.yan100.compose.rds.jimmer.IJimmerTreeRepo
import net.yan100.compose.rds.jimmer.entities.Address
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Repository
interface IAddressRepo : IJimmerTreeRepo<Address, RefId>
