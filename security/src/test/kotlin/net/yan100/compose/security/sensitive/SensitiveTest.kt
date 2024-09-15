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
package net.yan100.compose.security.sensitive

import jakarta.annotation.Resource
import net.yan100.compose.security.autoconfig.SensitiveResultResponseBodyAdvice
import net.yan100.compose.security.controller.SensitiveController
import net.yan100.compose.testtookit.SpringServletTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringServletTest
class SensitiveTest {
  lateinit var mockMvc: MockMvc @Resource set
  lateinit var controller: SensitiveController @Resource set
  lateinit var sensitive: SensitiveResultResponseBodyAdvice @Resource set

  @BeforeTest
  fun setup() {
    assertNotNull(mockMvc)
    assertNotNull(controller)
    assertNotNull(sensitive)
    mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(sensitive).build()
  }

  // TODO 补齐测试用例
  @Test
  fun `test query param split`() {
    mockMvc.get("/test/sensitive/get").andExpect {
      status { isOk() }
      content { contentType(MediaType.APPLICATION_JSON) }
    }
  }
}
