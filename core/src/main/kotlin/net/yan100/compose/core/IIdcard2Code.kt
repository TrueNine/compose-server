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
package net.yan100.compose.core

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Transient
import net.yan100.compose.core.consts.IRegexes
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

/** # 二代身份证代码 */
interface IIdcard2Code {

  private class DefaultIdcard2Code(override val idcard2Code: String) : IIdcard2Code {
    init {
      check(idcardBirthday.isBefore(LocalDate.now())) { "$idcard2Code is not a valid idcard2Code" }
      check(idcard2Code.matches(idCardRegex)) { "$idcard2Code is not a valid idcard2Code" }
    }

    companion object {
      private val idCardRegex = IRegexes.CHINA_ID_CARD.toRegex()
    }

    override fun hashCode(): Int = Objects.hashCode(idcard2Code)

    override fun equals(other: Any?): Boolean {
      if (null == other) return false
      return if (other is IIdcard2Code) {
        other.idcard2Code == idcard2Code
      } else false
    }

    override fun toString(): String = idcard2Code
  }

  companion object {
    @JvmStatic operator fun get(idcard2Code: String): IIdcard2Code = DefaultIdcard2Code(idcard2Code.uppercase())
  }

  @get:Transient @get:JsonIgnore val idcard2Code: String

  @get:Transient
  @get:JsonIgnore
  val idcardBirthday: LocalDate
    get() = LocalDate.of(idcard2Code.substring(6, 10).toInt(), idcard2Code.substring(10, 12).toInt(), idcard2Code.substring(12, 14).toInt())

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
  val idcardAge: Int
    get() = ChronoUnit.YEARS.between(idcardBirthday, LocalDate.now()).toInt()

  @get:Transient
  @get:JsonIgnore
  val idcardRecommendAvailabilityYear: Int
    get() {
      return when (idcardAge) {
        in 0..16 -> 5
        in 17..25 -> 10
        in 26..45 -> 20
        else -> -1
      }
    }

  /**
   * ## 行政区划码
   *
   * 不一定有效，可能会夹杂 00
   */
  @get:Transient
  @get:JsonIgnore
  val idcardDistrictCode: String
    get() = idcard2Code.substring(0, 6)

  @get:Transient
  @get:JsonIgnore
  val idcardUpperCase: String
    get() = idcardDistrictCode.uppercase()
}
