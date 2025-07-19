package io.github.truenine.composeserver.depend.jackson

import io.github.truenine.composeserver.typing.HttpStatus
import io.github.truenine.composeserver.typing.UserAgents

data class AnyTypingRecord(var stringTyping1: UserAgents? = null, var intTyping2: HttpStatus? = null)
