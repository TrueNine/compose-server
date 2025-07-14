package io.github.truenine.composeserver.rds.converters

import io.github.truenine.composeserver.IStringTyping

class StringTypingJimmerProvider : AbstractJimmerTypingProvider<IStringTyping, String>(IStringTyping::class, String::class)
