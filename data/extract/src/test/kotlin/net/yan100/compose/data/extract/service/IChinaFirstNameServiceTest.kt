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
package net.yan100.compose.data.extract.service

import kotlin.test.Test
import kotlin.test.assertEquals

class IChinaFirstNameServiceTest {
  @Test
  fun `test not repeat`() {
    val a = IChinaFirstNameService.CHINA_FIRST_NAMES.groupBy { it }.filter { it.value.size > 1 }
    assertEquals(a.size, 0, message = "re ${a.keys}")
  }
}
