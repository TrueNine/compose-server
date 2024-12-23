package net.yan100.compose.client

import net.yan100.compose.client.domain.TypescriptEnum
import net.yan100.compose.core.typing.StringTyping
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.types.TypeKind

internal fun String.typescriptUseName(): String {
  return this.split(".").last().split("$").last()
}


internal fun ClientType.toTypescriptEnum(): TypescriptEnum {
  val isString = superTypes.any { it.typeKind == TypeKind.INTERFACE && it.typeName == StringTyping::class.qualifiedName }
  return TypescriptEnum(
    name = typeName.typescriptUseName(),
    isExport = true,
    isString = isString,
    constants = enumConstants
  )
}
