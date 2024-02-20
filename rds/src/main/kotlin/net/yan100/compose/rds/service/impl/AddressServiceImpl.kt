/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.rds.service.impl

import jakarta.persistence.criteria.CriteriaQuery
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.rds.core.entities.AnyEntity
import net.yan100.compose.rds.core.entities.withNew
import net.yan100.compose.rds.entities.Address
import net.yan100.compose.rds.repositories.address.IAddressRepo
import net.yan100.compose.rds.service.IAddressService
import net.yan100.compose.rds.service.base.CrudService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AddressServiceImpl(private val repo: IAddressRepo) :
  IAddressService, CrudService<Address>(repo) {
  override fun findRoot(): Address {
    return repo.findRoot()
  }

  override fun clearAndInitProvinces(lazy: () -> List<Address>): List<Address> {
    repo.delete { root, _: CriteriaQuery<*>?, b ->
      b.notEqual(root.get<String>(AnyEntity.ID), repo.findRootId())
    }
    val cleanedRoot = repo.save(repo.findRoot().withNew().apply { id = repo.findRootId() })
    return repo.saveChildren(cleanedRoot, lazy())
  }

  override fun findAllByCodeIn(codes: List<String>): List<Address> {
    return repo.findAllByCodeIn(codes)
  }

  override fun findProvinces(): List<Address> {
    return findDirectChildrenById(repo.findRootId())
  }

  override fun findByCodeAndLevel(code: SerialCode, level: Int): Address? {
    return repo.findFirstByCodeAndLevel(code, level)
  }

  override fun findDirectChildrenByCode(code: String): List<Address> {
    return repo.findByCode(code)?.let { repo.findChildren(it) } ?: listOf()
  }

  override fun findDirectChildrenById(id: RefId): List<Address> {
    return repo.findByIdOrNull(id)?.let { repo.findDirectChildren(it) } ?: listOf()
  }

  override fun findFullPathById(id: String): String {
    TODO("Not yet implemented")
  }

  override fun findFullPathByCode(code: String): String {
    TODO("Not yet implemented")
  }
}
