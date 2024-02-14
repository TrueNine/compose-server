package net.yan100.compose.core.exceptions

/**
 * 远程调用错误
 *
 * @author TrueNine
 * @since 2023-04-19
 */
open class RemoteCallException(msg: String? = null, metaException: Throwable? = null, private val code: Int? = null) :
    KnownException(msg, metaException)
