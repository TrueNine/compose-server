package net.yan100.compose.security

import net.yan100.compose.domain.AuthRequestInfo
import net.yan100.compose.security.spring.security.SecurityUserDetailsService
import net.yan100.compose.slf4j
import net.yan100.compose.util.IEmptyDefault

private val log = slf4j(EmptySecurityDetailsService::class)

class EmptySecurityDetailsService : IEmptyDefault, SecurityUserDetailsService() {

  init {
    log.warn("正在使用默认安全服务，生产环请自行重写")
  }

  override fun loadUserDetailsByAccount(account: String?): AuthRequestInfo? {
    log.warn("account {} 正在获取空体", account)
    return null
  }
}
