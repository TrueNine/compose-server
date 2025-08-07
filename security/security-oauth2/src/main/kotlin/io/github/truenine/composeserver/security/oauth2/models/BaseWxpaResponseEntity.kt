package io.github.truenine.composeserver.security.oauth2.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

/** # 微信公众号响应基类 */
abstract class BaseWxpaResponseEntity {
  /** 错误码 */
  @JsonProperty("errcode") var errorCode: Int? = null

  /** 错误消息 */
  @JsonProperty("errmsg") var errorMessage: String? = null

  /**
   * 过期时间限制（在多久后过期）
   * > 使用秒表示
   */
  @JsonProperty("expires_in") var expireInSecond: Long? = null

  /** 是否为错误响应 */
  @get:JsonIgnore
  val isError: Boolean
    get() = errorCode != null && errorCode != 0
}
