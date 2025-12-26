package io.github.truenine.composeserver.consts

import java.util.regex.Pattern
import kotlin.test.*

/** Verifies the regular-expression constants declared in {@link IRegexes}. */
class IRegexTest {
  @Test
  fun matchesChineseAdministrativeCodes() {
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
  fun matchesChineseIdCardNumbers() {
    val pattern = Pattern.compile(IRegexes.CHINA_ID_CARD)
    assertTrue { pattern.matcher("430404197210280012").matches() }
    // Basic matches
    assertTrue { pattern.matcher("43040419721028001X").matches() }
    assertTrue { pattern.matcher("43040419721028001x").matches() }
    // Length validation
    assertFalse { pattern.matcher("43040419721028001x1").matches() }
    assertFalse { pattern.matcher("43040419721028001").matches() }
    // Invalid region codes
    assertFalse { pattern.matcher("01040419721028001").matches() }
    assertFalse { pattern.matcher("10040419721028001").matches() }
  }

  @Test
  fun matchesAntStyleUriPatterns() {
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
  fun matchesRbacNamePattern() {
    val reg = Pattern.compile(IRegexes.RBAC_NAME)
    assertTrue { reg.matcher("abc").matches() }
    assertTrue { reg.matcher("user_read").matches() }
    assertTrue { reg.matcher("user:read").matches() }
    assertTrue { reg.matcher("a").matches() }
    // Invalid cases
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
