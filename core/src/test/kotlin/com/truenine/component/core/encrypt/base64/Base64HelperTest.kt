package com.truenine.component.core.encrypt.base64

import com.truenine.component.core.encrypt.Base64Helper
import org.testng.annotations.Test
import java.util.Base64
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class Base64HelperTest {


  @Test
  fun testEncode() {
    val t = "这是一段测试字符串"
    val r = Base64Helper.encode(t.toByteArray())
    assertNotEquals(t, r, "没有进行编码")
  }

  @Test
  fun testEncodeToByte() {
    val t = "这是一段测试字符串".toByteArray()
    val r = Base64Helper.encodeToByte(t)



    assertNotEquals(t, r, "没有进行编码")
  }
}
