package net.yan100.compose.core.lang

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

/**
 * slf4j 日志 log 对象桥接器，针对 kotlin
 *
 * @author TrueNine
 * @since 2023-02-19
 */
@Deprecated(message = "已弃用，改用 slf4j 顶级函数获取日志实现")
object LogKt {
    /**
     * 获取日志对象
     *
     * @param kClazz 当前日志记录的 kt 类对象
     * @return [Logger] log 对象
     */
    private fun getLog(kClazz: KClass<*>): Logger = LoggerFactory.getLogger(kClazz.java)

    /**
     * 获取日志对象
     *
     * @param anyWay 当前日志记录的 kt 类对象
     * @return [Logger] log 对象
     */
    fun getLog(anyWay: Any): Logger = getLog(anyWay::class)
}

fun slf4j(kClass: KClass<*>): Logger = LoggerFactory.getLogger(kClass.java)
