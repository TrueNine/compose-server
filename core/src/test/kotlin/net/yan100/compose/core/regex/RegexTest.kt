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
package net.yan100.compose.core.regex

import net.yan100.compose.core.consts.Regexes
import java.util.regex.Pattern
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RegexTest {
  @Test
  fun `test icCard Match`() {
    val pattern = Pattern.compile(Regexes.CHINA_ID_CARD)
    assertTrue { pattern.matcher("430404197210280012").matches() }
    // 基本匹配
    assertTrue { pattern.matcher("43040419721028001X").matches() }
    assertTrue { pattern.matcher("43040419721028001x").matches() }
    // 位数
    assertFalse { pattern.matcher("43040419721028001x1").matches() }
    assertFalse { pattern.matcher("43040419721028001").matches() }
    // 地理位置不对
    assertFalse { pattern.matcher("01040419721028001").matches() }
    assertFalse { pattern.matcher("10040419721028001").matches() }
  }

  @Test
  fun `test match ant uri`() {
    val pattern = Regexes.ANT_URI.toRegex()
    assertTrue {
      arrayOf("/", "/a", "/a/b", "/.", "/.php", "/aaa.", "/a.b.", "/a.b.c", "/a/*/*", "/a/b/*/*", "/1/2")
        .map(pattern::matches).reduce(Boolean::and)
    }

    assertFalse {
      arrayOf(
        "//a", "//", "/:", "/:/:", "/%ad",
        "", "/**", "/..", " ", "/ ", "./", "../", " / ", "/ /\n",
        "/\n", "/\r", "/.*", "/..", "..", "/../..", "/1/2/**", "/1/2/**/*/a",
        "/1/2/*a", "/1/2/**", "/1/2/**/", "/1/2/"
      )
        .map(pattern::matches).reduce(Boolean::or)
    }
  }
}
