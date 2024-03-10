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

  fun findNonDesensitizedAllByPhone(
    phone: String,
    page: Pq = Pw.DEFAULT_MAX
  ): Pr<NonDesensitizedAddressDetails>

  fun findFullAllByPhone(phone: String, page: Pq): Pr<FullAddressDetails>

  fun findAllByUserId(userId: String, page: Pq = Pw.DEFAULT_MAX): Pr<AddressDetails>

  fun findNonDesensitizedAllByUserId(
    userId: String,
    page: Pq = Pw.DEFAULT_MAX
  ): Pr<NonDesensitizedAddressDetails>

  fun findFullAllByUserId(userId: ReferenceId, page: Pq = Pw.DEFAULT_MAX): Pr<FullAddressDetails>

  fun findFullPathById(id: Id): String

  fun findAllFullPathById(ids: List<Id>): List<Pair<Id, String>>
}
