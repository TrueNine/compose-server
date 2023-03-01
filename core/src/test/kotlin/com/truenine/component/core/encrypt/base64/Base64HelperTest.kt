package com.truenine.component.core.encrypt.base64

import org.testng.annotations.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class Base64HelperTest {

  private val h = Base64Helper.defaultHelper()

  @Test
  fun testDefaultHelper() {
    assertNotNull(Base64Helper.defaultHelper(), "没有获取到默认对象")
  }

  @Test
  fun testEncode() {
    val t = "这是一段测试字符串"
    val r = h.encode(t)
    assertNotEquals(t, r, "没有进行编码")
  }

  @Test
  fun testEncodeToByte() {
    val t = "这是一段测试字符串".toByteArray()
    val r = h.encodeToByte(t)
    assertNotEquals(t, r, "没有进行编码")
  }
}
