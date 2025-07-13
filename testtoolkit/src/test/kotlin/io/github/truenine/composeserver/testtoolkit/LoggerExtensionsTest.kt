package io.github.truenine.composeserver.testtoolkit

import org.slf4j.LoggerFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * # SLF4J 函数测试
 *
 * 测试 LoggerExtensions.kt 中的日志扩展函数
 *
 * @author TrueNine
 * @since 2025-07-12
 */
class LoggerExtensionsTest {

  @Test
  fun `测试 log 扩展属性获取正确的 Logger`() {
    log.info("开始测试 log 扩展属性")

    // 直接调用扩展属性来确保覆盖率
    val logger = this.log
    val logger2 = log  // 再次调用确保覆盖

    assertNotNull(logger, "Logger 不应该为 null")
    assertNotNull(logger2, "Logger2 不应该为 null")
    assertEquals(LoggerExtensionsTest::class.java.name, logger.name, "Logger 名称应该与类名匹配")
    assertEquals(logger.name, logger2.name, "两次获取的 Logger 名称应该一致")

    log.info("log 扩展属性测试完成")
  }

  @Test
  fun `测试不同类型的 log 扩展属性`() {
    log.info("开始测试不同类型的 log 扩展属性")

    // 测试字符串类型 - 确保调用扩展属性
    val testString = "test"
    val stringLogger = testString.log
    assertEquals(String::class.java.name, stringLogger.name, "String 类型的 Logger 名称应该正确")

    // 测试列表类型 - 确保调用扩展属性
    val testList = listOf(1, 2, 3)
    val listLogger = testList.log
    assertTrue(listLogger.name.contains("List"), "List 类型的 Logger 名称应该包含 List")

    // 测试自定义类型 - 确保调用扩展属性
    val testObject = TestClass()
    val testLogger = testObject.log
    assertEquals(TestClass::class.java.name, testLogger.name, "自定义类型的 Logger 名称应该正确")

    // 测试数字类型 - 确保调用扩展属性
    val testNumber = 42
    val numberLogger = testNumber.log
    assertEquals(Integer::class.java.name, numberLogger.name, "Integer 类型的 Logger 名称应该正确")

    log.info("不同类型的 log 扩展属性测试完成")
  }

  @Test
  fun `测试 Logger info 扩展函数打印变量值`() {
    log.info("开始测试 Logger info 扩展函数")

    // 测试属性引用（KCallable 扩展函数的正确用法）
    // 这些调用会触发 LoggerExtensions.kt 中的 info 扩展函数
    log.info(this::testProperty)
    log.info(this::testNumber)
    log.info(this::testList)

    // 确保扩展函数被调用 - 使用不同的logger实例
    val customLogger = this.log
    customLogger.info(this::testProperty)
    customLogger.info(this::testNumber)

    log.info("Logger info 扩展函数测试完成")
  }

  // 测试属性，用于测试 KCallable 扩展函数（需要是 public 才能被反射访问）
  val testProperty: String = "Hello, World!"
  val testNumber: Int = 42
  val testList: List<Int> = listOf(1, 2, 3)

  @Test
  fun `测试 Logger info 扩展函数处理不同类型的变量`() {
    log.info("开始测试 Logger info 扩展函数处理不同类型")

    // 测试不同类型的属性引用
    // 注意：nullValue 是可空类型，不能用于 KCallable<T : Any> 扩展函数
    log.info("nullValue: $nullValue") // 直接记录可空值
    log.info(this::booleanValue)
    log.info(this::doubleValue)
    log.info(this::mapValue)

    log.info("Logger info 扩展函数处理不同类型测试完成")
  }

  // 测试属性，用于测试不同类型的 KCallable（需要是 public 才能被反射访问）
  val nullValue: String? = null
  val booleanValue: Boolean = true
  val doubleValue: Double = 3.14
  val mapValue: Map<String, String> = mapOf("key" to "value")

  @Test
  fun `测试 Logger info 扩展函数处理属性`() {
    log.info("开始测试 Logger info 扩展函数处理属性")

    val testObject = TestClass()

    // 测试对象属性
    log.info(testObject::name)
    log.info(testObject::value)
    log.info(testObject::isActive)

    log.info("Logger info 扩展函数处理属性测试完成")
  }

  @Test
  fun `测试 Logger 实例的一致性`() {
    log.info("开始测试 Logger 实例的一致性")

    val logger1 = this.log
    val logger2 = this.log

    // 同一个类的 Logger 实例应该是相同的（由 LoggerFactory 缓存）
    assertEquals(logger1.name, logger2.name, "同一个类的 Logger 名称应该相同")

    // 验证与直接使用 LoggerFactory 获取的 Logger 一致
    val directLogger = LoggerFactory.getLogger(LoggerExtensionsTest::class.java)
    assertEquals(logger1.name, directLogger.name, "扩展属性获取的 Logger 应该与直接获取的一致")

    log.info("Logger 实例一致性测试完成")
  }

  @Test
  fun `测试 Slf4j 扩展函数的直接调用`() {
    log.info("开始测试 Slf4j 扩展函数的直接调用")

    // 直接测试扩展属性的调用
    val testLogger = this.log
    assertNotNull(testLogger, "扩展属性应该返回有效的 Logger")

    // 直接测试扩展函数的调用
    testLogger.info(this::testProperty)
    testLogger.info(this::testNumber)
    testLogger.info(this::booleanValue)
    testLogger.info(this::doubleValue)

    // 测试不同对象的扩展属性
    val testObj = TestClass()
    val objLogger = testObj.log
    assertNotNull(objLogger, "对象的扩展属性应该返回有效的 Logger")

    // 测试对象属性的扩展函数调用
    objLogger.info(testObj::name)
    objLogger.info(testObj::value)
    objLogger.info(testObj::isActive)

    log.info("Slf4j 扩展函数的直接调用测试完成")
  }

  @Test
  fun `测试 Slf4j 扩展函数的各种数据类型`() {
    log.info("开始测试 Slf4j 扩展函数的各种数据类型")

    // 测试基本数据类型 - 使用属性而不是局部变量
    log.info(this::testProperty)
    log.info(this::testNumber)
    log.info(this::booleanValue)
    log.info(this::doubleValue)

    // 测试对象属性
    val testObj = TestClass()
    log.info(testObj::name)
    log.info(testObj::value)
    log.info(testObj::isActive)

    log.info("Slf4j 扩展函数的各种数据类型测试完成")
  }

  @Test
  fun `测试 Slf4j 扩展属性在不同类型对象上的使用`() {
    log.info("开始测试 Slf4j 扩展属性在不同类型对象上的使用")

    // 测试字符串对象的扩展属性
    val stringObj = "test"
    val stringLogger = stringObj.log
    assertNotNull(stringLogger, "字符串对象的扩展属性应该返回有效的 Logger")
    assertEquals("java.lang.String", stringLogger.name, "字符串对象的 Logger 名称应该正确")

    // 测试数字对象的扩展属性
    val intObj = 42
    val intLogger = intObj.log
    assertNotNull(intLogger, "整数对象的扩展属性应该返回有效的 Logger")
    assertEquals("java.lang.Integer", intLogger.name, "整数对象的 Logger 名称应该正确")

    // 测试列表对象的扩展属性
    val listObj = listOf(1, 2, 3)
    val listLogger = listObj.log
    assertNotNull(listLogger, "列表对象的扩展属性应该返回有效的 Logger")

    // 测试自定义对象的扩展属性
    val customObj = TestClass()
    val customLogger = customObj.log
    assertNotNull(customLogger, "自定义对象的扩展属性应该返回有效的 Logger")
    assertEquals($$"io.github.truenine.composeserver.testtoolkit.LoggerExtensionsTest$TestClass", customLogger.name, "自定义对象的 Logger 名称应该正确")

    log.info("Slf4j 扩展属性在不同类型对象上的使用测试完成")
  }

  /** 测试用的内部类（需要是 public 才能被反射访问） */
  class TestClass {
    val name: String = "TestClass"
    val value: Int = 100
    val isActive: Boolean = true
  }
}
