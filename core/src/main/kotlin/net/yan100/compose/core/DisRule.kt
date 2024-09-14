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
import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema

@Schema(title = "残疾信息规则")
open class DisRule(@JsonIgnore private var disRuleMeta: ByteArray? = null) {
  fun init() {
    disRuleMeta = disRuleMeta ?: ByteArray(RULE_LENGTH)
    if (disRuleMeta!!.size != RULE_LENGTH) disRuleMeta = disRuleMeta!!.copyOf(RULE_LENGTH)
    for (i in disRuleMeta!!.indices) {
      disRuleMeta!![i] = if (disRuleMeta!![i] > 0) TRUE else FALSE
    }
  }

  init {
    init()
  }

  @get:JsonValue
  @set:JsonValue
  var meta: ByteArray
    get() = this.disRuleMeta!!
    set(f) {
      this.disRuleMeta = f
      init()
    }

  fun match(type: Int, level: Int): Boolean {
    check(type in 1..7) { "不是残疾类型" }
    check(level in 1..4) { "残疾级别超过4级" }

    val startIdx = (type * LEVEL_MAX) - LEVEL_MAX
    val endIdx = (startIdx + level) - 1
    return disRuleMeta!![endIdx] == TRUE
  }

  /**
   * ## 是否禁用
   *
   * 全部为 0
   */
  @get:JsonIgnore
  val isDisabled
    get() = disRuleMeta!!.all { it == FALSE }

  companion object {
    const val TRUE = 1.toByte()
    const val FALSE = 0.toByte()
    const val RULE_LENGTH = 28
    const val LEVEL_MAX = 4
  }
}
