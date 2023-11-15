package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.entities.documents.HouseholdRegistrationCard
import net.yan100.compose.rds.repositories.documents.IHouseholdRegistrationCardRepo
import net.yan100.compose.rds.service.base.CrudService
import org.springframework.stereotype.Service

@Service
class HouseholdRegistrationCardServiceImpl(
  val repo: IHouseholdRegistrationCardRepo
) : CrudService<HouseholdRegistrationCard>(repo)
