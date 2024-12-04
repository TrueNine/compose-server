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
package net.yan100.compose.rds.crud.entities

import jakarta.persistence.Entity
import jakarta.persistence.Table
import net.yan100.compose.rds.core.entities.IJpaEntity
import net.yan100.compose.rds.core.entities.entity
import org.hibernate.Hibernate
import java.time.Period

@Entity
@Table(name = "db_test_period_converter")
data class DbTestPeriodConverterEntity(
  var periods: Period?
) : IJpaEntity by entity() {


  constructor() : this(null)

  override fun equals(o: Any?): Boolean {
    if (this === o) return true
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false
    o as DbTestPeriodConverterEntity
    return id == o.id
  }

  override fun hashCode(): Int = javaClass.hashCode()

  override fun toString(): String {
    return this::class.simpleName + "(id=$id,rlv=$rlv,ldf=$ldf)"
  }
}
