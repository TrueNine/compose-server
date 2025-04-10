package net.yan100.compose.rds.converters.jimmer

import net.yan100.compose.typing.IntTyping

class IntTypingJimmerProvider :
  AbstractJimmerTypingProvider<IntTyping, Int>(IntTyping::class, Int::class)
