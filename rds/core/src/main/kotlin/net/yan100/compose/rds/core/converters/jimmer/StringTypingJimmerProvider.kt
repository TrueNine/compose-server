package net.yan100.compose.rds.core.converters.jimmer

import net.yan100.compose.core.typing.StringTyping

class StringTypingJimmerProvider :
  AbstractJimmerTypingProvider<StringTyping, String>(
    StringTyping::class,
    String::class,
  )
