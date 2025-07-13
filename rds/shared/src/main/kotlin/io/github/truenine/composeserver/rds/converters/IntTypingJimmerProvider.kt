package io.github.truenine.composeserver.rds.converters

import io.github.truenine.composeserver.IIntTyping

class IntTypingJimmerProvider : AbstractJimmerTypingProvider<IIntTyping, Int>(IIntTyping::class, Int::class)
