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
package net.yan100.compose.core.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@SpringBootTest
class ByteArrayDeserializerTest {
  @Autowired lateinit var mapper: ObjectMapper

  class S {
    var a: String? = null
    var b: ByteArray? = null
  }

  @Test
  fun `test serialize byte array`() {
    val ab =
      S().apply {
        a = "a"
        b = byteArrayOf(1, 0, 1, 0, 1, 0)
      }

    val json = mapper.writeValueAsString(ab)
    val ba = mapper.readValue(json, S::class.java)
    println(ba)
    println(json)
    assertEquals("{\"a\":\"a\",\"b\":[1,0,1,0,1,0]}", json)
    assertEquals(ba.a, ab.a)
    assertContentEquals(ba.b, ab.b)
  }
}
