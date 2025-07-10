package net.yan100.compose

import kotlin.test.*

/** ID 类型相关扩展函数的测试类 */
class AliasFnsTest {

  @Test
  fun `test long is id`() {
    assertFalse((-1L).isId(), "负数不是有效Id")
    assertTrue(0L.isId(), "0 是有效Id")
    assertTrue(1L.isId(), "正整数是有效Id")
    assertTrue(Long.MAX_VALUE.isId(), "Long最大值是有效Id")
    assertFalse(Long.MIN_VALUE.isId(), "Long最小值不是有效Id")
  }

  @Test
  fun `test string is id`() {
    assertFalse("".isId(), "空字符串不是有效Id")
    assertTrue("0".isId(), "0 是有效Id")
    assertTrue("123456".isId(), "全数字字符串是有效Id")
    assertTrue("000123".isId(), "前导0的数字字符串是有效Id")
    assertTrue("123a".isId(), "包含字母是有效Id")
    assertFalse("12 34".isId(), "包含空格不是有效Id")
    assertFalse("１２３４".isId(), "全角数字不是有效Id")
    assertFalse("123!".isId(), "包含符号不是有效Id")
    assertFalse("一二三".isId(), "中文不是有效Id")
    assertFalse("123.45".isId(), "小数不是有效Id")
    assertFalse(" 123".isId(), "前有空格不是有效Id")
    assertFalse("123 ".isId(), "后有空格不是有效Id")
    // 超大数字字符串（超出Long范围，但只要是数字也算有效）
    assertTrue("92233720368547758079223372036854775807".isId(), "超大数字字符串也是有效Id")
  }

  @Test
  fun `isId 当输入为有效Long类型ID时 返回true`() {
    assertTrue(1L.isId(), "正数 ID 应该返回 true")
    assertTrue(Long.MAX_VALUE.isId(), "最大 Long 值应该返回 true")
  }

  @Test
  fun `isId 当输入为无效Long类型ID时 返回false`() {
    assertTrue(0L.isId(), "0 应该返回 true")
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
