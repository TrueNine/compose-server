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
import net.yan100.compose.core.ISO4217Typing
import net.yan100.compose.testtookit.log
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

open class AB {
  var typ: ISO4217Typing? = null
}

@SpringBootTest
class AnyTypingDeserializerTest {
  @Resource
  lateinit var mapper: ObjectMapper

  @Test
  fun `test deserializer`() {
    val d = ISO4217Typing.CNY
    val dd = AB().apply { typ = d }
    val json = mapper.writeValueAsString(dd)
    val cc = mapper.readValue(json, AB::class.java)
    val ff = mapper.readValue("{\"typ\":1}", AB::class.java)
    val ee = mapper.readValue("{\"typ\":\"1\"}", AB::class.java)

    log.info("json: {}", json)
  }
}
