package net.yan100.compose.security.holders

import net.yan100.compose.domain.RequestInfo
import net.yan100.compose.holders.AbstractThreadLocalHolder

object UserInfoContextHolder : AbstractThreadLocalHolder<RequestInfo?>()
