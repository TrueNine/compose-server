package net.yan100.compose.client

import net.yan100.compose.client.domain.TsGeneric
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.domain.entries.TsName
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.client.ClientUsedGeneric

internal fun String.toTsName(): TsName.Name {
  if (isGenericName()) return TsName.Generic(this.unwrapGenericName()).toName()
  return TsName.Name(this.split(".").last().replace("$", "_"))
}

internal fun String.toTsPathName(): TsName.PathName {
  val packageAndClassName = split(".")
  if (packageAndClassName.size == 1) {
    return TsName.PathName(
      name = this.toTsName().name,
      path = ""
    )
  }
  return TsName.PathName(
    name = packageAndClassName.last().toTsName().name,
    path = packageAndClassName.dropLast(1).joinToString("_")
  )
}

internal fun ClientUsedGeneric.toTsGenericUsed(
  typeNameProvider: (ClientUsedGeneric) -> TsName
): TsGeneric.Used {
  return TsGeneric.Used(
    used = TsTypeVal.Ref(
      typeName = typeNameProvider(this),
      usedGenerics = this.usedGenerics.map { it.toTsGenericUsed(typeNameProvider) }
    ),
    index = index
  )
}

internal fun ClientType.toTsGenericUsed(
  typeNameProvider: (ClientUsedGeneric) -> TsName
): List<TsGeneric.Used> {
  return usedGenerics.mapIndexed { index, used ->
    TsGeneric.Used(
      used = TsTypeVal.Ref(
        typeName = typeNameProvider(used),
        usedGenerics = used.usedGenerics.map { it.toTsGenericUsed(typeNameProvider) }
      ),
      index = index
    )
  }
}

internal fun ClientType.toTypescriptEnum(): TsScope.Enum {
  return TsScope.Enum(
    meta = this,
    name = typeName.toTsPathName(),
    constants = resolveEnumConstants()
  )
}

/**
 * 判断是否为泛型类型
 */
internal fun String.isGenericName(): Boolean {
  return contains("<") && contains(">") && contains("::") && contains("[") && contains("]")
}

internal fun <T : Any> String.ifGenericName(default: T, block: (String) -> T = { default }): T {
  return if (isGenericName()) block(this)
  else default
}

internal fun <T : Any> String.ifNotGenericName(default: T, block: (String) -> T = { default }): T {
  return if (!isGenericName()) block(this)
  else default
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
