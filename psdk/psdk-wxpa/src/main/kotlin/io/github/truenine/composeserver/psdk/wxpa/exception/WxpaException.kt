package io.github.truenine.composeserver.psdk.wxpa.exception

/**
 * # 微信公众号平台异常基类
 *
 * @author TrueNine
 * @since 2025-08-08
 */
sealed class WxpaException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

/** # 微信API调用异常 */
class WxpaApiException(val errorCode: Int?, val errorMessage: String?, cause: Throwable? = null) :
  WxpaException("WeChat API error: code=$errorCode, message=$errorMessage", cause)

/** # 微信Token获取异常 */
class WxpaTokenException(message: String, cause: Throwable? = null) : WxpaException("WeChat token error: $message", cause)

/** # 微信签名验证异常 */
class WxpaSignatureException(message: String, cause: Throwable? = null) : WxpaException("WeChat signature error: $message", cause)

/** # 微信配置异常 */
class WxpaConfigurationException(message: String, cause: Throwable? = null) : WxpaException("WeChat configuration error: $message", cause)
