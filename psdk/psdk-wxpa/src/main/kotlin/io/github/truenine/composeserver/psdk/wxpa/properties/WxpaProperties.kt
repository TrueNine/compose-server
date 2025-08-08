package io.github.truenine.composeserver.psdk.wxpa.properties

/**
 * # 微信公众号配置属性
 *
 * @author TrueNine
 * @since 2025-08-08
 */
data class WxpaProperties(
  /** 验证服务器的配置 token */
  var verifyToken: String? = null,

  /** 应用ID */
  var appId: String? = null,

  /** 应用密钥 */
  var appSecret: String? = null,

  /** Token刷新间隔（秒），默认1小时 */
  var tokenRefreshInterval: Long = 3600L,

  /** Token提前过期时间（秒），默认5分钟 */
  var tokenExpireAdvance: Long = 300L,

  /** API调用超时时间（毫秒） */
  var apiTimeout: Long = 10000L,

  /** API调用重试次数 */
  var apiRetryCount: Int = 3,

  /** 是否启用Token自动刷新 */
  var enableAutoRefresh: Boolean = true,
) {
  init {
    require(tokenRefreshInterval > 0) { "Token refresh interval must be positive" }
    require(tokenExpireAdvance >= 0) { "Token expire advance must be non-negative" }
    require(apiTimeout > 0) { "API timeout must be positive" }
    require(apiRetryCount >= 0) { "API retry count must be non-negative" }
  }
}
