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
package net.yan100.compose.depend.jvalid

import kotlin.test.Test
import kotlin.test.assertNotNull
import net.yan100.compose.depend.jvalid.test.AtLeast
import net.yan100.compose.depend.jvalid.test.ValidTestFns
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [JValidEntrance::class])
class ValidTest {
  @Autowired lateinit var testHandle: ValidTestFns

  @Test
  fun `test valid`() {
    assertNotNull(testHandle)
    testHandle.a(AtLeast())
  }
}
