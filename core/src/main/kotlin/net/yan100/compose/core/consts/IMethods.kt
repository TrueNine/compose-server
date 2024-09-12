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
package net.yan100.compose.core.consts


interface IMethods {
  companion object {
    fun all(): Array<String> {
      return arrayOf(GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD, TRACE)
    }

    const val GET: String = "GET"
    const val POST: String = "POST"
    const val PUT: String = "PUT"
    const val DELETE: String = "DELETE"
    const val OPTIONS: String = "OPTIONS"
    const val PATCH: String = "PATCH"
    const val HEAD: String = "HEAD"
    const val TRACE: String = "TRACE"
  }
}
