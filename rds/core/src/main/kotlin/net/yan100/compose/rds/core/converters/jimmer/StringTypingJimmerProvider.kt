package net.yan100.compose.rds.core.converters.jimmer

import net.yan100.compose.core.typing.StringTyping
import net.yan100.compose.rds.core.converters.jimmer.AbstractJimmerTypingProvider

class StringTypingJimmerProvider : AbstractJimmerTypingProvider<StringTyping, String>(
  StringTyping::class,
  String::class
)
