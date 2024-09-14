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
package net.yan100.compose.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Resource
import net.yan100.compose.depend.jackson.autoconfig.JacksonAutoConfiguration
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@SpringBootTest
class ByteArrayDeserializerTest {
  lateinit var mapper: ObjectMapper @Resource set

  lateinit var map: ObjectMapper
    @Resource(name = JacksonAutoConfiguration.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME)
    set

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
    assertEquals("{\"a\":\"a\",\"b\":\"AQABAAEA\"}", json)
    assertEquals(ba.a, ab.a)
    assertContentEquals(ba.b, ab.b)
  }

  @Test
  fun `test deserialize byte array`() {
    val ab =
      S().apply {
        a = "a"
        b = byteArrayOf(1, 0, 1, 0, 1, 0)
      }

    val json = map.writeValueAsString(ab)
    println(json)

    val ba = map.readValue(json, S::class.java)
    println(ba)

    assertEquals("{\"net.yan100.compose.depend.jackson.ByteArrayDeserializerTest\$S\":{\"a\":\"a\",\"b\":{\"[B\":\"AQABAAEA\"}}}", json)
    assertEquals(ba.a, ab.a)
    assertContentEquals(ba.b, ab.b)
  }
}
