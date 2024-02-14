package net.yan100.compose.rds.service

import net.yan100.compose.core.alias.Id
import net.yan100.compose.core.alias.ReferenceId
import net.yan100.compose.rds.core.util.Pq
import net.yan100.compose.rds.core.util.Pr
import net.yan100.compose.rds.core.util.Pw
import net.yan100.compose.rds.entities.AddressDetails
import net.yan100.compose.rds.entities.FullAddressDetails
import net.yan100.compose.rds.entities.NonDesensitizedAddressDetails
import net.yan100.compose.rds.service.base.IService


interface IAddressDetailsService : IService<AddressDetails> {
    fun findAllByPhone(phone: String, page: Pq = Pw.DEFAULT_MAX): Pr<AddressDetails>
    fun findNonDesensitizedAllByPhone(phone: String, page: Pq = Pw.DEFAULT_MAX): Pr<NonDesensitizedAddressDetails>
    fun findFullAllByPhone(phone: String, page: Pq): Pr<FullAddressDetails>

    fun findAllByUserId(userId: String, page: Pq = Pw.DEFAULT_MAX): Pr<AddressDetails>
    fun findNonDesensitizedAllByUserId(userId: String, page: Pq = Pw.DEFAULT_MAX): Pr<NonDesensitizedAddressDetails>
    fun findFullAllByUserId(userId: ReferenceId, page: Pq = Pw.DEFAULT_MAX): Pr<FullAddressDetails>

    fun findFullPathById(id: Id): String
    fun findAllFullPathById(ids: List<Id>): List<Pair<Id, String>>
}
