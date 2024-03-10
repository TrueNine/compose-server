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
package net.yan100.compose.core.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Transient
import java.time.LocalDate

/** # 二代身份证代码 */
interface IIdcard2Code {
  private class DefaultIdcard2Code(code: String) : IIdcard2Code {
    override val idcard2Code: String = code
  }

  companion object {
    @JvmStatic
    fun of(idcard2Code: String): IIdcard2Code {
      return DefaultIdcard2Code(idcard2Code)
    }
  }

  @get:Transient @get:JsonIgnore val idcard2Code: String

  @get:Transient
  @get:JsonIgnore
  val idcardBirthday: LocalDate
    get() =
      LocalDate.of(
        idcard2Code.substring(6, 10).toInt(),
        idcard2Code.substring(10, 12).toInt(),
        idcard2Code.substring(12, 14).toInt()
      )

  @get:Transient
  @get:JsonIgnore
  val idcardSexCode: String
    get() = idcard2Code.substring(16, 17)

  @get:Transient
  @get:JsonIgnore
  val idcardSex: Boolean
    get() = idcardSexCode.toByte() % 2 != 0

  @get:Transient
  @get:JsonIgnore
  val idcardDistrictCode: String
    get() = idcard2Code.substring(0, 6)

  @get:Transient
  @get:JsonIgnore
  val idcardUpperCase: String
    get() = idcardDistrictCode.uppercase()
}
