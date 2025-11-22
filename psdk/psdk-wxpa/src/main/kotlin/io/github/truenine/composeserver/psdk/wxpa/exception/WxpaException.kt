package io.github.truenine.composeserver.psdk.wxpa.exception

/**
 * Base exception for the WeChat Official Account platform.
 *
 * @author TrueNine
 * @since 2025-08-08
 */
sealed class WxpaException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

/** WeChat API invocation exception. */
class WxpaApiException(val errorCode: Int?, val errorMessage: String?, cause: Throwable? = null) :
  WxpaException("WeChat API error: code=$errorCode, message=$errorMessage", cause)

/** WeChat token retrieval exception. */
class WxpaTokenException(message: String, cause: Throwable? = null) : WxpaException("WeChat token error: $message", cause)

/** WeChat signature verification exception. */
class WxpaSignatureException(message: String, cause: Throwable? = null) : WxpaException("WeChat signature error: $message", cause)

/** WeChat configuration exception. */
class WxpaConfigurationException(message: String, cause: Throwable? = null) : WxpaException("WeChat configuration error: $message", cause)
