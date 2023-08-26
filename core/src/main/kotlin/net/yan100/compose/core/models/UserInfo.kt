package net.yan100.compose.core.models

import jakarta.annotation.Nullable
import lombok.EqualsAndHashCode

/**
 * 基础用户传递信息
 *
 * @author T_teng
 * @since 2023-04-06
 */
open class UserInfo {
  @Nullable
  var userId: String? = null

  @Nullable
  var account: String? = null

  @Nullable
  var deviceId: String? = null

  @Nullable
  var loginIpAddr: String? = null

  @Nullable
  var currentIpAddr: String? = null
}
