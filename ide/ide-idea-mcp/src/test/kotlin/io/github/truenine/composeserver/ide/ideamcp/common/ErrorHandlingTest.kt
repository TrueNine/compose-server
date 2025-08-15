package io.github.truenine.composeserver.ide.ideamcp.common

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/** 错误处理测试 测试通用错误处理功能 */
class ErrorHandlingTest : BasePlatformTestCase() {

  fun testErrorDetailsCreation() {
    // 测试错误详情创建
    val errorDetails = ErrorDetails(type = "TestError", message = "测试错误消息", suggestions = listOf("建议1", "建议2"))

    assertNotNull(errorDetails)
    assertEquals("TestError", errorDetails.type)
    assertEquals("测试错误消息", errorDetails.message)
    assertEquals(2, errorDetails.suggestions.size)
    assertTrue(errorDetails.suggestions.contains("建议1"))
    assertTrue(errorDetails.suggestions.contains("建议2"))
  }

  fun testErrorDetailsWithEmptySuggestions() {
    // 测试没有建议的错误详情
    val errorDetails = ErrorDetails(type = "SimpleError", message = "简单错误")

    assertNotNull(errorDetails)
    assertEquals("SimpleError", errorDetails.type)
    assertEquals("简单错误", errorDetails.message)
    assertTrue(errorDetails.suggestions.isEmpty())
  }

  fun testErrorDetailsWithLongMessage() {
    // 测试长错误消息
    val longMessage = "这是一个非常长的错误消息，用于测试错误详情类是否能够正确处理长文本内容。这个消息包含了足够多的字符来验证长文本处理功能的正确性和稳定性。"
    val errorDetails = ErrorDetails(type = "LongMessageError", message = longMessage)

    assertNotNull(errorDetails)
    assertEquals("LongMessageError", errorDetails.type)
    assertEquals(longMessage, errorDetails.message)
    assertTrue(errorDetails.message.length > 50)
  }

  fun testErrorDetailsWithMultipleSuggestions() {
    // 测试多个建议的错误详情
    val suggestions = listOf("检查网络连接", "验证配置文件", "重启服务", "联系管理员")
    val errorDetails = ErrorDetails(type = "NetworkError", message = "网络连接失败", suggestions = suggestions)

    assertNotNull(errorDetails)
    assertEquals("NetworkError", errorDetails.type)
    assertEquals("网络连接失败", errorDetails.message)
    assertEquals(4, errorDetails.suggestions.size)
    suggestions.forEach { suggestion -> assertTrue(errorDetails.suggestions.contains(suggestion)) }
  }

  fun testErrorDetailsEquality() {
    // 测试错误详情相等性
    val errorDetails1 = ErrorDetails(type = "TestError", message = "测试消息", suggestions = listOf("建议1"))

    val errorDetails2 = ErrorDetails(type = "TestError", message = "测试消息", suggestions = listOf("建议1"))

    assertEquals(errorDetails1, errorDetails2)
    assertEquals(errorDetails1.hashCode(), errorDetails2.hashCode())
  }

  fun testErrorDetailsInequality() {
    // 测试错误详情不相等性
    val errorDetails1 = ErrorDetails(type = "TestError", message = "测试消息1")

    val errorDetails2 = ErrorDetails(type = "TestError", message = "测试消息2")

    assertFalse(errorDetails1 == errorDetails2)
  }

  fun testErrorDetailsToString() {
    // 测试错误详情字符串表示
    val errorDetails = ErrorDetails(type = "TestError", message = "测试消息", suggestions = listOf("建议1", "建议2"))

    val stringRepresentation = errorDetails.toString()
    assertNotNull(stringRepresentation)
    assertTrue(stringRepresentation.contains("TestError"))
    assertTrue(stringRepresentation.contains("测试消息"))
  }
}
