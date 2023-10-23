package net.yan100.compose.rds.repository.address

import net.yan100.compose.rds.entity.NonDesensitizedAddressDetails
import org.springframework.data.jpa.repository.JpaRepository

interface NonDesensitizedAddressDetailsRepository: JpaRepository<NonDesensitizedAddressDetails, String>
