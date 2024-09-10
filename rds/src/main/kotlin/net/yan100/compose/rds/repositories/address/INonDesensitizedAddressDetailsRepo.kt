package net.yan100.compose.rds.repositories.address

import net.yan100.compose.rds.entities.address.NonDesensitizedAddressDetails
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.stereotype.Repository

@Repository
interface INonDesensitizedAddressDetailsRepo : IRepo<NonDesensitizedAddressDetails>
