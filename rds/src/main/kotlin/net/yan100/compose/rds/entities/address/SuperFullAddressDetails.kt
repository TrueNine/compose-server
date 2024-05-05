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
package net.yan100.compose.rds.entities.address

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import net.yan100.compose.ksp.annotations.MetaDef
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction

@MetaDef
abstract class SuperFullAddressDetails : SuperAddressDetails() {
  /** 地址 */
  @ManyToOne(fetch = FetchType.EAGER)
  @Schema(title = "地址", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JoinColumn(name = "address_id", referencedColumnName = ID, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false)
  @NotFound(action = NotFoundAction.IGNORE)
  @Fetch(FetchMode.JOIN)
  var address: Address? = null
}
