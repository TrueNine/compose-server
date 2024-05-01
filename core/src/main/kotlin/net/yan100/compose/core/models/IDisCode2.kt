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
import net.yan100.compose.core.alias.SerialCode

/** 二代残疾证代码 */
interface IDisCode2 : IIdcard2Code {
  private class DefaultDis2Code(dCode: SerialCode) : IDisCode2 {
    override val disabilityCode: SerialCode = dCode
  }

  companion object {
    @JvmStatic
    fun of(code: String): IDisCode2 {
      return DefaultDis2Code(code)
    }
  }

  @get:Transient
  @get:JsonIgnore
  override val idcard2Code: SerialCode
    get() = disabilityCode.substring(0, 18)

  /** ## 残疾证号 */
  @get:Transient @get:JsonIgnore val disabilityCode: SerialCode

  /**
   * ## 是否补办过
   * 根据第 20 位是否有 b 判断是否有补办过
   */
  @get:Transient
  @get:JsonIgnore
  val codeReIssued: Boolean
    get() = disabilityCode.substring(19, 20).uppercase() == "B"

  /**
   * 补办次数 根据第 21 位 来解析补办次数
   *
   * @see codeReIssued
   */
  @get:Transient
  @get:JsonIgnore
  val codeReIssuedCount: Byte?
    get() = disabilityCode.substring(20, 21).toByteOrNull()
}
