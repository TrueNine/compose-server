package net.yan100.compose.core.encrypt.base64

import org.testng.annotations.Test
import kotlin.test.assertNotEquals

class Base64HelperTest {


  @Test
  fun testEncode() {
    val t = "这是一段测试字符串"
    val r = net.yan100.compose.core.encrypt.Base64Helper.encode(t.toByteArray())
    assertNotEquals(t, r, "没有进行编码")
  }

  @Test
  fun testEncodeToByte() {
    val t = "这是一段测试字符串".toByteArray()
    val r = net.yan100.compose.core.encrypt.Base64Helper.encodeToByte(t)



    assertNotEquals(t, r, "没有进行编码")
  }
}
