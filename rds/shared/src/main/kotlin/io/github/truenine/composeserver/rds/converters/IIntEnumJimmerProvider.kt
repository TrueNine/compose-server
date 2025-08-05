package io.github.truenine.composeserver.rds.converters

import io.github.truenine.composeserver.IIntEnum

class IIntEnumJimmerProvider : AbstractJimmerTypingProvider<IIntEnum, Int>(IIntEnum::class, Int::class)
