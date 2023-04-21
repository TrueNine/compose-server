package com.truenine.component.core.lang;

/**
 * string 工具类
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public interface Str {
  String EMPTY = "";

  static boolean nonText(String text) {
    return !hasText(text);
  }

  static String inLine(String str) {
    if (hasText(str)) {
      return str.replace("\r", "")
        .replace("\n", "")
        .replace("\r\n", "")
        .replace("\t", "");
    } else {
      return str;
    }
  }

  static boolean hasText(String text) {
    return (text != null) && !text.isEmpty() && containsText(text);
  }

  private static boolean containsText(CharSequence str) {
    int strLen = str.length();
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return true;
      }
    }
    return false;
  }

  static String omit(String s) {
    return omit(s.trim(), 100);
  }

  /**
   * 对文本进行省略处理
   *
   * @param s      {@link String}
   * @param maxLen 最大长度
   * @return {@link String}
   */
  static String omit(String s, int maxLen) {
    if (nonText(s)) {
      return s;
    } else {
      var totalLen = s.length();
      if (totalLen <= maxLen) {
        return s;
      }
      return s.substring(0, maxLen) + "...";
    }
  }
}
