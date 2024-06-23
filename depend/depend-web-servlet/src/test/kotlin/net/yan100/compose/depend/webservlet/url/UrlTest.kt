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
package net.yan100.compose.depend.webservlet.url

import kotlin.test.Test
import net.yan100.compose.depend.webservlet.DependServletEntrance
import net.yan100.compose.depend.webservlet.controller.UrlTestController
import net.yan100.compose.testtookit.SpringServletTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringServletTest
@SpringBootTest(classes = [DependServletEntrance::class, UrlTestController::class])
class UrlTest {

  @Autowired lateinit var mockMvc: MockMvc

  @Test fun `test query param split`() {}
}
