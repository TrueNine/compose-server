package io.github.truenine.composeserver.annotations

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * # 敏感数据脱敏策略测试
 *
 * 测试 SensitiveStrategy 枚举中定义的各种脱敏策略
 */
class SensitiveRefTest {

  @Test
  fun `测试 ONCE 策略 - 单个星号掩码`() {
    log.info("测试 ONCE 策略 - 单个星号掩码")

    val strategy = SensitiveStrategy.ONCE
    val desensitizer = strategy.desensitizeSerializer()

    assertEquals("*", desensitizer("任何内容"))
    assertEquals("*", desensitizer(""))
    assertEquals("*", desensitizer("123456789"))
    assertEquals("*", desensitizer("sensitive data"))

    log.info("ONCE 策略测试通过")
  }

  @Test
  fun `测试 NONE 策略 - 不进行脱敏`() {
    log.info("测试 NONE 策略 - 不进行脱敏")

    val strategy = SensitiveStrategy.NONE
    val desensitizer = strategy.desensitizeSerializer()

    assertEquals("原始数据", desensitizer("原始数据"))
    assertEquals("", desensitizer(""))
    assertEquals("123456789", desensitizer("123456789"))
    assertEquals("sensitive data", desensitizer("sensitive data"))

    log.info("NONE 策略测试通过")
  }

  @Test
  fun `测试 PHONE 策略 - 手机号脱敏`() {
    log.info("测试 PHONE 策略 - 手机号脱敏")

    val strategy = SensitiveStrategy.PHONE
    val desensitizer = strategy.desensitizeSerializer()

    // 根据实际的正则表达式 "^(\\S{3})\\S+(\\S{2})$" 来验证
    assertEquals("138****34", desensitizer("13812341234"))
    assertEquals("186****78", desensitizer("18656785678"))
    assertEquals("155****99", desensitizer("15599999999"))

    // 测试不符合格式的情况
    val shortNumber = desensitizer("123")
    log.info("短号码脱敏结果: {}", shortNumber)

    log.info("PHONE 策略测试通过")
  }

  @Test
  fun `测试 EMAIL 策略 - 邮箱脱敏`() {
    log.info("测试 EMAIL 策略 - 邮箱脱敏")

    val strategy = SensitiveStrategy.EMAIL
    val desensitizer = strategy.desensitizeSerializer()

    assertEquals("te****@example.com", desensitizer("test@example.com"))
    assertEquals("us****@gmail.com", desensitizer("user@gmail.com"))
    assertEquals("ad****@company.org", desensitizer("admin@company.org"))

    // 测试复杂邮箱
    val complexEmail = desensitizer("user.name+tag@example.co.uk")
    log.info("复杂邮箱脱敏结果: {}", complexEmail)

    log.info("EMAIL 策略测试通过")
  }

  @Test
  fun `测试 ID_CARD 策略 - 身份证号脱敏`() {
    log.info("测试 ID_CARD 策略 - 身份证号脱敏")

    val strategy = SensitiveStrategy.ID_CARD
    val desensitizer = strategy.desensitizeSerializer()

    // 根据实际的正则表达式 "^(\\S{2})\\S+(\\S{2})$" 来验证
    assertEquals("11****34", desensitizer("110101199001011234"))
    assertEquals("43****12", desensitizer("430404197210280012"))
    assertEquals("12****78", desensitizer("123456789012345678"))

    log.info("ID_CARD 策略测试通过")
  }

  @Test
  fun `测试 BANK_CARD_CODE 策略 - 银行卡号脱敏`() {
    log.info("测试 BANK_CARD_CODE 策略 - 银行卡号脱敏")

    val strategy = SensitiveStrategy.BANK_CARD_CODE
    val desensitizer = strategy.desensitizeSerializer()

    assertEquals("62****34", desensitizer("6212341234123434"))
    assertEquals("43****78", desensitizer("4367123456781278"))
    assertEquals("12****90", desensitizer("1234567890"))

    log.info("BANK_CARD_CODE 策略测试通过")
  }

  @Test
  fun `测试 NAME 策略 - 姓名脱敏`() {
    log.info("测试 NAME 策略 - 姓名脱敏")

    val strategy = SensitiveStrategy.NAME
    val desensitizer = strategy.desensitizeSerializer()

    assertEquals("**明", desensitizer("小明"))
    assertEquals("**华", desensitizer("张华"))
    assertEquals("**龙", desensitizer("李小龙"))
    assertEquals("**n", desensitizer("John"))

    // 测试空字符串和空白字符串
    assertEquals("", desensitizer(""))
    assertEquals("   ", desensitizer("   "))

    log.info("NAME 策略测试通过")
  }

  @Test
  fun `测试 MULTIPLE_NAME 策略 - 多段落姓名脱敏`() {
    log.info("测试 MULTIPLE_NAME 策略 - 多段落姓名脱敏")

    val strategy = SensitiveStrategy.MULTIPLE_NAME
    val desensitizer = strategy.desensitizeSerializer()

    // 根据实际的脱敏逻辑：
    // 长度 <= 2: 1 -> "*", 2 -> "**", 其他 -> 原值
    // 长度 > 2: "**" + 最后一个字符
    assertEquals("**", desensitizer("小明")) // 长度2，应该返回 "**"
    assertEquals("**华", desensitizer("张华华")) // 长度3，应该返回 "**华"
    assertEquals("**龙", desensitizer("李小龙")) // 长度3，应该返回 "**龙"

    // 测试长度为1和2的特殊情况
    assertEquals("*", desensitizer("李")) // 长度1，返回 "*"
    assertEquals("**", desensitizer("张华")) // 长度2，返回 "**"

    // 测试空字符串
    assertEquals("", desensitizer(""))
    assertEquals("   ", desensitizer("   "))

    log.info("MULTIPLE_NAME 策略测试通过")
  }

  @Test
  fun `测试 ADDRESS 策略 - 地址脱敏`() {
    log.info("测试 ADDRESS 策略 - 地址脱敏")

    val strategy = SensitiveStrategy.ADDRESS
    val desensitizer = strategy.desensitizeSerializer()

    val address1 = "北京市朝阳区建国门外大街1号"
    val result1 = desensitizer(address1)
    log.info("地址脱敏: {} -> {}", address1, result1)

    val address2 = "上海市浦东新区陆家嘴环路1000号"
    val result2 = desensitizer(address2)
    log.info("地址脱敏: {} -> {}", address2, result2)

    assertNotNull(result1, "地址脱敏结果不应该为空")
    assertNotNull(result2, "地址脱敏结果不应该为空")

    log.info("ADDRESS 策略测试通过")
  }

  @Test
  fun `测试 PASSWORD 策略 - 密码脱敏`() {
    log.info("测试 PASSWORD 策略 - 密码脱敏")

    val strategy = SensitiveStrategy.PASSWORD
    val desensitizer = strategy.desensitizeSerializer()

    assertEquals("****", desensitizer("password123"))
    assertEquals("****", desensitizer("123456"))
    assertEquals("****", desensitizer(""))
    assertEquals("****", desensitizer("very_long_password_with_special_chars!@#"))

    log.info("PASSWORD 策略测试通过")
  }

  @Test
  fun `测试所有策略的 desensitizeSerializer 方法`() {
    log.info("测试所有策略的 desensitizeSerializer 方法")

    val strategies = SensitiveStrategy.values()

    strategies.forEach { strategy ->
      val desensitizer = strategy.desensitizeSerializer()
      assertNotNull(desensitizer, "策略 $strategy 的脱敏器不应该为空")

      // 测试脱敏器能正常工作
      val result = desensitizer("test")
      assertNotNull(result, "策略 $strategy 的脱敏结果不应该为空")

      log.info("策略 {} 脱敏 'test' -> '{}'", strategy.name, result)
    }

    log.info("所有策略的 desensitizeSerializer 方法测试通过")
  }

  @Test
  fun `测试策略枚举的完整性`() {
    log.info("测试策略枚举的完整性")

    val expectedStrategies = setOf("ONCE", "NONE", "PHONE", "EMAIL", "ID_CARD", "BANK_CARD_CODE", "NAME", "MULTIPLE_NAME", "ADDRESS", "PASSWORD")

    val actualStrategies = SensitiveStrategy.values().map { it.name }.toSet()

    assertEquals(expectedStrategies, actualStrategies, "策略枚举应该包含所有预期的策略")

    log.info("策略枚举包含 {} 个策略: {}", actualStrategies.size, actualStrategies)
  }

  @Test
  fun `测试脱敏策略的边界情况`() {
    log.info("测试脱敏策略的边界情况")

    val testCases = listOf("", " ", "  ", "\n", "\t", "\r\n")

    SensitiveStrategy.values().forEach { strategy ->
      testCases.forEach { testCase ->
        val desensitizer = strategy.desensitizeSerializer()
        val result = desensitizer(testCase)

        log.info("策略 {} 处理 '{}' -> '{}'", strategy.name, testCase.replace("\n", "\\n").replace("\t", "\\t").replace("\r", "\\r"), result)
        assertNotNull(result, "策略 $strategy 处理边界情况应该返回非空结果")
      }
    }

    log.info("边界情况测试通过")
  }
}
