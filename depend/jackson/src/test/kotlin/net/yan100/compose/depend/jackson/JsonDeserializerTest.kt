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
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import jakarta.annotation.Resource
import net.yan100.compose.core.toDate
import net.yan100.compose.core.toLocalDatetime
import net.yan100.compose.core.toLong
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
class JsonDeserializerTest {
  @JsonSerialize
  lateinit var mapper: ObjectMapper @Resource set

  @Test
  fun serializeLongToString() {
    val longJsonString = mapper.writeValueAsString(1234567890123451L)
    assertEquals("1234567890123451", longJsonString, "正常情况下，未注册 string 则必须转换为 long 数字格式")
  }

  @Test
  fun serializeLocalDateTime() {
    val localDatetime = Date().toLocalDatetime()
    val json = mapper.writeValueAsString(localDatetime)
    log.info(json)
    val local = mapper.readValue(json, LocalDateTime::class.java)
    log.info("local: {}", local.toDate().toLong())
    assertEquals(local.toDate().toLong(), localDatetime.toDate().toLong())
  }
}
