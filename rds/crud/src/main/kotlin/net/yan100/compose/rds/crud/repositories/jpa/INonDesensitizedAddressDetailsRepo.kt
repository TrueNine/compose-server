package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.crud.entities.jpa.NonDesensitizedAddressDetails
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Primary
@Repository
interface INonDesensitizedAddressDetailsRepo :
  IRepo<NonDesensitizedAddressDetails>
