package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.rds.IRepo
import net.yan100.compose.rds.crud.entities.jpa.BankCard
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Deprecated("弃用 JPA")
@Repository("IBankCardRepository")
interface IBankCardRepo : IRepo<BankCard>
