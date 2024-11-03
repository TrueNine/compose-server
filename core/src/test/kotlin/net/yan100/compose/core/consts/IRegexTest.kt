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

import java.util.regex.Pattern
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IRegexTest {
  @Test
  fun `match china ad code`() {
    val pattern = Pattern.compile(IRegexes.CHINA_AD_CODE)
    assertTrue { pattern.matcher("43").matches() }
    assertTrue { pattern.matcher("4304").matches() }
    assertTrue { pattern.matcher("430404").matches() }
    assertTrue { pattern.matcher("430404100").matches() }
    assertTrue { pattern.matcher("430404100101").matches() }


    assertTrue { pattern.matcher("430404").matches() }

    assertFalse { pattern.matcher("00").matches() }
    assertFalse { pattern.matcher("01").matches() }
    assertFalse { pattern.matcher("4304041").matches() }
  }


  @Test
  fun `iccard match`() {
    val pattern = Pattern.compile(IRegexes.CHINA_ID_CARD)
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
    val pattern = IRegexes.ANT_URI.toRegex()
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

  @Test
  fun `rbac name match`() {
    val reg = Pattern.compile(IRegexes.RBAC_NAME)
    assertTrue { reg.matcher("abc").matches() }
    assertTrue { reg.matcher("user_read").matches() }
    assertTrue { reg.matcher("user:read").matches() }
    assertTrue { reg.matcher("a").matches() }
    // 失败
    assertFalse { reg.matcher("_").matches() }
    assertFalse { reg.matcher("_a").matches() }
    assertFalse { reg.matcher(":").matches() }
    assertFalse { reg.matcher(":a").matches() }
    assertFalse { reg.matcher("-").matches() }
    assertFalse { reg.matcher("-a").matches() }
    assertFalse { reg.matcher("9").matches() }
    assertFalse { reg.matcher("2a").matches() }
    assertFalse { reg.matcher("1:").matches() }
    assertFalse { reg.matcher("\n").matches() }
  }
}
