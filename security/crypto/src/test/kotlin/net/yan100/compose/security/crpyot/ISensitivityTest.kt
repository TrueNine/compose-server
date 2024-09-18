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
package net.yan100.compose.security.crpyot

import net.yan100.compose.core.domain.ISensitivity
import net.yan100.compose.testtookit.log
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ISensitivityTest {
  abstract class Ab : ISensitivity {
    var ab: String? = null

    override fun changeWithSensitiveData() {
      ab?.also { ab = "ab sensitive" }
    }
  }

  class B : Ab() {
    override fun changeWithSensitiveData() {
      ab?.also { ab = "b sensitive" }
    }
  }

  @Test
  fun `change sensitive`() {
    val b = B()
    b.ab = "123"
    val old = b.ab
    b.changeWithSensitiveData()
    val new = b.ab
    log.info(b.ab)

    assertNotEquals(old, new)
    assertEquals("b sensitive", new, "确保函数调用必须处于最底层")
    assertNotEquals("ab sensitive", new, "确保函数调用必须处于最底层")
  }
}
