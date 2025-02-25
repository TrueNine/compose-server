package net.yan100.compose.rds.crud.service

import net.yan100.compose.core.Id
import net.yan100.compose.core.Pq
import net.yan100.compose.core.Pr
import net.yan100.compose.core.RefId
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.crud.entities.jpa.AddressDetails
import net.yan100.compose.rds.crud.entities.jpa.FullAddressDetails
import net.yan100.compose.rds.crud.entities.jpa.NonDesensitizedAddressDetails

interface IAddressDetailsService : ICrud<AddressDetails> {
  fun fetchAllByPhone(
    phone: String,
    page: Pq = Pq.DEFAULT_MAX,
  ): Pr<AddressDetails>

  fun fetchNonDesensitizedAllByPhone(
    phone: String,
    pq: Pq = Pq.DEFAULT_MAX,
  ): Pr<NonDesensitizedAddressDetails>

  @Deprecated("vo")
  fun fetchFullAllByPhone(phone: String, page: Pq): Pr<FullAddressDetails>

  fun fetchAllByUserId(
    userId: String,
    page: Pq = Pq.DEFAULT_MAX,
  ): Pr<AddressDetails>

  fun fetchNonDesensitizedAllByUserId(
    userId: String,
    page: Pq = Pq.DEFAULT_MAX,
  ): Pr<NonDesensitizedAddressDetails>

  fun fetchFullAllByUserId(
    userId: RefId,
    page: Pq = Pq.DEFAULT_MAX,
  ): Pr<FullAddressDetails>

  fun fetchFullPathById(id: Id): String

  fun fetchAllFullPathById(ids: List<Id>): List<Pair<Id, String>>
}
