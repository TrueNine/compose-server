package io.github.truenine.composeserver.rds.converters

import io.github.truenine.composeserver.IStringEnum

class IStringEnumJimmerProvider : AbstractJimmerTypingProvider<IStringEnum, String>(IStringEnum::class, String::class)
