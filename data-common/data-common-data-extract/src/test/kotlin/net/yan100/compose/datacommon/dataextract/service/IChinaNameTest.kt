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
package net.yan100.compose.datacommon.dataextract.service

import kotlin.test.Test
import org.springframework.core.io.ClassPathResource

class IChinaNameTest {
  @Test
  fun `test get all name`() {
    val text = ClassPathResource("names.txt").file.readText().replace("\\((.*?)\\)".toRegex(), ",")
    val a = text.split(",").map { it.trim() }.distinct()
    val b = a.filter { it.length == 1 }.joinToString { "\n\"$it\"" }
    val c = a.filter { it.length == 2 }.joinToString { "\n\"$it\"" }
    println(b)
    println("==================")
    println(c)
  }
}
