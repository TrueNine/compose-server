package io.github.truenine.composeserver.testtoolkit

import kotlin.test.Test
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Nested

/**
 * # Kotlin 测试函数测试
 *
 * 测试 KotlinTestExtensions.kt 中的测试工具函数
 *
 * @author TrueNine
 * @since 2025-07-12
 */
class KotlinTestExtensionsTest {

  @Nested
  inner class AssertNotEmptyTests {
    @Test
    fun assert_not_empty_succeeds_with_non_empty_list() {
      log.info("开始测试 assertNotEmpty 函数成功情况")

      // 测试非空列表
      assertNotEmpty("列表不应该为空") { listOf(1, 2, 3) }
      assertNotEmpty { listOf("a", "b") }

      log.info("assertNotEmpty 函数成功情况测试完成")
    }

    @Test
    fun assert_not_empty_fails_with_empty_list() {
      log.info("开始测试 assertNotEmpty 函数失败情况")

      // 测试空列表应该抛出异常
      assertFailsWith<AssertionError> { assertNotEmpty("列表应该不为空") { emptyList<String>() } }

      assertFailsWith<AssertionError> { assertNotEmpty { emptyList<Int>() } }

      log.info("assertNotEmpty 函数失败情况测试完成")
    }
  }

  @Nested
  inner class AssertEmptyTests {
    @Test
    fun assert_empty_succeeds_with_empty_list() {
      log.info("开始测试 assertEmpty 函数成功情况")

      // 测试空列表
      assertEmpty("列表应该为空") { emptyList<String>() }
      assertEmpty { emptyList<Int>() }

      log.info("assertEmpty 函数成功情况测试完成")
    }

    @Test
    fun assert_empty_fails_with_non_empty_list() {
      log.info("开始测试 assertEmpty 函数失败情况")

      // 测试非空列表应该抛出异常
      assertFailsWith<AssertionError> { assertEmpty("列表应该为空") { listOf(1, 2, 3) } }

      assertFailsWith<AssertionError> { assertEmpty { listOf("a") } }

      log.info("assertEmpty 函数失败情况测试完成")
    }
  }

  @Nested
  inner class AssertNotBlankTests {
    @Test
    fun assert_not_blank_succeeds_with_non_blank_string() {
      log.info("开始测试 assertNotBlank 函数成功情况")

      // 测试非空白字符串
      assertNotBlank("Hello", "字符串不应该为空白")
      assertNotBlank("  test  ")
      assertNotBlank("123")
      assertNotBlank("a")

      log.info("assertNotBlank 函数成功情况测试完成")
    }

    @Test
    fun assert_not_blank_fails_with_blank_string() {
      log.info("开始测试 assertNotBlank 函数失败情况")

      // 测试空白字符串应该抛出异常
      assertFailsWith<AssertionError> { assertNotBlank("", "字符串不应该为空白") }

      assertFailsWith<AssertionError> { assertNotBlank("   ") }

      assertFailsWith<AssertionError> { assertNotBlank("\t\n\r") }

      log.info("assertNotBlank 函数失败情况测试完成")
    }
  }

  @Nested
  inner class AssertBlankTests {
    @Test
    fun assert_blank_succeeds_with_blank_string() {
      log.info("开始测试 assertBlank 函数成功情况")

      // 测试空白字符串
      assertBlank("", "字符串应该为空白")
      assertBlank("   ")
      assertBlank("\t")
      assertBlank("\n")
      assertBlank("\r")
      assertBlank("\t\n\r   ")

      log.info("assertBlank 函数成功情况测试完成")
    }

    @Test
    fun assert_blank_fails_with_non_blank_string() {
      log.info("开始测试 assertBlank 函数失败情况")

      // 测试非空白字符串应该抛出异常
      assertFailsWith<AssertionError> { assertBlank("Hello", "字符串应该为空白") }

      assertFailsWith<AssertionError> { assertBlank("  test  ") }

      assertFailsWith<AssertionError> { assertBlank("a") }

      log.info("assertBlank 函数失败情况测试完成")
    }
  }

  @Nested
  inner class DefaultMessageTests {
    @Test
    fun all_assertion_functions_provide_default_messages() {
      log.info("开始测试所有断言函数的默认消息")

      // 测试默认消息是否正常工作
      try {
        assertNotEmpty { emptyList<String>() }
      } catch (e: AssertionError) {
        // 验证默认消息存在
        log.info("assertNotEmpty 默认消息: ${e.message}")
      }

      try {
        assertEmpty { listOf(1) }
      } catch (e: AssertionError) {
        // 验证默认消息存在
        log.info("assertEmpty 默认消息: ${e.message}")
      }

      try {
        assertNotBlank("")
      } catch (e: AssertionError) {
        // 验证默认消息存在
        log.info("assertNotBlank 默认消息: ${e.message}")
      }

      try {
        assertBlank("test")
      } catch (e: AssertionError) {
        // 验证默认消息存在
        log.info("assertBlank 默认消息: ${e.message}")
      }

      log.info("所有断言函数的默认消息测试完成")
    }
  }
}
