/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.rds.service.impl

import net.yan100.compose.core.Pq
import net.yan100.compose.core.Pr
import net.yan100.compose.core.ReferenceId
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.core.page
import net.yan100.compose.rds.core.result
import net.yan100.compose.rds.entities.AddressDetails
import net.yan100.compose.rds.entities.FullAddressDetails
import net.yan100.compose.rds.entities.NonDesensitizedAddressDetails
import net.yan100.compose.rds.repositories.IAddressDetailsRepo
import net.yan100.compose.rds.repositories.IAddressRepo
import net.yan100.compose.rds.repositories.IFullAddressDetailsRepo
import net.yan100.compose.rds.service.IAddressDetailsService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AddressDetailsServiceImpl(
    private val aRepo: IAddressRepo,
    override val repo: IAddressDetailsRepo,
    private val fRepo: IFullAddressDetailsRepo
) : IAddressDetailsService, ICrud<AddressDetails> by jpa(repo) {
  override fun fetchAllByUserId(userId: String, page: Pq): Pr<AddressDetails> {
    return repo.findAllByUserId(userId, page.page).result
  }

  override fun fetchNonDesensitizedAllByUserId(userId: String, page: Pq): Pr<NonDesensitizedAddressDetails> {
    return repo.findNonDesensitizedAllByUserId(userId, page.page).result
  }

  override fun fetchFullAllByUserId(userId: String, page: Pq): Pr<FullAddressDetails> {
    return fRepo.findAllByUserId(userId, page.page).result
  }

  override fun fetchFullPathById(id: String): String {
    return repo.findByIdOrNull(id)?.let { ad ->
      val adPath =
        ad.addressCode.let { addrCode -> aRepo.findFirstByCode(addrCode)?.let { addr -> aRepo.findParentPath(addr) } }?.joinToString(separator = "") { it.name }
          ?: ""
      val maybePath = ad.addressDetails
      "$adPath$maybePath"
    } ?: ""
  }

  override fun fetchAllFullPathById(ids: List<ReferenceId>): List<Pair<ReferenceId, String>> {
    return repo.findAllById(ids).let { ds ->
      // 地址的路径集合
      val addresses =
        aRepo.findAllByCodeIn(ds.map { it.addressCode }).map { addr ->
          addr.id to aRepo.findParentPath(addr).sortedBy { it.code }.joinToString(separator = "") { it.name } + addr.name
        }
      ds.map { dss ->
        val b = addresses.find { it.first == dss.addressId }
        dss.id to ((b?.second ?: "") + dss.addressDetails)
      }
    }
  }

  override fun fetchAllByPhone(phone: String, page: Pq): Pr<AddressDetails> {
    return repo.findAllByPhone(phone, page.page).result
  }

  override fun fetchNonDesensitizedAllByPhone(phone: String, pq: Pq): Pr<NonDesensitizedAddressDetails> {
    return repo.findNonDesensitizedAllByPhone(phone, pq.page).result
  }

  override fun fetchFullAllByPhone(phone: String, page: Pq): Pr<FullAddressDetails> {
    return fRepo.findAllByPhone(phone, page.page).result
  }
}
