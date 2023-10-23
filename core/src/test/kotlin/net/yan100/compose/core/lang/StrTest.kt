package net.yan100.compose.core.lang


import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class StrTest {
  @Test
  fun testNonText() {
    assertTrue {
      Str.nonText("")
    }
  }

  @Test
  fun testInLine() {
    val a = Str.inLine("1\n")
    assertFalse {
      a.contains("\n")
    }
  }

  @Test
  fun testHasText() {
    assertTrue {
      Str.hasText("abc")
    }
  }

  @Test
  fun testOmit() {
    val b = Str.omit("abc", 2)
    assertFalse {
      b.contains("c")
    }
  }

  //@Test
  fun a() {
    val names = mutableLockListOf("", "十", "百", "千", "万", "亿").reversed()
    val numbers = charArrayOf('零', '一', '二', '三', '四', '五', '六', '七', '八', '九')

    val a = 9102039402394
    val str = a.toString().run {
      String(map {
        numbers[it.code - 48]
      }.toCharArray())
    }

    val groups = str.chunked(4).toMutableList()
    for ((index, s) in groups.withIndex()) {
      groups[index] = "${s}${names[index + groups.size]}"
    }
    println(groups)
  }
}
