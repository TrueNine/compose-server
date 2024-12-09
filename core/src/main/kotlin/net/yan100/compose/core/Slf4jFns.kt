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

import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

/**
 * slf4j 日志 log 对象桥接器，针对 kotlin
 *
 * @author TrueNine
 * @since 2023-02-19
 */
@Deprecated(message = "已弃用，改用 slf4j 顶级函数获取日志实现")
object Slf4jKotlinAdaptor {
  /**
   * 获取日志对象
   *
   * @param kClazz 当前日志记录的 kt 类对象
   * @return [Logger] log 对象
   */
  private fun getLog(kClazz: KClass<*>): SysLogger = LoggerFactory.getLogger(kClazz.java)

  /**
   * 获取日志对象
   *
   * @param anyWay 当前日志记录的 kt 类对象
   * @return [Logger] log 对象
   */
  fun getLog(anyWay: Any): SysLogger = getLog(anyWay::class)
}

fun slf4j(clz: Class<*>): SysLogger = LoggerFactory.getLogger(clz)
fun slf4j(kClass: KClass<*> = SysLogger::class): SysLogger = slf4j(kClass.java)
inline fun <reified T : Any> slf4j(): SysLogger = slf4j(T::class)
