package com.truenine.component.core.lang

import org.slf4j.Logger
import kotlin.reflect.KClass

/**
 * slf4j 日志 log 对象桥接器，针对 kotlin
 *
 * @author TrueNine
 * @since 2023-02-19
 */
object LogKt {
  /**
   * 获取日志对象
   *
   * @param kclazz 当前日志记录的 kt 类对象
   * @return slf4j log 对象
   */
  fun getLog(kclazz: KClass<*>): Logger {
    return KtLogBridge.getLog(kclazz.java)
  }
}
