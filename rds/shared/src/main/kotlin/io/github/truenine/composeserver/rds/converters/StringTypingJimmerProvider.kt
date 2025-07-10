package io.github.truenine.composeserver.rds.converters

import io.github.truenine.composeserver.typing.StringTyping

class StringTypingJimmerProvider : AbstractJimmerTypingProvider<StringTyping, String>(StringTyping::class, String::class)
