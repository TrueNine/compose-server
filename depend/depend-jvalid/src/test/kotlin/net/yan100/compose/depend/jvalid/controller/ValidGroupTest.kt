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
package net.yan100.compose.depend.jvalid.controller

import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import java.net.URI
import net.yan100.compose.testtookit.SpringServletTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@EntityScan("net.yan100.compose.depend.jvalid")
@SpringServletTest
class ValidGroupTest {
  @Autowired lateinit var mockMvc: MockMvc

  @Autowired lateinit var fac: ValidatorFactory

  @Autowired lateinit var validator: Validator

  @Test
  fun `test jpa save entity`() {
    val ret =
      mockMvc
        .post(URI("/valid-test/post")) {
          param("name", "123")
          param("age", "123")
          param("id", "123")
        }
        .andExpect { status { isOk() } }
        .andReturn()
        .response
        .contentAsString
    println(ret)
  }

  @Test
  fun `test get valid`() {
    println(fac)
    val ret =
      mockMvc
        .get("/valid-test/get") {
          param("name", "123")
          param("age", "123")
          param("id", "123")
        }
        .andExpect { status { isBadRequest() } }
        .andReturn()
        .response
        .contentAsString
    println(ret)
  }
}
