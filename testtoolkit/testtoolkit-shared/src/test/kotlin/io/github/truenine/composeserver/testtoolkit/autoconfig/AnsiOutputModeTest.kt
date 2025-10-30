package io.github.truenine.composeserver.testtoolkit.autoconfig

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
  fun ansiOutputModeValuesShouldMapCorrectly() {
    assertEquals("never", AnsiOutputMode.NEVER.value)
    assertEquals("detect", AnsiOutputMode.DETECT.value)
    assertEquals("always", AnsiOutputMode.ALWAYS.value)

    // verify toString
    assertEquals("never", AnsiOutputMode.NEVER.toString())
    assertEquals("detect", AnsiOutputMode.DETECT.toString())
    assertEquals("always", AnsiOutputMode.ALWAYS.toString())
  }

  @Test
  fun ansiOutputModeEnumShouldHaveAllValues() {
    val allValues = AnsiOutputMode.values()
    assertEquals(3, allValues.size)

    val expectedValues = setOf("never", "detect", "always")
    val actualValues = allValues.map { it.value }.toSet()
    assertEquals(expectedValues, actualValues)
  }
}
