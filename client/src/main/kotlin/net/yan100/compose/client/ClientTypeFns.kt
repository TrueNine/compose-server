package net.yan100.compose.client

import net.yan100.compose.client.domain.TypescriptScope
import net.yan100.compose.client.domain.entries.TypescriptName
import net.yan100.compose.meta.client.ClientType

internal fun String.typescriptUseName(): String {
  return this.split(".").last().split("$").last()
}


internal fun ClientType.toTypescriptEnum(): TypescriptScope.Enum {
  return TypescriptScope.Enum(
    name = TypescriptName.Name(typeName.typescriptUseName()),
    constants = enumConstants
  )
}
