package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.base.BaseServiceImpl
import net.yan100.compose.rds.entity.AddressDetails
import net.yan100.compose.rds.entity.FullAddressDetails
import net.yan100.compose.rds.entity.NonDesensitizedAddressDetails
import net.yan100.compose.rds.repository.address.AddressDetailsRepo
import net.yan100.compose.rds.repository.address.AddressRepo
import net.yan100.compose.rds.repository.address.FullAddressDetailsRepo
import net.yan100.compose.rds.service.AddressDetailsService
import net.yan100.compose.rds.util.Pq
import net.yan100.compose.rds.util.Pr
import net.yan100.compose.rds.util.page
import net.yan100.compose.rds.util.result
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AddressDetailsServiceImpl(
  private val aRepo: AddressRepo,
  private val detailsRepo: AddressDetailsRepo,
  private val fRepo: FullAddressDetailsRepo
) : AddressDetailsService, BaseServiceImpl<AddressDetails>(detailsRepo) {
  override fun findAllByUserId(userId: String, page: Pq): Pr<AddressDetails> {
    return detailsRepo.findAllByUserId(userId, page.page).result
  }

  override fun findNonDesensitizedAllByUserId(userId: String, page: Pq): Pr<NonDesensitizedAddressDetails> {
    return detailsRepo.findNonDesensitizedAllByUserId(userId, page.page).result
  }

  override fun findFullAllByUserId(userId: String, page: Pq): Pr<FullAddressDetails> {
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

  override fun findNonDesensitizedAllByPhone(phone: String, page: Pq): Pr<NonDesensitizedAddressDetails> {
    return detailsRepo.findNonDesensitizedAllByPhone(phone, page.page).result
  }

  override fun findFullAllByPhone(phone: String, page: Pq): Pr<FullAddressDetails> {
    return fRepo.findAllByPhone(phone, page.page).result
  }
}
