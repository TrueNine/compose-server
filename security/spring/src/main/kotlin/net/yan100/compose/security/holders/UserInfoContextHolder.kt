package net.yan100.compose.security.holders

import net.yan100.compose.core.domain.RequestInfo
import net.yan100.compose.core.holders.AbstractThreadLocalHolder

object UserInfoContextHolder : AbstractThreadLocalHolder<RequestInfo?>()
