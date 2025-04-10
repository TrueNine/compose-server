package net.yan100.compose.rds.converters.jimmer

import net.yan100.compose.typing.StringTyping

class StringTypingJimmerProvider :
  AbstractJimmerTypingProvider<StringTyping, String>(
    StringTyping::class,
    String::class,
  )
