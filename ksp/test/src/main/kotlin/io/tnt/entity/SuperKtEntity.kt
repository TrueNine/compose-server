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
package io.tnt.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.ksp.core.annotations.MetaName
import java.math.BigDecimal
import java.time.LocalDate

@MetaDef
@MetaName("kt")
class SuperKtEntity : SuperBaseEntity(), Cloneable {
  var delegatedNotNull: Int by @Suppress("DEPRECATION_ERROR") late()

  var decimal: BigDecimal? = null

  var annotationNonNull: Int by @Suppress("DEPRECATION_ERROR") late()

  /**
   * ## test doc
   *
   * this is documentation
   */
  var date: LocalDate? = null

  /** KDocumentation */
  var firstName: String? = null

  @Schema(title = "我的")
  var lastName: String? = null

  val fullName: String
    get() = "$firstName $lastName"

  @Column(name = "adl_cc")
  private var abc: String? = null

  @Column(name = "adl_cc")
  var ddd: String? = null

  @Column(name = "bd")
  var birthday: LocalDate? = null

  override fun toString(): String {
    return super.toString()
  }
}
