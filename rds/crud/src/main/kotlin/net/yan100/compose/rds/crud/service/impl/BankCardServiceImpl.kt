package net.yan100.compose.rds.crud.service.impl

import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.crud.entities.jpa.BankCard
import net.yan100.compose.rds.crud.repositories.jpa.IBankCardRepo
import net.yan100.compose.rds.crud.service.IBankCardService
import org.springframework.stereotype.Service

@Service
class BankCardServiceImpl(private val bRepo: IBankCardRepo) :
  IBankCardService, ICrud<BankCard> by jpa(bRepo)
