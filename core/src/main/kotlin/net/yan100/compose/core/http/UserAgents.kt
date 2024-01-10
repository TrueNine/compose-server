package net.yan100.compose.core.http

import net.yan100.compose.core.lang.StringTyping

/**
 * 一些收集的 userAgent 枚举
 * 使用 val() 方法进行调用
 *
 * @author TrueNine
 * @since 2022-10-28
 */
enum class UserAgents(private val ua: String) : StringTyping {
  /**
   * chrome windows 103
   */
  CHROME_WIN_103("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36"),

  /**
   * chrome windows 115
   */
  CHROME_WIN_115("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36"),

  /**
   * edge windows 115
   */
  EDGE_WIN_115("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36 Edg/115.0.1901.203"),

  /**
   * edge windows 106
   */
  EDGE_WIN_106("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.52");

  override val value: String = ua


  companion object {
    @JvmStatic
    fun findVal(v: String) = entries.find { it.ua == v }
  }
}
