/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 * 
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
*/
package net.yan100.compose.core

/**
 * string 工具类
 *
 * @author TrueNine
 * @since 2022-10-28
 */
interface IString {
  companion object {
    @JvmStatic
    fun nonText(text: String?): Boolean {
      return !hasText(text)
    }

    @JvmStatic
    fun inLine(str: String): String {
      return if (hasText(str)) str.replace("\r", "").replace("\n", "").replace("\r\n", "").replace("\t", "")
      else str
    }

    @JvmStatic
    fun hasText(text: String?): Boolean {
      return !text.isNullOrEmpty() && containsText(text)
    }

    @JvmStatic
    fun containsText(str: CharSequence): Boolean {
      val strLen = str.length
      for (i in 0 until strLen) {
        if (!Character.isWhitespace(str[i])) return true
      }
      return false
    }

    @JvmStatic
    fun omit(s: String): String {
      return omit(s.trim { it <= ' ' }, 100)
    }

    /**
     * 对文本进行省略处理
     *
     * @param s [String]
     * @param maxLen 最大长度
     * @return [String]
     */
    @JvmStatic
    fun omit(s: String, maxLen: Int): String {
      if (nonText(s)) return s
      else {
        val totalLen = s.length
        if (totalLen <= maxLen) return s
        return s.substring(0, maxLen) + "..."
      }
    }

    const val EMPTY: String = ""
  }
}
