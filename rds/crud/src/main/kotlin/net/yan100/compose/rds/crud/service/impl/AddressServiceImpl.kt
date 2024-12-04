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
package net.yan100.compose.rds.crud.service.impl

import jakarta.persistence.criteria.CriteriaQuery
import net.yan100.compose.core.RefId
import net.yan100.compose.core.string
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.entities.IJpaPersistentEntity
import net.yan100.compose.rds.core.entities.withNew
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.crud.entities.jpa.Address
import net.yan100.compose.rds.crud.repositories.jpa.IAddressRepo
import net.yan100.compose.rds.crud.service.IAddressService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AddressServiceImpl(
  private val aRepo: IAddressRepo
) : IAddressService, ICrud<Address> by jpa(aRepo) {

  override fun findByCode(code: string): Address? {
    return aRepo.findByCode(code)
  }

  override fun findRoot(): Address {
    return aRepo.findRoot()
  }

  override fun clearAndInitProvinces(lazy: () -> List<Address>): List<Address> {
    repo.delete { root, _: CriteriaQuery<*>?, b -> b.notEqual(root.get<String>(IJpaPersistentEntity.ID), aRepo.findRootId()) }
    val cleanedRoot = aRepo.save(aRepo.findRoot().withNew().apply { id = aRepo.findRootId() })
    return aRepo.saveChildren(cleanedRoot, lazy())
  }

  override fun findAllByCodeIn(codes: List<String>): List<Address> {
    return aRepo.findAllByCodeIn(codes)
  }

  override fun findProvinces(): List<Address> {
    return findDirectChildrenById(aRepo.findRootId())
  }

  override fun findByCodeAndLevel(code: string, level: Int): Address? {
    return aRepo.findFirstByCodeAndLevel(code, level)
  }

  override fun findDirectChildrenByCode(code: String): List<Address> {
    return aRepo.findByCode(code)?.let { aRepo.findChildren(it) } ?: listOf()
  }

  override fun findDirectChildrenById(id: RefId): List<Address> {
    return repo.findByIdOrNull(id)?.let { aRepo.findDirectChildren(it) } ?: listOf()
  }

  override fun findFullPathById(id: RefId): String {
    TODO("Not yet implemented")
  }

  override fun findFullPathByCode(code: RefId): String {
    TODO("Not yet implemented")
  }
}
