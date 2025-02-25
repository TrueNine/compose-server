package net.yan100.compose.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IStringTest {
  @Test
  fun `has text`() {
    assertTrue { IString.hasText("abc") }
    assertFalse { IString.hasText("") }
    assertFalse { IString.hasText(" ") }
  }

  @Test
  fun `non text`() {
    listOf("", " ", "\n", "\r", "\t", "\r\n").forEach {
      assertTrue { IString.nonText(it) }
    }
    assertFalse { IString.nonText("a") }
  }

  @Test
  fun `inline text`() {
    val a = IString.inLine("1\n")
    assertFalse { a.contains("\n") }
  }

  @Test
  fun `test toSnakeCase`() {
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
  fun `test toPascalCase`() {
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
  fun `omit text`() {
    val b = IString.omit("abc", 2)
    assertFalse { b.contains("c") }
  }
}
