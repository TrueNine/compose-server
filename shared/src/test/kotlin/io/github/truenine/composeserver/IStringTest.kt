package io.github.truenine.composeserver

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * # 字符串工具类测试
 *
 * 测试 IString 工具类的各种字符串处理功能
 */
class IStringTest {
  @Test
  fun `测试 hasText 方法 - 验证字符串是否包含有效文本`() {
    assertTrue { IString.hasText("abc") }
    assertFalse { IString.hasText("") }
    assertFalse { IString.hasText(" ") }
  }

  @Test
  fun `测试 nonText 方法 - 验证字符串是否为空或仅包含空白字符`() {
    listOf("", " ", "\n", "\r", "\t", "\r\n").forEach { assertTrue { IString.nonText(it) } }
    assertFalse { IString.nonText("a") }
  }

  @Test
  fun `测试 inLine 方法 - 验证移除换行符功能`() {
    val a = IString.inLine("1\n")
    assertFalse { a.contains("\n") }
  }

  @Test
  fun `测试 toSnakeCase 方法 - 验证驼峰命名转蛇形命名`() {
    val result = "MDCFilter".toSnakeCase()
    assertEquals("mdcfilter", result)
    val result2 = "PascCase".toSnakeCase()
    assertEquals("pasc_case", result2)
    val result3 = "PascalCase".toSnakeCase()
    assertEquals("pascal_case", result3)
    val result4 = "camelCase".toSnakeCase()
    assertEquals("camel_case", result4)
    val result5 = "roleGroupId".toSnakeCase()
    assertEquals("role_group_id", result5)
    val result6 = "Address".toSnakeCase()
    assertEquals("address", result6)
    val result7 = "address_details".toSnakeCase()
    assertEquals("address_details", result7)
  }

  @Test
  fun `测试 toPascalCase 方法 - 验证蛇形命名转驼峰命名`() {
    val result = "mdc_filter".toPascalCase()
    assertEquals("mdcFilter", result)
    val result2 = "mdc_filter".toPascalCase(true)
    assertEquals("MdcFilter", result2)
    val result3 = "_mdc_filter".toPascalCase()
    assertEquals("mdcFilter", result3)
    val result4 = "_mdc__filter_".toPascalCase()
    assertEquals("mdcFilter", result4)
  }

  @Test
  fun `测试 omit 方法 - 验证字符串截断功能`() {
    val b = IString.omit("abc", 2)
    assertFalse { b.contains("c") }
  }
}
