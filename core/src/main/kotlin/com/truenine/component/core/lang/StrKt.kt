package com.truenine.component.core.lang

/**
 * 判断一个字符串是否包含有效字符
 *
 * @param str 需判断字符串
 * @return boolean
 */
fun hasText(str: String): Boolean {
  return Str.hasText(str)
}

/**
 * 判断一个字符串是否为无效字符串
 *
 * @param str 字符串
 * @return boolean
 */
fun nonText(str: String): Boolean = !hasText(str)
