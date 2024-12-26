package net.yan100.compose.client

import net.yan100.compose.client.domain.TsGeneric
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.domain.entries.TsName
import net.yan100.compose.meta.client.ClientInputGenericType
import net.yan100.compose.meta.client.ClientType

internal fun String.toTsStyleName(): TsName.Name {
  return TsName.Name(this.split(".").last().replace("$", "_"))
}

internal fun String.toTsStylePathName(): TsName.PathName {
  val packageAndClassName = split(".")
  if (packageAndClassName.size == 1) {
    return TsName.PathName(
      name = this.toTsStyleName().name,
      path = ""
    )
  }
  return TsName.PathName(
    name = packageAndClassName.last().toTsStyleName().name,
    path = packageAndClassName.dropLast(1).joinToString("_")
  )
}

internal fun ClientInputGenericType.toTsGenericUsed(
  typeNameProvider: (ClientInputGenericType) -> TsName
): List<TsGeneric.Used> {
  return inputGenerics.map {
    TsGeneric.Used(
      used = TsTypeVal.TypeDef(
        typeName = typeNameProvider(it),
        usedGenerics = it.toTsGenericUsed(typeNameProvider)
      ),
      index = it.index
    )
  }
}

internal fun ClientType.toTsGenericUsed(
  typeNameProvider: (ClientInputGenericType) -> TsName
): List<TsGeneric.Used> {
  return usedGenerics.mapIndexed { index, used ->
    TsGeneric.Used(
      used = TsTypeVal.TypeDef(
        typeName = typeNameProvider(used),
        usedGenerics = used.toTsGenericUsed(typeNameProvider)
      ),
      index = index
    )
  }
}

internal fun ClientType.toTypescriptEnum(): TsScope.Enum {
  return TsScope.Enum(
    meta = this,
    name = typeName.toTsStylePathName(),
    constants = resolveEnumConstants()
  )
}
