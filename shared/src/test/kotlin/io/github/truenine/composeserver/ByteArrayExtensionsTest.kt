package io.github.truenine.composeserver

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * # 字节数组扩展函数测试
 *
 * 测试 ByteArrayExtensions.kt 中定义的字节数组相关扩展函数
 */
class ByteArrayExtensionsTest {

  @Test
  fun `测试 utf8String 扩展属性 - 字节数组转UTF8字符串`() {
    val testString = "Hello, 世界! 🌍"
    val byteArray = testString.toByteArray(Charsets.UTF_8)

    val result = byteArray.utf8String

    log.info("原始字符串: {}", testString)
    log.info("字节数组长度: {}", byteArray.size)
    log.info("转换后字符串: {}", result)

    assertEquals(testString, result, "字节数组应该正确转换为UTF-8字符串")
  }

  @Test
  fun `测试 utf8String 扩展属性 - 空字节数组`() {
    val emptyByteArray = ByteArray(0)

    val result = emptyByteArray.utf8String

    log.info("空字节数组转换结果: '{}'", result)

    assertEquals("", result, "空字节数组应该转换为空字符串")
  }

  @Test
  fun `测试 utf8String 扩展属性 - 中文字符`() {
    val chineseText = "你好，世界！这是一个测试。"
    val byteArray = chineseText.toByteArray(Charsets.UTF_8)

    val result = byteArray.utf8String

    log.info("中文原文: {}", chineseText)
    log.info("UTF-8字节数组长度: {}", byteArray.size)
    log.info("转换后中文: {}", result)

    assertEquals(chineseText, result, "中文字符应该正确转换")
  }

  @Test
  fun `测试 utf8String 扩展属性 - 特殊字符和表情符号`() {
    val specialText = "Special chars: @#$%^&*()_+-=[]{}|;':\",./<>? 🎉🚀💻🌟"
    val byteArray = specialText.toByteArray(Charsets.UTF_8)

    val result = byteArray.utf8String

    log.info("特殊字符原文: {}", specialText)
    log.info("UTF-8字节数组长度: {}", byteArray.size)
    log.info("转换后文本: {}", result)

    assertEquals(specialText, result, "特殊字符和表情符号应该正确转换")
  }

  @Test
  fun `测试 utf8String 扩展属性 - 多行文本`() {
    val multilineText =
      """
        第一行文本
        第二行文本
        第三行包含特殊字符: !@#$%
        第四行包含表情: 😊🎈
      """
        .trimIndent()

    val byteArray = multilineText.toByteArray(Charsets.UTF_8)

    val result = byteArray.utf8String

    log.info("多行文本原文:\n{}", multilineText)
    log.info("UTF-8字节数组长度: {}", byteArray.size)
    log.info("转换后文本:\n{}", result)

    assertEquals(multilineText, result, "多行文本应该正确转换")
  }

  @Test
  fun `测试 utf8String 扩展属性 - 数字和英文混合`() {
    val mixedText = "ABC123abc测试Test456混合MixedContent789"
    val byteArray = mixedText.toByteArray(Charsets.UTF_8)

    val result = byteArray.utf8String

    log.info("混合文本原文: {}", mixedText)
    log.info("UTF-8字节数组长度: {}", byteArray.size)
    log.info("转换后文本: {}", result)

    assertEquals(mixedText, result, "数字和英文混合文本应该正确转换")
  }

  @Test
  fun `测试 utf8String 扩展属性 - 与其他编码对比`() {
    val testText = "编码测试 Encoding Test"

    val utf8Bytes = testText.toByteArray(Charsets.UTF_8)
    val iso8859Bytes = testText.toByteArray(Charsets.ISO_8859_1)

    val utf8Result = utf8Bytes.utf8String
    // 注意：ISO-8859-1字节用UTF-8解码可能会产生不同的结果
    val iso8859AsUtf8Result = iso8859Bytes.utf8String

    log.info("原始文本: {}", testText)
    log.info("UTF-8编码后再解码: {}", utf8Result)
    log.info("ISO-8859-1编码后用UTF-8解码: {}", iso8859AsUtf8Result)
    log.info("UTF-8字节数组长度: {}", utf8Bytes.size)
    log.info("ISO-8859-1字节数组长度: {}", iso8859Bytes.size)

    assertEquals(testText, utf8Result, "UTF-8编码解码应该保持一致")
    // ISO-8859-1编码的中文字符用UTF-8解码通常会出现乱码，这是预期的
  }

  @Test
  fun `测试 utf8String 扩展属性 - 长文本性能`() {
    val longText = "这是一个很长的文本，用于测试性能。".repeat(1000)
    val byteArray = longText.toByteArray(Charsets.UTF_8)

    val startTime = System.currentTimeMillis()
    val result = byteArray.utf8String
    val endTime = System.currentTimeMillis()

    log.info("长文本长度: {} 字符", longText.length)
    log.info("字节数组长度: {} 字节", byteArray.size)
    log.info("转换耗时: {} 毫秒", endTime - startTime)
    log.info("转换结果长度: {} 字符", result.length)

    assertEquals(longText, result, "长文本应该正确转换")
    assertEquals(longText.length, result.length, "转换后长度应该一致")
  }
}
