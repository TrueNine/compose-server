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
package net.yan100.compose.depend.servlet.url

import jakarta.annotation.Resource
import net.yan100.compose.depend.servlet.DependServletEntrance
import net.yan100.compose.depend.servlet.controller.UrlTestController
import net.yan100.compose.testtookit.SpringServletTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import kotlin.test.Test

@SpringServletTest
@SpringBootTest(classes = [DependServletEntrance::class, UrlTestController::class])
class UrlTest {
  lateinit var mockMvc: MockMvc @Resource set

  @Test
  fun `test query param split`() {
  }
}