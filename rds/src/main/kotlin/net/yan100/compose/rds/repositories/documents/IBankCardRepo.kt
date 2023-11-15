package net.yan100.compose.rds.repositories.documents

import net.yan100.compose.rds.entities.documents.BankCard
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.stereotype.Repository

@Repository
interface IBankCardRepo : IRepo<BankCard>
