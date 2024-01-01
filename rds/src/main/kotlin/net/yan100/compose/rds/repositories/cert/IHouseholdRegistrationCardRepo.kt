package net.yan100.compose.rds.repositories.cert

import net.yan100.compose.rds.entities.cert.HouseholdCert
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.stereotype.Repository

@Repository
interface IHouseholdRegistrationCardRepo : IRepo<HouseholdCert>
