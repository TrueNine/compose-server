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
package net.yan100.compose.rds.core.util

import kotlin.test.assertEquals
import net.yan100.compose.rds.core.util.PagedWrapper.DEFAULT_MAX
import org.junit.jupiter.api.Test

class PagedWrapperTest {

  @Test
  fun testWrapBy() {
    val a = generateSequence(0) { it + 1 }.take(1000).map { it.toString() }.toList()
    val b =
      PagedWrapper.warpBy(DEFAULT_MAX) { a.asSequence() }
        .also {
          assertEquals(it.dataList.size, 42)
          assertEquals(it.size, 42)
          assertEquals(it.total, 1000)
          assertEquals(it.pageSize, 23)
          assertEquals(it.offset, 0)
        }
    println(b)
  }
}
