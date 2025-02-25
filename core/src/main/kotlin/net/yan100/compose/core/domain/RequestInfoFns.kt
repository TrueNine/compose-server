package net.yan100.compose.core.domain

/** ## 当前是否已经完全登录 */
val AuthRequestInfo.isLogin
  get() =
    userId != null && enabled && nonLocked && nonExpired && account != null
