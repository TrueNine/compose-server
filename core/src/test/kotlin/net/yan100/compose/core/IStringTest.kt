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

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IStringTest {
  @Test
  fun `has text`() {
    assertTrue { IString.hasText("abc") }
    assertFalse { IString.hasText("") }
    assertFalse { IString.hasText(" ") }
  }


  @Test
  fun `non text`() {
    listOf("", " ", "\n", "\r", "\t", "\r\n").forEach {
      assertTrue { IString.nonText(it) }
    }
    assertFalse { IString.nonText("a") }
  }

  @Test
  fun `inline text`() {
    val a = IString.inLine("1\n")
    assertFalse { a.contains("\n") }
  }



  @Test
  fun `omit text`() {
    val b = IString.omit("abc", 2)
    assertFalse { b.contains("c") }
  }
}
