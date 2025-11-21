package io.github.truenine.composeserver.psdk.wxpa.properties

/**
 * WeChat Official Account configuration properties.
 *
 * @author TrueNine
 * @since 2025-08-08
 */
data class WxpaProperties(
  /** Token used to verify server configuration. */
  var verifyToken: String? = null,

  /** Application ID. */
  var appId: String? = null,

  /** Application secret. */
  var appSecret: String? = null,

  /** Token refresh interval in seconds (default 1 hour). */
  var tokenRefreshInterval: Long = 3600L,

  /** Token expire-advance time in seconds (default 5 minutes). */
  var tokenExpireAdvance: Long = 300L,

  /** API call timeout in milliseconds. */
  var apiTimeout: Long = 10000L,

  /** Number of API call retries. */
  var apiRetryCount: Int = 3,

  /** Whether to enable automatic token refresh. */
  var enableAutoRefresh: Boolean = true,
) {
  init {
    require(tokenRefreshInterval > 0) { "Token refresh interval must be positive" }
    require(tokenExpireAdvance >= 0) { "Token expire advance must be non-negative" }
    require(apiTimeout > 0) { "API timeout must be positive" }
    require(apiRetryCount >= 0) { "API retry count must be non-negative" }
  }
}
