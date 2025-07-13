package io.github.truenine.composeserver.exceptions

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * # 远程调用异常测试
 *
 * 测试 RemoteCallException 异常类
 */
@Suppress("DEPRECATION")
class RemoteCallExceptionsTest {

  @Test
  fun `测试 RemoteCallException 默认构造函数`() {
    log.info("测试 RemoteCallException 默认构造函数")

    val exception = RemoteCallException()

    assertNull(exception.getMsg(), "默认消息应该为空")
    assertNull(exception.getMeta(), "默认元异常应该为空")
    assertNotNull(exception.getCode(), "错误码不应该为空")

    log.info("默认构造的远程调用异常: {}", exception)
  }

  @Test
  fun `测试 RemoteCallException 带消息的构造函数`() {
    log.info("测试 RemoteCallException 带消息的构造函数")

    val message = "远程服务调用失败"
    val exception = RemoteCallException(message)

    assertEquals(message, exception.getMsg(), "消息应该匹配")
    assertNull(exception.getMeta(), "元异常应该为空")
    assertNotNull(exception.getCode(), "错误码不应该为空")

    log.info("带消息的远程调用异常: {}", exception)
  }

  @Test
  fun `测试 RemoteCallException 带元异常的构造函数`() {
    log.info("测试 RemoteCallException 带元异常的构造函数")

    val message = "远程服务调用失败"
    val cause = RuntimeException("网络连接超时")
    val exception = RemoteCallException(message, cause)

    assertEquals(message, exception.getMsg(), "消息应该匹配")
    assertEquals(cause, exception.getMeta(), "元异常应该匹配")
    assertEquals(cause, exception.cause, "cause 应该匹配")
    assertNotNull(exception.getCode(), "错误码不应该为空")

    log.info("带元异常的远程调用异常: {}", exception)
  }

  @Test
  fun `测试 RemoteCallException 只带元异常的构造函数`() {
    log.info("测试 RemoteCallException 只带元异常的构造函数")

    val cause = RuntimeException("网络连接超时")
    val exception = RemoteCallException(null, cause)

    assertNull(exception.getMsg(), "消息应该为空")
    assertEquals(cause, exception.getMeta(), "元异常应该匹配")
    assertEquals(cause, exception.cause, "cause 应该匹配")

    log.info("只带元异常的远程调用异常: {}", exception)
  }

  @Test
  fun `测试 RemoteCallException 的继承关系`() {
    log.info("测试 RemoteCallException 的继承关系")

    val exception = RemoteCallException("测试消息")

    // 验证继承关系
    assert(exception is KnownException) { "应该是 KnownException 的子类" }
    assert(exception is RuntimeException) { "应该是 RuntimeException 的子类" }
    assert(exception is Exception) { "应该是 Exception 的子类" }
    assert(exception is Throwable) { "应该是 Throwable 的子类" }

    log.info("继承关系验证通过")
  }

  @Test
  fun `测试 RemoteCallException 继承的方法`() {
    log.info("测试 RemoteCallException 继承的方法")

    val exception = RemoteCallException("初始消息")

    // 测试继承自 KnownException 的方法
    val newMessage = "更新的消息"
    val newCause = IllegalStateException("新的原因")
    val newCode = 503

    exception.setMsg(newMessage)
    exception.setMeta(newCause)
    exception.setCode(newCode)

    assertEquals(newMessage, exception.getMsg(), "设置的消息应该匹配")
    assertEquals(newCause, exception.getMeta(), "设置的元异常应该匹配")
    assertEquals(newCode, exception.getCode(), "设置的错误码应该匹配")

    log.info("继承方法测试通过")
  }

  @Test
  fun `测试 RemoteCallException 的 toString 方法`() {
    log.info("测试 RemoteCallException 的 toString 方法")

    val exception1 = RemoteCallException("远程调用失败")
    val toString1 = exception1.toString()

    assertNotNull(toString1, "toString 结果不应该为空")
    log.info("远程调用异常 toString: {}", toString1)

    // 测试带原因的异常
    val cause = RuntimeException("网络错误")
    val exception2 = RemoteCallException("远程调用失败", cause)
    val toString2 = exception2.toString()

    assertNotNull(toString2, "toString 结果不应该为空")
    log.info("带原因的远程调用异常 toString: {}", toString2)
  }

  @Test
  fun `测试 RemoteCallException 的实际使用场景`() {
    log.info("测试 RemoteCallException 的实际使用场景")

    // 模拟网络调用失败的场景
    fun simulateRemoteCall(): String {
      throw RemoteCallException("API 服务不可用", RuntimeException("连接超时"))
    }

    try {
      simulateRemoteCall()
    } catch (e: RemoteCallException) {
      log.info("捕获到远程调用异常: {}", e.getMsg())
      log.info("异常原因: {}", e.getMeta()?.message)

      assertEquals("API 服务不可用", e.getMsg(), "异常消息应该匹配")
      assertEquals("连接超时", e.getMeta()?.message, "异常原因应该匹配")
    }

    log.info("实际使用场景测试通过")
  }

  @Test
  fun `测试 RemoteCallException 的异常链`() {
    log.info("测试 RemoteCallException 的异常链")

    val rootCause = IllegalArgumentException("参数错误")
    val networkException = RuntimeException("网络异常", rootCause)
    val remoteCallException = RemoteCallException("远程调用失败", networkException)

    // 验证异常链
    assertEquals(networkException, remoteCallException.cause, "直接原因应该匹配")
    assertEquals(rootCause, remoteCallException.cause?.cause, "根本原因应该匹配")

    log.info("异常链验证通过")
    log.info("远程调用异常: {}", remoteCallException.getMsg())
    log.info("网络异常: {}", remoteCallException.cause?.message)
    log.info("根本原因: {}", remoteCallException.cause?.cause?.message)
  }

  @Test
  fun `测试 RemoteCallException 的空值处理`() {
    log.info("测试 RemoteCallException 的空值处理")

    // 测试所有参数都为 null 的情况
    val exception = RemoteCallException(null, null)

    assertNull(exception.getMsg(), "消息应该为 null")
    assertNull(exception.getMeta(), "元异常应该为 null")
    assertNull(exception.cause, "cause 应该为 null")

    // toString 方法应该能正常处理 null 值
    val toStringResult = exception.toString()
    assertNotNull(toStringResult, "toString 结果不应该为空")

    log.info("空值处理测试通过")
  }

  @Test
  fun `测试 RemoteCallException 的类型信息`() {
    log.info("测试 RemoteCallException 的类型信息")

    val exceptionClass = RemoteCallException::class.java

    assertEquals("RemoteCallException", exceptionClass.simpleName, "类名应该正确")
    assertEquals("io.github.truenine.composeserver.exceptions", exceptionClass.packageName, "包名应该正确")

    // 验证父类
    assertEquals<Class<*>>(KnownException::class.java, exceptionClass.superclass, "父类应该是 KnownException")

    log.info("类型信息验证通过")
  }
}
