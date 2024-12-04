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
  fun fetchAllByPhone(phone: String, page: Pq = Pq.DEFAULT_MAX): Pr<AddressDetails>

  fun fetchNonDesensitizedAllByPhone(phone: String, pq: Pq = Pq.DEFAULT_MAX): Pr<NonDesensitizedAddressDetails>

  @Deprecated("vo")
  fun fetchFullAllByPhone(phone: String, page: Pq): Pr<FullAddressDetails>

  fun fetchAllByUserId(userId: String, page: Pq = Pq.DEFAULT_MAX): Pr<AddressDetails>

  fun fetchNonDesensitizedAllByUserId(userId: String, page: Pq = Pq.DEFAULT_MAX): Pr<NonDesensitizedAddressDetails>

  fun fetchFullAllByUserId(userId: RefId, page: Pq = Pq.DEFAULT_MAX): Pr<FullAddressDetails>

  fun fetchFullPathById(id: Id): String

  fun fetchAllFullPathById(ids: List<Id>): List<Pair<Id, String>>
}
