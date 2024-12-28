package net.yan100.compose.client

import net.yan100.compose.client.domain.TsGeneric
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.domain.entries.TsName
import net.yan100.compose.meta.client.ClientInputGenericType
import net.yan100.compose.meta.client.ClientType

internal fun String.toTsStyleName(): TsName.Name {
  if (isGenericName()) return TsName.Generic(this.unwrapGenericName()).toName()
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
): TsGeneric.Used {
  return TsGeneric.Used(
    used = TsTypeVal.TypeDef(
      typeName = typeNameProvider(this),
      usedGenerics = this.inputGenerics.map { it.toTsGenericUsed(typeNameProvider) }
    ),
    index = index
  )
}

internal fun ClientType.toTsGenericUsed(
  typeNameProvider: (ClientInputGenericType) -> TsName
): List<TsGeneric.Used> {
  return usedGenerics.mapIndexed { index, used ->
    TsGeneric.Used(
      used = TsTypeVal.TypeDef(
        typeName = typeNameProvider(used),
        usedGenerics = used.inputGenerics.map { it.toTsGenericUsed(typeNameProvider) }
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

/**
 * 判断是否为泛型类型
 */
internal fun String.isGenericName(): Boolean {
  return contains("<") && contains(">") && contains("::") && contains("[") && contains("]")
}

/**
 * 将原始的未经转换的泛型参数，转换为 `T` or `E` 此类形式
 */
internal fun String.unwrapGenericName(): String {
  return if (isGenericName()) {
    val genericName = this.substring(indexOf("<") + 1, indexOf(">"))
    genericName.split("::").last().split("]").last().trim()
  } else this
}
