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
package net.yan100.compose.rds.service

import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.rds.entities.Address
import net.yan100.compose.rds.service.base.IService

interface IAddressService : IService<Address> {
  fun findRoot(): Address

  fun clearAndInitProvinces(lazy: () -> List<Address>): List<Address>

  fun findProvinces(): List<Address>

  fun findByCodeAndLevel(code: SerialCode, level: Int): Address?

  fun findDirectChildrenByCode(code: String): List<Address>

  fun findDirectChildrenById(id: String): List<Address>

  fun findFullPathById(id: RefId): String

  fun findFullPathByCode(code: RefId): String

  fun findAllByCodeIn(codes: List<String>): List<Address>
}
