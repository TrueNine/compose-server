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
package net.yan100.compose.core.jackson

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlin.test.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import net.yan100.compose.core.autoconfig.JacksonSerializationAutoConfig
import org.springframework.beans.factory.annotation.Qualifier

data class A(val a: String, val b: String)

class B {
  lateinit var s: String
}

@WebMvcTest
class DataClassSerializerTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @Autowired
  lateinit var mapper: ObjectMapper

  @Test
  fun `test web request`() {
    val b =
      mockMvc
        .post("/v1/a") {
          content = A("a", "b")
          contentType = MediaType.APPLICATION_JSON
        }
        .andDo { this.print() }
  }

  @Test
  fun `test serialize class with late init var`() {
    val b = B()
    b.s = "s"
    val json = mapper.writeValueAsString(b)
    val obj = mapper.readValue<B>(json)
    println(obj)
  }

  @Test
  fun `test serialize data class`() {
    val a = A("a", "b")
    val json = mapper.writeValueAsString(a)
    println(json)
    val obj = mapper.readValue<A>(json)
    println(obj)
  }

  @Test
  fun `test serialize interface internal data class`() {
    val a = InterFace.InternalClass("a", "b", "c")
    val json = mapper.writeValueAsString(a)
    println(json)
    val obj = mapper.readValue<InterFace.InternalClass>(json)
    println(obj)
  }

  @Autowired
  @Qualifier(JacksonSerializationAutoConfig.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME)
  lateinit var map: ObjectMapper

  @Test
  fun `test serialize interface internal data class be typed`() {
    val a = InterFace.InternalClass("a", "b", "c")
    val json = map.writeValueAsString(a)
    println(json)
    val obj = map.readValue(json, Any::class.java)
    println(obj)
    println(obj::class)
  }
}


interface InterFace {
  data class InternalClass(
    val a: String,
    val b: String,
    @JsonIgnore
    val c: String?
  )
}
