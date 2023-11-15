package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.entities.documents.BankCard
import net.yan100.compose.rds.repositories.documents.IBankCardRepo
import net.yan100.compose.rds.service.base.CrudService
import org.springframework.stereotype.Service

@Service
class BankCardServiceImpl(
  val repo: IBankCardRepo
) : CrudService<BankCard>(repo)
