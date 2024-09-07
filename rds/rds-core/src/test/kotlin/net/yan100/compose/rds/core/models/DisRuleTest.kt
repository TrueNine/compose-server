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
package net.yan100.compose.rds.core.models

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.yan100.compose.rds.core.typing.cert.DisTyping
import kotlin.test.*

class DisRuleTest {

  @Test
  fun `test create`() {
    val emptyArray = DisRule(byteArrayOf())
    assertTrue { emptyArray.meta.size == 28 }
    emptyArray.meta.forEach { assertTrue { it == 0.toByte() || it == 1.toByte() } }
  }

  @Test
  fun `test match`() {
    val emptyArray = DisRule(byteArrayOf(1, 1, 1, 1, 1, 1, 1, 1))
    assertTrue { emptyArray.match(DisTyping.EYE, 2) }
    assertFalse { emptyArray.match(DisTyping.MOUTH, 1) }
    assertTrue { emptyArray.match(DisTyping.EAR, 4) }
    assertFails { emptyArray.match(8, 4) }
    assertFails { emptyArray.match(0, 4) }
    assertFails { emptyArray.match(-1, 4) }
    assertFails { emptyArray.match(1, 5) }
    assertFails { emptyArray.match(1, 0) }
  }

  @Test
  fun `test serialize`() {
    val emptyArray = DisRule(byteArrayOf(1, 1, 1, 1, 1, 1, 1, 1))
    val map = ObjectMapper()

    val json = map.writeValueAsString(emptyArray)
    assertEquals("\"AQEBAQEBAQEAAAAAAAAAAAAAAAAAAAAAAAAAAA==\"", json)
    val readJson = map.readValue<ByteArray>(json)
    assertContentEquals(readJson, emptyArray.meta)
    println(readJson.joinToString())
  }
}
