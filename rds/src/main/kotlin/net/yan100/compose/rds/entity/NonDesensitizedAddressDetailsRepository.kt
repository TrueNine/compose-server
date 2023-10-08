package net.yan100.compose.rds.entity;

import org.springframework.data.jpa.repository.JpaRepository

interface NonDesensitizedAddressDetailsRepository: JpaRepository<NonDesensitizedAddressDetails, String> {
}
