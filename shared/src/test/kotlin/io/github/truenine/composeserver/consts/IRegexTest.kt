package io.github.truenine.composeserver.consts

import java.util.regex.Pattern
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * # 正则表达式常量测试
 *
 * 测试 IRegexes 中定义的各种正则表达式的匹配功能
 */
class IRegexTest {
  @Test
  fun `测试中国行政区划代码正则匹配`() {
    val pattern = Pattern.compile(IRegexes.CHINA_AD_CODE)
    assertTrue { pattern.matcher("43").matches() }
    assertTrue { pattern.matcher("4304").matches() }
    assertTrue { pattern.matcher("430404").matches() }
    assertTrue { pattern.matcher("430404100").matches() }
    assertTrue { pattern.matcher("430404100101").matches() }

    assertTrue { pattern.matcher("430404").matches() }

    assertFalse { pattern.matcher("00").matches() }
    assertFalse { pattern.matcher("01").matches() }
    assertFalse { pattern.matcher("4304041").matches() }
  }

  @Test
  fun `测试中国身份证号码正则匹配`() {
    val pattern = Pattern.compile(IRegexes.CHINA_ID_CARD)
    assertTrue { pattern.matcher("430404197210280012").matches() }
    // 基本匹配
    assertTrue { pattern.matcher("43040419721028001X").matches() }
    assertTrue { pattern.matcher("43040419721028001x").matches() }
    // 位数
    assertFalse { pattern.matcher("43040419721028001x1").matches() }
    assertFalse { pattern.matcher("43040419721028001").matches() }
    // 地理位置不对
    assertFalse { pattern.matcher("01040419721028001").matches() }
    assertFalse { pattern.matcher("10040419721028001").matches() }
  }

  @Test
  fun `测试 Ant 风格 URI 路径正则匹配`() {
    val pattern = IRegexes.ANT_URI.toRegex()
    assertTrue {
      arrayOf("/", "/a", "/a/b", "/.", "/.php", "/aaa.", "/a.b.", "/a.b.c", "/a/*/*", "/a/b/*/*", "/1/2").map(pattern::matches).reduce(Boolean::and)
    }

    assertFalse {
      arrayOf(
          "//a",
          "//",
          "/:",
          "/:/:",
          "/%ad",
          "",
          "/**",
          "/..",
          " ",
          "/ ",
          "./",
          "../",
          " / ",
          "/ /\n",
          "/\n",
          "/\r",
          "/.*",
          "/..",
          "..",
          "/../..",
          "/1/2/**",
          "/1/2/**/*/a",
          "/1/2/*a",
          "/1/2/**",
          "/1/2/**/",
          "/1/2/",
        )
        .map(pattern::matches)
        .reduce(Boolean::or)
    }
  }

  @Test
  fun `测试 RBAC 名称正则匹配`() {
    val reg = Pattern.compile(IRegexes.RBAC_NAME)
    assertTrue { reg.matcher("abc").matches() }
    assertTrue { reg.matcher("user_read").matches() }
    assertTrue { reg.matcher("user:read").matches() }
    assertTrue { reg.matcher("a").matches() }
    // 失败情况
    assertFalse { reg.matcher("_").matches() }
    assertFalse { reg.matcher("_a").matches() }
    assertFalse { reg.matcher(":").matches() }
    assertFalse { reg.matcher(":a").matches() }
    assertFalse { reg.matcher("-").matches() }
    assertFalse { reg.matcher("-a").matches() }
    assertFalse { reg.matcher("9").matches() }
    assertFalse { reg.matcher("2a").matches() }
    assertFalse { reg.matcher("1:").matches() }
    assertFalse { reg.matcher("\n").matches() }
  }
}
