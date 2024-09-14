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
package net.yan100.compose.core.domain

interface IChinaName {
  companion object {
    @JvmStatic
    operator fun get(name: String): IChinaName = DefaultChinaName(name)

    @JvmStatic
    fun splitName(name: String): Pair<String, String> {
      val cName = name.trim()
      check(cName.length in 2..4) { "姓名 $cName 长度不符合要求" }
      return if (name.length == 2) Pair(name.substring(0, 1), name.substring(1))
      else if (name.length == 3) Pair(name.substring(0, 1), name.substring(1))
      else if (name.length == 4 && name[2] == ' ') Pair(name.substring(0, 2), name.substring(3))
      else if (name.length == 4) Pair(name.substring(0, 2), name.substring(2)) else error("姓名 $cName 格式不符合要求")
    }
  }

  private class DefaultChinaName(override val fullName: String) : IChinaName {
    private val f: String
    private val l: String

    init {
      val (f, l) = splitName(fullName)
      this.f = f
      this.l = l
    }

    override val firstName: String = f
    override val lastName: String = l
  }

  val fullName: String
  val firstName: String
  val lastName: String
}
