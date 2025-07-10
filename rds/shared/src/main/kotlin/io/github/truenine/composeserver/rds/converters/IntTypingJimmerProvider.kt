package io.github.truenine.composeserver.rds.converters

import io.github.truenine.composeserver.typing.IntTyping

class IntTypingJimmerProvider : AbstractJimmerTypingProvider<IntTyping, Int>(IntTyping::class, Int::class)
