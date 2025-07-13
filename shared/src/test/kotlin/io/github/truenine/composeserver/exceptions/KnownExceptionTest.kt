package io.github.truenine.composeserver.exceptions

import io.github.truenine.composeserver.testtoolkit.log
import io.github.truenine.composeserver.typing.HttpStatusTyping
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * # 已知异常类测试
 *
 * 测试 KnownException 类和相关工具函数
 */
@Suppress("DEPRECATION")
class KnownExceptionTest {

  @Test
  fun `测试 KnownException 默认构造函数`() {
    log.info("测试 KnownException 默认构造函数")

    val exception = KnownException()

    assertNull(exception.getMsg(), "默认消息应该为空")
    assertNull(exception.getMeta(), "默认元异常应该为空")
    assertEquals(HttpStatusTyping.UNKNOWN.code, exception.getCode(), "默认错误码应该是 UNKNOWN")

    log.info("默认构造的异常: {}", exception)
  }

  @Test
  fun `测试 KnownException 带消息的构造函数`() {
    log.info("测试 KnownException 带消息的构造函数")

    val message = "测试异常消息"
    val exception = KnownException(message)

    assertEquals(message, exception.getMsg(), "消息应该匹配")
    assertNull(exception.getMeta(), "元异常应该为空")
    assertEquals(HttpStatusTyping.UNKNOWN.code, exception.getCode(), "错误码应该是默认值")

    log.info("带消息的异常: {}", exception)
  }

  @Test
  fun `测试 KnownException 带元异常的构造函数`() {
    log.info("测试 KnownException 带元异常的构造函数")

    val message = "测试异常消息"
    val cause = RuntimeException("原始异常")
    val exception = KnownException(message, cause)

    assertEquals(message, exception.getMsg(), "消息应该匹配")
    assertEquals(cause, exception.getMeta(), "元异常应该匹配")
    assertEquals(cause, exception.cause, "cause 应该匹配")

    log.info("带元异常的异常: {}", exception)
  }

  @Test
  fun `测试 KnownException 完整构造函数`() {
    log.info("测试 KnownException 完整构造函数")

    val message = "测试异常消息"
    val cause = RuntimeException("原始异常")
    val code = 500
    val exception = KnownException(message, cause, code)

    assertEquals(message, exception.getMsg(), "消息应该匹配")
    assertEquals(cause, exception.getMeta(), "元异常应该匹配")
    assertEquals(code, exception.getCode(), "错误码应该匹配")

    log.info("完整构造的异常: {}", exception)
  }

  @Test
  fun `测试 KnownException 的 setter 方法`() {
    log.info("测试 KnownException 的 setter 方法")

    val exception = KnownException()

    val newMessage = "新的异常消息"
    val newCause = IllegalArgumentException("新的原因")
    val newCode = 400

    exception.setMsg(newMessage)
    exception.setMeta(newCause)
    exception.setCode(newCode)

    assertEquals(newMessage, exception.getMsg(), "设置的消息应该匹配")
    assertEquals(newCause, exception.getMeta(), "设置的元异常应该匹配")
    assertEquals(newCode, exception.getCode(), "设置的错误码应该匹配")

    log.info("设置后的异常: {}", exception)
  }

  @Test
  fun `测试 KnownException 的 toString 方法`() {
    log.info("测试 KnownException 的 toString 方法")

    // 测试有 localizedMessage 的情况
    val exception1 = KnownException("测试消息")
    val toString1 = exception1.toString()

    assertNotNull(toString1, "toString 结果不应该为空")
    log.info("有消息的异常 toString: {}", toString1)

    // 测试没有 localizedMessage 但有 msg 的情况
    val exception2 = KnownException()
    exception2.setMsg("自定义消息")
    val toString2 = exception2.toString()

    assertNotNull(toString2, "toString 结果不应该为空")
    log.info("自定义消息的异常 toString: {}", toString2)
  }

  @Test
  fun `测试 requireKnown 函数 - 表达式为真`() {
    log.info("测试 requireKnown 函数 - 表达式为真")

    // 表达式为真时不应该抛出异常
    requireKnown(true)
    requireKnown(1 == 1)
    requireKnown("test".isNotEmpty())

    log.info("表达式为真时 requireKnown 正常执行")
  }

  @Test
  fun `测试 requireKnown 函数 - 表达式为假`() {
    log.info("测试 requireKnown 函数 - 表达式为假")

    // 表达式为假时应该抛出异常
    assertFailsWith<KnownException> { requireKnown(false) }

    assertFailsWith<KnownException> { requireKnown(1 == 2) }

    assertFailsWith<KnownException> { requireKnown("".isNotEmpty()) }

    log.info("表达式为假时 requireKnown 正确抛出异常")
  }

  @Test
  fun `测试 requireKnown 函数 - 带懒加载消息`() {
    log.info("测试 requireKnown 函数 - 带懒加载消息")

    // 表达式为真时不应该抛出异常
    requireKnown(true) { "这个消息不应该被计算" }

    // 表达式为假时应该抛出异常并包含消息
    val exception = assertFailsWith<KnownException> { requireKnown(false) { "自定义错误消息" } }

    assertEquals("自定义错误消息", exception.getMsg(), "异常消息应该匹配")
    log.info("带懒加载消息的异常: {}", exception)
  }

  @Test
  fun `测试 requireKnown 函数 - 带自定义异常`() {
    log.info("测试 requireKnown 函数 - 带自定义异常")

    class CustomKnownException : KnownException()

    // 表达式为真时不应该抛出异常
    requireKnown(true, CustomKnownException()) { "不应该抛出" }

    // 表达式为假时应该抛出自定义异常
    val exception = assertFailsWith<CustomKnownException> { requireKnown(false, CustomKnownException()) { "自定义异常消息" } }

    assertEquals("自定义异常消息", exception.getMsg(), "异常消息应该匹配")
    log.info("自定义异常: {}", exception)
  }

  @Test
  fun `测试 KnownException 的继承性`() {
    log.info("测试 KnownException 的继承性")

    class CustomException(msg: String) : KnownException(msg)

    val customException = CustomException("自定义异常")

    // 验证继承关系
    assert(customException is KnownException) { "应该是 KnownException 的子类" }
    assert(customException is RuntimeException) { "应该是 RuntimeException 的子类" }

    assertEquals("自定义异常", customException.getMsg(), "继承的方法应该正常工作")

    log.info("自定义继承异常: {}", customException)
  }

  @Test
  fun `测试 KnownException 的空值处理`() {
    log.info("测试 KnownException 的空值处理")

    val exception = KnownException()

    // 测试设置 null 值
    exception.setMsg(null)
    exception.setMeta(null)
    exception.setCode(null)

    assertNull(exception.getMsg(), "消息应该为 null")
    assertNull(exception.getMeta(), "元异常应该为 null")
    assertNull(exception.getCode(), "错误码应该为 null")

    log.info("空值处理测试通过")
  }

  @Test
  fun `测试 requireKnown 函数的懒加载特性`() {
    log.info("测试 requireKnown 函数的懒加载特性")

    var messageComputed = false

    // 表达式为真时，懒加载消息不应该被计算
    requireKnown(true) {
      messageComputed = true
      "这个消息不应该被计算"
    }

    assertEquals(false, messageComputed, "表达式为真时消息不应该被计算")

    // 表达式为假时，懒加载消息应该被计算
    assertFailsWith<KnownException> {
      requireKnown(false) {
        messageComputed = true
        "现在消息被计算了"
      }
    }

    assertEquals(true, messageComputed, "表达式为假时消息应该被计算")

    log.info("懒加载特性测试通过")
  }
}
