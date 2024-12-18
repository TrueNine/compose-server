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
import net.yan100.compose.core.typing.HttpStatusTyping
import net.yan100.compose.core.typing.UserAgents
import net.yan100.compose.testtookit.log
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


@SpringBootTest
class AnyTypingDeserializerTest {
  lateinit var mapper: ObjectMapper @Resource set

  @Test
  fun `test deserializer`() {
    val stringTyping = UserAgents.CHROME_WIN_103
    val intTyping = HttpStatusTyping._403
    val dd = AnyTypingRecord(stringTyping, intTyping)
    val json = mapper.writeValueAsString(dd)
    log.info("json: {}", json)
    assertEquals(
      """{"stringTyping1":"${UserAgents.CHROME_WIN_103.value}","intTyping2":${HttpStatusTyping._403.value}}""",
      json
    )
    val des = mapper.readValue(json, AnyTypingRecord::class.java)
    log.info("des: {}", des)
    assertNotNull(des)
  }
}
