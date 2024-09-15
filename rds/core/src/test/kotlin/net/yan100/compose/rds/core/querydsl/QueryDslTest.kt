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
package net.yan100.compose.rds.core.querydsl

import com.querydsl.core.BooleanBuilder
import net.yan100.compose.rds.core.entities.QIEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class QueryDslTest {
  @Test
  fun `test a`() {
  }

  /**  */
  @Test
  fun `test query dsl clone not exception`() {
    val q = QIEntity.iEntity

    val b = BooleanBuilder()
    val c = b.clone()
    println(b.hashCode())
    println(c.hashCode())
    assertEquals(b, c)

    b.and(q.ldf.eq(true).or(q.isNull))

    val bv = b.value
    val cv = c.value
    assertNull(cv)
    assertEquals(cv.toString(), "null")

    println(bv)
    println(cv)
  }
}
