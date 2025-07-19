package io.github.truenine.composeserver.testtoolkit.autoconfig

import io.github.truenine.composeserver.testtoolkit.log
import io.github.truenine.composeserver.testtoolkit.properties.AnsiOutputMode
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * # ANSI 输出模式测试
 *
 * 测试 AnsiOutputMode 枚举类的所有功能
 *
 * @author TrueNine
 * @since 2025-07-12
 */
class AnsiOutputModeTest {

  @Test
  fun `测试 NEVER 模式的值和字符串表示`() {
    log.info("开始测试 NEVER 模式")

    val mode = AnsiOutputMode.NEVER

    assertEquals("never", mode.value, "NEVER 模式的值应该是 'never'")
    assertEquals("never", mode.toString(), "NEVER 模式的字符串表示应该是 'never'")

    log.info("NEVER 模式测试完成")
  }

  @Test
  fun `测试 DETECT 模式的值和字符串表示`() {
    log.info("开始测试 DETECT 模式")

    val mode = AnsiOutputMode.DETECT

    assertEquals("detect", mode.value, "DETECT 模式的值应该是 'detect'")
    assertEquals("detect", mode.toString(), "DETECT 模式的字符串表示应该是 'detect'")

    log.info("DETECT 模式测试完成")
  }

  @Test
  fun `测试 ALWAYS 模式的值和字符串表示`() {
    log.info("开始测试 ALWAYS 模式")

    val mode = AnsiOutputMode.ALWAYS

    assertEquals("always", mode.value, "ALWAYS 模式的值应该是 'always'")
    assertEquals("always", mode.toString(), "ALWAYS 模式的字符串表示应该是 'always'")

    log.info("ALWAYS 模式测试完成")
  }

  @Test
  fun `测试所有枚举值的完整性`() {
    log.info("开始测试所有枚举值")

    val allValues = AnsiOutputMode.values()

    assertEquals(3, allValues.size, "应该有3个枚举值")

    val expectedValues = setOf("never", "detect", "always")
    val actualValues = allValues.map { it.value }.toSet()

    assertEquals(expectedValues, actualValues, "所有枚举值应该包含预期的值")

    log.info("所有枚举值测试完成")
  }

  @Test
  fun `测试枚举值的唯一性`() {
    log.info("开始测试枚举值的唯一性")

    val values = AnsiOutputMode.values().map { it.value }
    val uniqueValues = values.toSet()

    assertEquals(values.size, uniqueValues.size, "所有枚举值应该是唯一的")

    log.info("枚举值唯一性测试完成")
  }
}
