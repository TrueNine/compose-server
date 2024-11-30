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
package net.yan100.compose.rds.entities

import jakarta.persistence.ConstraintMode.NO_CONSTRAINT
import jakarta.persistence.FetchType.EAGER
import jakarta.persistence.ForeignKey
import jakarta.persistence.JoinColumn
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.OneToMany
import net.yan100.compose.meta.annotations.MetaDef
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode.SUBSELECT

@MetaDef(shadow = true)
@MappedSuperclass
abstract class SuperFullAddress : SuperAddress() {
  /** 当前地址包含的地址详情 */
  @OneToMany(targetEntity = AddressDetails::class, fetch = EAGER)
  @JoinColumn(name = AddressDetails.ADDRESS_ID, referencedColumnName = ID, foreignKey = ForeignKey(NO_CONSTRAINT), insertable = false, updatable = false)
  @Fetch(SUBSELECT)
  open var details: List<AddressDetails> = listOf()
}
