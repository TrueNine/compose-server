package net.yan100.compose

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** ID 类型相关扩展函数的测试类 */
class AliasFnsTest {

  @Test
  fun `isId 当输入为有效Long类型ID时 返回true`() {
    assertTrue(1L.isId(), "正数 ID 应该返回 true")
    assertTrue(Long.MAX_VALUE.isId(), "最大 Long 值应该返回 true")
  }

  @Test
  fun `isId 当输入为无效Long类型ID时 返回false`() {
    assertFalse(0L.isId(), "0 应该返回 false")
    assertFalse((-1L).isId(), "负数应该返回 false")
  }

  @Test
  fun `isId 当输入为有效字符串ID时 返回true`() {
    assertTrue("123".isId(), "数字字符串应该返回 true")
    assertTrue("9999999999".isId(), "长数字字符串应该返回 true")
  }

  @Test
  fun `isId 当输入为无效字符串ID时 返回false`() {
    assertFalse("".isId(), "空字符串应该返回 false")
    assertFalse("abc".isId(), "非数字字符串应该返回 false")
    assertFalse("123abc".isId(), "混合字符串应该返回 false")
    assertFalse("-123".isId(), "负数字符串应该返回 false")
  }

  @Test
  fun `toId 当输入为有效数字类型时 正确转换为ID`() {
    assertEquals(123L, 123.toId(), "Int类型应该正确转换")
    assertEquals(123L, 123L.toId(), "Long类型应该正确转换")
    assertEquals(123L, 123.0.toId(), "Double类型应该正确转换")
  }

  @Test
  fun `toId 当输入为无效数字类型时 返回null`() {
    assertNull(Long.MIN_VALUE.toId(), "Long.MIN_VALUE应该返回null")
  }

  @Test
  fun `toId 当输入为有效字符串时 正确转换为ID`() {
    assertEquals(123L, "123".toId(), "有效数字字符串应该正确转换")
    assertEquals(-123L, "-123".toId(), "负数字符串应该正确转换")
  }

  @Test
  fun `toId 当输入为无效字符串时 返回null`() {
    assertNull("".toId(), "空字符串应该返回null")
    assertNull("abc".toId(), "非数字字符串应该返回null")
    assertNull("123abc".toId(), "混合字符串应该返回null")
    assertNull(Long.MIN_VALUE.toString().toId(), "Long.MIN_VALUE字符串应该返回null")
  }
}
