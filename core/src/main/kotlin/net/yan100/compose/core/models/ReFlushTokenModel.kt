package net.yan100.compose.core.models

import lombok.EqualsAndHashCode
import lombok.Getter
import lombok.Setter
import lombok.ToString
import java.time.LocalDateTime

/**
 * 过期时间令牌
 *
 * @author TrueNine
 * @since 2022-12-20
 */
class ReFlushTokenModel {
  var userId: String? = null
  var deviceId: String? = null
  var loginIpAddr: String? = null
  var issueAt: LocalDateTime? = null
  var expireTime: LocalDateTime? = null
}
