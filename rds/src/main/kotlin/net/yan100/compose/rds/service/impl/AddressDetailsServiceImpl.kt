package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.core.util.Pq
import net.yan100.compose.rds.core.util.Pr
import net.yan100.compose.rds.core.util.page
import net.yan100.compose.rds.core.util.result
import net.yan100.compose.rds.entities.AddressDetails
import net.yan100.compose.rds.repositories.address.FullAddressDetailsRepo
import net.yan100.compose.rds.repositories.address.IAddressDetailsRepo
import net.yan100.compose.rds.repositories.address.IAddressRepo
import net.yan100.compose.rds.service.IAddressDetailsService
import net.yan100.compose.rds.service.base.CrudService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AddressDetailsServiceImpl(
  private val aRepo: IAddressRepo,
  private val detailsRepo: IAddressDetailsRepo,
  private val fRepo: FullAddressDetailsRepo
) : IAddressDetailsService, CrudService<AddressDetails>(detailsRepo) {
  override fun findAllByUserId(userId: String, page: Pq): Pr<AddressDetails> {
    return detailsRepo.findAllByUserId(userId, page.page).result
  }

  override fun findNonDesensitizedAllByUserId(userId: String, page: Pq): Pr<net.yan100.compose.rds.entities.NonDesensitizedAddressDetails> {
    return detailsRepo.findNonDesensitizedAllByUserId(userId, page.page).result
  }

  override fun findFullAllByUserId(userId: String, page: Pq): Pr<net.yan100.compose.rds.entities.FullAddressDetails> {
    return fRepo.findAllByUserId(userId, page.page).result
  }

  override fun findFullPathById(id: String): String {
    return detailsRepo.findByIdOrNull(id)?.let { ad ->
      val adPath = ad.addressCode?.let { addrCode ->
        aRepo.findFirstByCode(addrCode)?.let { addr ->
          aRepo.findParentPath(addr)
        }
      }?.map { it.name }?.joinToString(separator = "") ?: ""
      val maybePath = ad.addressDetails ?: ""
      "$adPath$maybePath"
    } ?: ""
  }


  override fun findAllFullPathById(ids: List<String>): List<Pair<String, String>> {
    return detailsRepo.findAllById(ids).let { ds ->
      // 地址的路径集合
      val addresses = aRepo.findAllByCodeIn(ds.map { it.addressCode!! }).map { addr ->
        addr.id to aRepo.findParentPath(addr)
          .sortedBy {
            it.code
          }
          .map { it.name }.joinToString(separator = "") + addr.name
      }
      ds.map { dss ->
        val b = addresses.find { it.first == dss.addressId }
        dss.id!! to ((b?.second ?: "") + (dss.addressDetails ?: ""))
      }
    }
  }

  override fun findAllByPhone(phone: String, page: Pq): Pr<AddressDetails> {
    return detailsRepo.findAllByPhone(phone, page.page).result
  }

  override fun findNonDesensitizedAllByPhone(phone: String, page: Pq): Pr<net.yan100.compose.rds.entities.NonDesensitizedAddressDetails> {
    return detailsRepo.findNonDesensitizedAllByPhone(phone, page.page).result
  }

  override fun findFullAllByPhone(phone: String, page: Pq): Pr<net.yan100.compose.rds.entities.FullAddressDetails> {
    return fRepo.findAllByPhone(phone, page.page).result
  }
}
