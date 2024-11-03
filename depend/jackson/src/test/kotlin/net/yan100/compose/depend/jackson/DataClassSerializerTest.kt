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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.annotation.Resource
import net.yan100.compose.core.datetime
import net.yan100.compose.depend.jackson.autoconfig.JacksonAutoConfiguration
import net.yan100.compose.testtookit.annotations.SpringServletTest
import net.yan100.compose.testtookit.log
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import kotlin.test.Test
import kotlin.test.assertTrue

data class A(val a: String, val b: String)

class B {
  lateinit var s: String
}

@SpringServletTest
class DataClassSerializerTest {
  @Resource
  lateinit var mockMvc: MockMvc

  @Resource
  lateinit var mapper: ObjectMapper

  @Test
  fun `test web request`() {
    val b = mockMvc.post("/v1/a") {
      content = A("a", "b")
      contentType = MediaType.APPLICATION_JSON
    }.andDo { this.print() }
  }

  @Test
  fun `test serialize class with late init var`() {
    val b = B()
    b.s = "s"
    val json = mapper.writeValueAsString(b)
    val obj = mapper.readValue<B>(json)
    log.info("obj b: {}", obj)
  }

  @Test
  fun `test serialize data class`() {
    val a = A("a", "b")
    val json = mapper.writeValueAsString(a)
    log.info("a json: {}", json)
    val obj = mapper.readValue<A>(json)
    log.info("deserialization obj: {}", obj)
  }

  @Test
  fun `test serialize interface internal data class`() {
    val a = InterFace.InternalClass("a", "b", "c")
    val json = mapper.writeValueAsString(a)
    log.info("json: {}", json)
    val obj = mapper.readValue<InterFace.InternalClass>(json)
    log.info("obj: {}", obj)
  }

  @Resource
  @Qualifier(JacksonAutoConfiguration.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME)
  lateinit var map: ObjectMapper

  @Test
  fun `test serialize interface internal data class be typed`() {
    val a = InterFace.InternalClass("a", "b", "c")
    val json = map.writeValueAsString(a)
    assertTrue { json.contains("net.yan100.compose.depend.jackson.InterFace\$InternalClass") }
    log.info("json a: {}", json)
    val obj = map.readValue(json, InterFace.InternalClass::class.java)
    log.info("obj a: {}", obj)
    log.info("obj class: {}", obj::class)
  }

  @Test
  fun `test ignore json serialize datetime`() {
    val dt = datetime.now()
    val a = InterFace.InternalClass("a", "b", "c", dt)
    val json = map.writeValueAsString(a)
    log.info("json m: {}", json)

    val obj = map.readValue(json, InterFace.InternalClass::class.java)
    log.info("obj c: {}", obj)
    log.info("obj c class: {}", obj::class)
  }
}


interface InterFace {
  data class InternalClass(
    val a: String, val b: String, @JsonIgnore val c: String?, @JsonIgnore val d: datetime? = null
  )
}
