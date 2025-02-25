package net.yan100.compose.rds.crud.service.impl

import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.crud.entities.jpa.HouseholdCert
import net.yan100.compose.rds.crud.repositories.jpa.IHouseholdRegistrationCardRepo
import org.springframework.stereotype.Service

@Service
class HouseholdRegistrationCardServiceImpl(
  val hRepo: IHouseholdRegistrationCardRepo
) : ICrud<HouseholdCert> by jpa(hRepo)
