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
package net.yan100.compose.core.lang

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class StrTest {
  @Test
  fun testNonText() {
    assertTrue { Str.nonText("") }
  }

  @Test
  fun testInLine() {
    val a = Str.inLine("1\n")
    assertFalse { a.contains("\n") }
  }

  @Test
  fun testHasText() {
    assertTrue { Str.hasText("abc") }
  }

  @Test
  fun testOmit() {
    val b = Str.omit("abc", 2)
    assertFalse { b.contains("c") }
  }

  // @Test
  fun a() {
    val names = mutableLockListOf("", "十", "百", "千", "万", "亿").reversed()
    val numbers = charArrayOf('零', '一', '二', '三', '四', '五', '六', '七', '八', '九')

    val a = 9102039402394
    val str = a.toString().run { String(map { numbers[it.code - 48] }.toCharArray()) }

    val groups = str.chunked(4).toMutableList()
    for ((index, s) in groups.withIndex()) {
      groups[index] = "${s}${names[index + groups.size]}"
    }
    println(groups)
  }
}
