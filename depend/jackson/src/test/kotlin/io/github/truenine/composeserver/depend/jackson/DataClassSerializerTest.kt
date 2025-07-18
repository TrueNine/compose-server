package io.github.truenine.composeserver.depend.jackson

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.truenine.composeserver.datetime
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import io.github.truenine.composeserver.testtoolkit.log
import jakarta.annotation.Resource
import kotlin.test.Test
import kotlin.test.assertTrue
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest

data class A(val a: String, val b: String)

class B {
  lateinit var s: String
}

@SpringBootTest
class DataClassSerializerTest {

  @Resource lateinit var mapper: ObjectMapper

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

  @Resource @Qualifier(JacksonAutoConfiguration.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME) lateinit var map: ObjectMapper

  @Test
  fun `test serialize interface internal data class be typed`() {
    val a = InterFace.InternalClass("a", "b", "c")
    val json = map.writeValueAsString(a)
    assertTrue { json.contains("io.github.truenine.composeserver.depend.jackson.InterFace\$InternalClass") }
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

  @Test
  fun `test serialize null values`() {
    val a = InterFace.InternalClass("a", "b", null, null)
    val json = mapper.writeValueAsString(a)
    log.info("null values json: {}", json)
    val obj = mapper.readValue<InterFace.InternalClass>(json)
    log.info("null values obj: {}", obj)
  }

  @Test
  fun `test serialize empty strings`() {
    val a = A("", "")
    val json = mapper.writeValueAsString(a)
    log.info("empty strings json: {}", json)
    val obj = mapper.readValue<A>(json)
    log.info("empty strings obj: {}", obj)
  }

  @Test
  fun `test serialize with special characters`() {
    val a = A("hello\nworld", "test\"quote'apostrophe")
    val json = mapper.writeValueAsString(a)
    log.info("special chars json: {}", json)
    val obj = mapper.readValue<A>(json)
    log.info("special chars obj: {}", obj)
  }

  @Test
  fun `test serialize unicode characters`() {
    val a = A("测试中文", "🎉emoji")
    val json = mapper.writeValueAsString(a)
    log.info("unicode json: {}", json)
    val obj = mapper.readValue<A>(json)
    log.info("unicode obj: {}", obj)
  }

  @Test
  fun `test serialize large strings`() {
    val largeString = "x".repeat(10000)
    val a = A(largeString, "normal")
    val json = mapper.writeValueAsString(a)
    log.info("large string json length: {}", json.length)
    val obj = mapper.readValue<A>(json)
    log.info("large string obj a length: {}", obj.a.length)
  }

  @Test
  fun `test serialize class with uninitialized lateinit var should fail`() {
    val b = B()
    try {
      val json = mapper.writeValueAsString(b)
      log.error("Unexpected success: {}", json)
    } catch (e: Exception) {
      log.info("Expected exception for uninitialized lateinit: {}", e.javaClass.simpleName)
    }
  }

  @Test
  fun `test deserialize malformed json should fail`() {
    try {
      val obj = mapper.readValue<A>("{\"a\":\"test\",\"b\":}")
      log.error("Unexpected success: {}", obj)
    } catch (e: Exception) {
      log.info("Expected exception for malformed JSON: {}", e.javaClass.simpleName)
    }
  }

  @Test
  fun `test deserialize missing required field should fail`() {
    try {
      val obj = mapper.readValue<A>("{\"a\":\"test\"}")
      log.error("Unexpected success: {}", obj)
    } catch (e: Exception) {
      log.info("Expected exception for missing field: {}", e.javaClass.simpleName)
    }
  }
}

interface InterFace {
  data class InternalClass(val a: String, val b: String, @JsonIgnore val c: String?, @JsonIgnore val d: datetime? = null)
}
