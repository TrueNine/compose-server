package net.yan100.compose.rds.core.converters.jimmer

import net.yan100.compose.core.typing.IntTyping

class IntTypingJimmerProvider : AbstractJimmerTypingProvider<IntTyping, Int>(
  IntTyping::class,
  Int::class
)
