package io.github.truenine.composeserver.depend.jackson

import io.github.truenine.composeserver.enums.HttpStatus
import io.github.truenine.composeserver.enums.UserAgents

data class AnyEnumRecord(var stringTyping1: UserAgents? = null, var intTyping2: HttpStatus? = null)
