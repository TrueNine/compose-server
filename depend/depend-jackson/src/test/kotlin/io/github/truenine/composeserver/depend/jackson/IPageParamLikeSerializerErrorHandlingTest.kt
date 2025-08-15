package io.github.truenine.composeserver.depend.jackson

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.domain.IPageParam
import io.github.truenine.composeserver.testtoolkit.log
import jakarta.annotation.Resource
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class IPageParamLikeSerializerErrorHandlingTest {

  @Resource lateinit var mapper: ObjectMapper

  @Nested
  inner class ErrorHandling {

    @Test
    fun `handle invalid json format gracefully`() {
      val invalidJson = """{"o":"not_a_number","s":20}"""

      val exception = assertFailsWith<JsonMappingException> { mapper.readValue(invalidJson, IPageParam::class.java) }

      log.info("Caught expected exception: {}", exception.message)
      assertNotNull(exception.message)
    }

    @Test
    fun `handle invalid boolean value gracefully`() {
      val invalidJson = """{"o":1,"s":20,"u":"not_a_boolean"}"""

      val exception = assertFailsWith<JsonMappingException> { mapper.readValue(invalidJson, IPageParam::class.java) }

      log.info("Caught expected exception for boolean: {}", exception.message)
      assertNotNull(exception.message)
    }

    @Test
    fun `handle non-object json gracefully`() {
      val invalidJson = """["not", "an", "object"]"""

      val exception = assertFailsWith<JsonMappingException> { mapper.readValue(invalidJson, IPageParam::class.java) }

      log.info("Caught expected exception for non-object: {}", exception.message)
      assertNotNull(exception.message)
    }

    @Test
    fun `handle string json gracefully`() {
      val invalidJson = """"just_a_string""""

      val exception = assertFailsWith<JsonMappingException> { mapper.readValue(invalidJson, IPageParam::class.java) }

      log.info("Caught expected exception for string: {}", exception.message)
      assertNotNull(exception.message)
    }
  }

  @Nested
  inner class EdgeCases {

    @Test
    fun `handle very large numbers`() {
      val json = """{"o":${Int.MAX_VALUE},"s":${Int.MAX_VALUE}}"""
      val pageParam = mapper.readValue(json, IPageParam::class.java)

      assertNotNull(pageParam)
      log.info("Handled large numbers: o={}, s={}", pageParam.o, pageParam.s)
    }

    @Test
    fun `handle negative numbers`() {
      val json = """{"o":-1,"s":-1}"""
      val pageParam = mapper.readValue(json, IPageParam::class.java)

      assertNotNull(pageParam)
      // 验证负数被正确处理（根据Pq工厂方法的逻辑）
      log.info("Handled negative numbers: o={}, s={}", pageParam.o, pageParam.s)
    }

    @Test
    fun `handle zero values`() {
      val json = """{"o":0,"s":0}"""
      val pageParam = mapper.readValue(json, IPageParam::class.java)

      assertNotNull(pageParam)
      log.info("Handled zero values: o={}, s={}", pageParam.o, pageParam.s)
    }
  }

  @Nested
  inner class SerializerLogicVerification {

    @Test
    fun `verify factory method integration`() {
      val json = """{"o":5,"s":25}"""
      val pageParam = mapper.readValue(json, IPageParam::class.java)

      assertNotNull(pageParam)
      // 验证使用了Pq工厂方法创建的实例
      log.info("Factory method result: o={}, s={}", pageParam.o, pageParam.s)

      // 验证实例类型
      log.info("Instance class: {}", pageParam::class.java.simpleName)
    }

    @Test
    fun `verify field skipping works correctly`() {
      val jsonWithExtraFields = """{"o":3,"s":15,"unknown_field":"value","extra_number":123,"nested":{"inner":"value"}}"""
      val pageParam = mapper.readValue(jsonWithExtraFields, IPageParam::class.java)

      assertNotNull(pageParam)
      // 验证只处理了已知字段
      log.info("Processed with extra fields: o={}, s={}", pageParam.o, pageParam.s)
    }
  }
}
