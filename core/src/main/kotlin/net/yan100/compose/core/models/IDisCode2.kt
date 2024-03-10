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

/** 二代残疾证代码 */
interface IDisCode2 : IIdcard2Code {
  private class DefaultDis2Code(dCode: String) : IDisCode2 {
    override val disabilityCode: String = dCode
  }

  companion object {
    @JvmStatic
    fun of(code: String): IDisCode2 {
      return DefaultDis2Code(code)
    }
  }

  @get:Transient
  @get:JsonIgnore
  override val idcard2Code: String
    get() = disabilityCode.substring(0, -2)

  @get:Transient @get:JsonIgnore val disabilityCode: String
}
