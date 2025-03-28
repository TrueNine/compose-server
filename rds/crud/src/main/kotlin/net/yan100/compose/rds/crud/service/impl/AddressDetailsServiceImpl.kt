package net.yan100.compose.rds.crud.service.impl

import net.yan100.compose.core.Pq
import net.yan100.compose.core.Pr
import net.yan100.compose.core.RefId
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.core.toPageable
import net.yan100.compose.rds.core.toPr
import net.yan100.compose.rds.crud.entities.jpa.AddressDetails
import net.yan100.compose.rds.crud.entities.jpa.FullAddressDetails
import net.yan100.compose.rds.crud.entities.jpa.NonDesensitizedAddressDetails
import net.yan100.compose.rds.crud.repositories.jpa.IAddressDetailsRepo
import net.yan100.compose.rds.crud.repositories.jpa.IAddressRepo
import net.yan100.compose.rds.crud.repositories.jpa.IFullAddressDetailsRepo
import net.yan100.compose.rds.crud.service.IAddressDetailsService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AddressDetailsServiceImpl(
  private val aRepo: IAddressRepo,
  override val repo: IAddressDetailsRepo,
  private val fRepo: IFullAddressDetailsRepo,
) : IAddressDetailsService, ICrud<AddressDetails> by jpa(repo) {
  override fun fetchAllByUserId(userId: String, page: Pq): Pr<AddressDetails> {
    return repo.findAllByUserId(userId, page.toPageable()).toPr()
  }

  override fun fetchNonDesensitizedAllByUserId(
    userId: String,
    page: Pq,
  ): Pr<NonDesensitizedAddressDetails> {
    return repo.findNonDesensitizedAllByUserId(userId, page.toPageable()).toPr()
  }

  override fun fetchFullAllByUserId(
    userId: RefId,
    page: Pq,
  ): Pr<FullAddressDetails> {
    return fRepo.findAllByUserId(userId, page.toPageable()).toPr()
  }

  override fun fetchFullPathById(id: RefId): String {
    return repo.findByIdOrNull(id)?.let { ad ->
      val adPath =
        ad.addressCode
          .let { addrCode ->
            aRepo.findFirstByCode(addrCode)?.let { addr ->
              aRepo.findParentPath(addr)
            }
          }
          ?.joinToString(separator = "") { it.name } ?: ""
      val maybePath = ad.addressDetails
      "$adPath$maybePath"
    } ?: ""
  }

  override fun fetchAllFullPathById(
    ids: List<RefId>
  ): List<Pair<RefId, String>> {
    return repo.findAllById(ids).let { ds ->
      // 地址的路径集合
      val addresses =
        aRepo.findAllByCodeIn(ds.map { it.addressCode }).map { addr ->
          addr.id to
            aRepo
              .findParentPath(addr)
              .sortedBy { it.code }
              .joinToString(separator = "") { it.name } + addr.name
        }
      ds.map { dss ->
        val b = addresses.find { it.first == dss.addressId }
        dss.id to ((b?.second ?: "") + dss.addressDetails)
      }
    }
  }

  override fun fetchAllByPhone(phone: String, page: Pq): Pr<AddressDetails> {
    return repo.findAllByPhone(phone, page.toPageable()).toPr()
  }

  override fun fetchNonDesensitizedAllByPhone(
    phone: String,
    pq: Pq,
  ): Pr<NonDesensitizedAddressDetails> {
    return repo.findNonDesensitizedAllByPhone(phone, pq.toPageable()).toPr()
  }

  override fun fetchFullAllByPhone(
    phone: String,
    page: Pq,
  ): Pr<FullAddressDetails> {
    return fRepo.findAllByPhone(phone, page.toPageable()).toPr()
  }
}
