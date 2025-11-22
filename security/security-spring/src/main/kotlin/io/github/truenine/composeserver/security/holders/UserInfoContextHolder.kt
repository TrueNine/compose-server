package io.github.truenine.composeserver.security.holders

import io.github.truenine.composeserver.domain.RequestInfo
import io.github.truenine.composeserver.holders.AbstractThreadLocalHolder

object UserInfoContextHolder : AbstractThreadLocalHolder<RequestInfo>()
