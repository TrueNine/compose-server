package net.yan100.compose.depend.jackson

import net.yan100.compose.typing.HttpStatusTyping
import net.yan100.compose.typing.UserAgents

data class AnyTypingRecord(var stringTyping1: UserAgents? = null, var intTyping2: HttpStatusTyping? = null)
