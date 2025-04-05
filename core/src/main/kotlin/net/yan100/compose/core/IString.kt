package net.yan100.compose.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * string 工具类
 *
 * @author TrueNine
 * @since 2022-10-28
 */
interface IString {
  companion object {
    /**
     * 检查字符串是否为空或仅包含空白字符
     *
     * @param text 待检查的字符串
     * @return 如果字符串为 null、空字符串或仅包含空白字符则返回 true，否则返回 false
     */
    @JvmStatic
    @OptIn(ExperimentalContracts::class)
    inline fun nonText(text: String?): Boolean {
      contract { returns(true) implies (text == null) }
      return text.isNullOrBlank()
    }

    /**
     * 移除字符串中的所有换行符、回车符和制表符
     *
     * @param str 待处理的字符串
     * @return 处理后的单行字符串
     */
    @JvmStatic
    inline fun inLine(str: String): String {
      return if (str.isNotBlank()) {
        str.replace(Regex("[\r\n\t]"), "")
      } else str
    }

    /**
     * 检查字符串是否包含非空白字符
     *
     * @param text 待检查的字符串
     * @return 如果字符串不为 null 且包含至少一个非空白字符则返回 true，否则返回 false
     */
    @JvmStatic
    @OptIn(ExperimentalContracts::class)
    inline fun hasText(text: String?): Boolean {
      contract { returns(false) implies (text != null) }
      return !text.isNullOrBlank()
    }

    /**
     * 检查字符序列中是否包含非空白字符
     *
     * @param str 待检查的字符序列
     * @return 如果字符序列包含至少一个非空白字符则返回 true，否则返回 false
     */
    @JvmStatic
    inline fun containsText(str: CharSequence): Boolean {
      return str.any { !it.isWhitespace() }
    }

    /**
     * 对文本进行省略处理，如果文本长度超过指定长度则截断并添加省略号
     *
     * @param s 待处理的字符串
     * @param maxLen 最大允许长度
     * @return 处理后的字符串，如果原字符串长度超过最大长度，则截断并添加"..."
     */
    @JvmStatic
    inline fun omit(s: String, maxLen: Int): String {
      if (s.isBlank()) return s
      return if (s.length <= maxLen) s else "${s.substring(0, maxLen)}..."
    }

    const val EMPTY: String = ""
  }
}
