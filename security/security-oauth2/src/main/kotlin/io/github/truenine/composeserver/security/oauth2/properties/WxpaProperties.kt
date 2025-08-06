package io.github.truenine.composeserver.security.oauth2.properties

class WxpaProperties {
  /** 验证服务器的配置 token */
  var verifyToken: String? = null
  var appId: String? = null
  var appSecret: String? = null

  /** 微信固定的 api 过期时间，一般不需要调整 */
  var fixedExpiredSecond = 7000L
}
