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
package net.yan100.compose.core.extensionfunctions

/**
 * 判断传入的所有 boolean 不得包含 false
 *
 * @param conditions 条件
 * @param lazyMessage 消息
 */
inline fun requireAll(
  vararg conditions: Boolean,
  crossinline lazyMessage: (() -> String),
): Boolean {
  require(!conditions.contains(false), lazyMessage)
  return true
}

/**
 * 判断传入的所有 boolean 不得包含 false
 *
 * @param conditions 条件
 */
fun requireAll(vararg conditions: Boolean): Boolean {
  require(!conditions.contains(false))
  return true
}

fun <T> checkAllNotNull(vararg values: T?) {
  checkAllNotNull(values) { "index $it has null value" }
}

inline fun <T> checkAllNotNull(vararg values: T?, crossinline lazyMessage: (idx: Int) -> Any) {
  values.forEachIndexed { idx, it -> checkNotNull(it) { lazyMessage(idx) } }
}
