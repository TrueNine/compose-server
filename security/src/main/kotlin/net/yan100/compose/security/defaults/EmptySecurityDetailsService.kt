package net.yan100.compose.security.defaults

import net.yan100.compose.core.lang.EmptyDefault
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.core.models.AuthRequestInfo
import net.yan100.compose.security.spring.security.SecurityUserDetailsService


class EmptySecurityDetailsService : EmptyDefault, SecurityUserDetailsService() {
    private val log = slf4j(this::class)

    init {
        log.warn("正在使用默认安全服务，生产环请自行重写")
    }

    override fun loadUserDetailsByAccount(account: String?): AuthRequestInfo? {
        log.warn("account {} 正在获取空体", account)
        return null
    }
}
