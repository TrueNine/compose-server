/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.core.models

import io.swagger.v3.oas.annotations.media.Schema

@Schema(title = "kotlin 入参替代品")
class KPair<K, V> {
  var first: K? = null
  var second: V? = null

  @Suppress("UNCHECKED_CAST")
  fun toPair(): Pair<K, V> {
    return Pair(first, second) as Pair<K, V>
  }
}
