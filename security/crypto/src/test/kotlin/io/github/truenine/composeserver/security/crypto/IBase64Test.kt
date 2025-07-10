package io.github.truenine.composeserver.security.crypto

import io.github.truenine.composeserver.consts.IRegexes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class IBase64Test {
  private val metaText = "这是一段测试字符串"
  private val cipherText = "6L+Z5piv5LiA5q615rWL6K+V5a2X56ym5Liy"

  @Test
  fun `encode str`() {
    val r = IBase64.encode(metaText.toByteArray())
    assertNotEquals(r, metaText, "没有进行编码")
    assertEquals(r, cipherText, "编码结果不正确")
    assertTrue("出现非 base64 字符") { IRegexes.BASE_64.toRegex().matches(r) }
  }

  @Test
  fun `decode str`() {
    val m = IBase64.decode(cipherText)
    assertNotEquals(m, cipherText, "没有进行解码")
    assertEquals(m, metaText, "解码结果不正确")
  }

  @Test
  fun `encode bytes`() {
    val r = IBase64.encodeToByte(metaText.toByteArray())
    assertNotEquals(r, metaText.toByteArray(), "没有进行编码")
    assertEquals(cipherText, r.toString(Charsets.UTF_8), "编码结果不正确")
    assertTrue("出现非 base64 字符") { IRegexes.BASE_64.toRegex().matches(r.toString(Charsets.UTF_8)) }
  }
}
