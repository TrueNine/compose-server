package net.yan100.compose.exceptions

/**
 * 远程调用错误
 *
 * @author TrueNine
 * @since 2023-04-19
 */
@Deprecated("过于泛用，不建议使用")
open class RemoteCallException(
  msg: String? = null,
  metaException: Throwable? = null,
) : KnownException(msg, metaException)
