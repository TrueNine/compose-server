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
package net.yan100.compose.depend.jvalid.functions

import net.yan100.compose.core.extensionfunctions.hasText
import net.yan100.compose.depend.jvalid.exceptions.ValidException

/**
 * # 双参数校验域
 *
 * @property aValue a value
 * @property bValue a value
 * @property checkExpression 校验表达式结果
 * @throws ValidException 当两个参数均为 `null` 时
 * @author TrueNine
 * @since 2024-03-13
 */
class PairDiffScope<A, B>(
  private val aValue: A?,
  private val bValue: B?,
  private val checkExpression: Boolean = false,
) {
  init {
    if (null == aValue && null == bValue) throw ValidException("两侧的值均为空")
  }

  var lazyMessage: (() -> Any)? = null

  /** ## 检测表达式的值，抛出异常 */
  fun check() {
    if (!this.checkExpression) throw ValidException(lazyMessage?.invoke().toString())
  }
}

/**
 * ## 为符合规定的表达式抛出异常消息
 *
 * @param lazyMessage 异常信息
 */
infix fun <A, B> PairDiffScope<A, B>.message(lazyMessage: () -> Any) {
  this.lazyMessage = lazyMessage
  check()
}

/**
 * ## 为符合规定的表达式抛出异常消息
 *
 * @param message 异常信息
 */
infix fun <A, B> PairDiffScope<A, B>.message(message: Any) {
  message { message }
}

private fun toValidException(f: () -> Unit) {
  try {
    f()
  } catch (ex: Throwable) {
    throw ValidException(ex.message)
  }
}

/**
 * # 参数校验域
 *
 * @author TrueNine
 * @since 2024-03-13
 */
interface CheckParamScope<T> {
  /**
   * # 参数校验域的默认实现
   *
   * @property value 校验参数
   */
  class DefaultCheckScope<T>(
    private val value: T,
  ) : CheckParamScope<T> {
    override fun require(checkBlock: T.() -> Unit) = checkBlock(value)
  }

  /**
   * ## 参数校验域带入函数
   *
   * @param checkBlock 校验域
   */
  fun require(checkBlock: T.() -> Unit)

  /**
   * ## 如果参数为 `null` 则抛出异常
   *
   * @param lazyMessage message
   */
  infix fun <A> A?.nilThen(lazyMessage: () -> Any) = toValidException { checkNotNull(this, lazyMessage) }

  /**
   * ## 如果参数为 `null` 则抛出异常
   *
   * @param message message
   */
  infix fun <A> A?.nilThen(message: Any) = nilThen { message }

  /**
   * ## 如果 `字符串不为空` 则抛出异常
   *
   * @param lazyMessage message
   */
  infix fun String?.nonTextThen(lazyMessage: () -> Any) = toValidException { check(this.hasText(), lazyMessage) }

  /**
   * ## 如果 `字符串不为空` 则抛出异常
   *
   * @param message message
   */
  infix fun String?.nonTextThen(message: Any) = nonTextThen { message }

  /**
   * ## 如果两个参数 `不相等` 则抛出异常
   *
   * @param other 对比参数
   * @return 比对参数
   * @see [PairDiffScope]
   */
  infix fun <A> A.notEq(other: A): PairDiffScope<A, A> = PairDiffScope(this, other, this == other)

  /**
   * ## 如果两个参数 `相等` 则抛出异常
   *
   * @param other 对比参数
   * @return 比对参数
   * @see [PairDiffScope]
   */
  infix fun <A> A.eq(other: A): PairDiffScope<A, A> = PairDiffScope(this, other, this != other)

  /**
   * ## 当 集合 `不为空` 时抛出异常
   *
   * @param lazyMessage message
   */
  infix fun <E, A : Iterable<E>> A?.notEmptyThen(lazyMessage: () -> Any) {
    if (null == this || iterator().hasNext()) throw ValidException(lazyMessage().toString())
  }

  /**
   * ## 当 集合 `不为空` 时抛出异常
   *
   * @param message message
   */
  infix fun <E, A : Iterable<E>> A?.notEmptyThen(message: Any) = notEmptyThen { message }

  /**
   * ## 当 集合 `为空` 时抛出异常
   *
   * @param lazyMessage message
   */
  infix fun <E, A : Iterable<E>> A?.emptyThen(lazyMessage: () -> Any) {
    if (null != this && !iterator().hasNext()) throw ValidException(lazyMessage().toString())
  }

  /**
   * ## 当 集合 `为空` 时抛出异常
   *
   * @param message message
   */
  infix fun <E, A : Iterable<E>> A?.emptyThen(message: Any) = emptyThen { message }
}

/**
 * ## 参数校验 dsl
 *
 * 使用特定的 dsl 域 来校验一些参数，它擅长用于在 web 请求中，对 大型的 pojo 进行校验
 *
 * ```kotlin
 *     checkDsl(AuthRequestInfo()) {
 *         require {
 *             deviceId nonTextThen "设备 id 不能为空"
 *             roles emptyThen "权限列表为空"
 *         }
 *     }
 * ```
 *
 * @param validParam 校验对象
 * @param block 校验函数启动域
 * @author TrueNine
 * @since 2024-03-13
 */
inline fun <T> checkDsl(validParam: T?, crossinline block: CheckParamScope<T>.() -> Unit): T {
  if (null == validParam) throw ValidException("校验参数为空")
  block(CheckParamScope.DefaultCheckScope(validParam))
  return validParam
}
