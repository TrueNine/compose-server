package io.github.truenine.composeserver.security

import io.github.truenine.composeserver.domain.AuthRequestInfo
import io.github.truenine.composeserver.security.spring.security.SecurityUserDetailsService
import io.github.truenine.composeserver.slf4j
import io.github.truenine.composeserver.util.IEmptyDefault

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
