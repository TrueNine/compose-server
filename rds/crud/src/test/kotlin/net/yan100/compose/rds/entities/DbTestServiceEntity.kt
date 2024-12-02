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

import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Table
import net.yan100.compose.core.Id
import net.yan100.compose.core.RefId
import net.yan100.compose.core.datetime
import net.yan100.compose.core.domain.Coordinate
import net.yan100.compose.core.i64
import net.yan100.compose.rds.converters.WGS84Converter
import net.yan100.compose.rds.core.entities.IEntity

@Entity
@Table(name = DbTestServiceEntity.TABLE_NAME)
class DbTestServiceEntity : IEntity {
  private var internalId: RefId = ""
  override var rlv: i64?
    get() = TODO("Not yet implemented")
    set(value) {}
  override var crd: datetime?
    get() = TODO("Not yet implemented")
    set(value) {}
  override var mrd: datetime?
    get() = TODO("Not yet implemented")
    set(value) {}
  override var ldf: Boolean?
    get() = TODO("Not yet implemented")
    set(value) {}

  override fun setId(id: Id) {
    internalId = id
  }

  @jakarta.persistence.Id
  override fun getId(): Id {
    return internalId
  }

  var title: String? = null

  @Convert(converter = WGS84Converter::class)
  var center: Coordinate? = null

  companion object {

    const val TABLE_NAME = "db_test_service"
  }
}
