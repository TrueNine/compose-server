package net.yan100.compose.client

import net.yan100.compose.client.domain.TsGeneric
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.domain.entries.TsImport
import net.yan100.compose.client.domain.entries.TsName

fun TsGeneric.getUsedNames(): List<TsName> {
  return when (this) {
    is TsGeneric.Used -> used.getUsedNames()
    else -> emptyList()
  }
}

fun TsTypeVal.toTsName(): TsName {
  return when (this) {
    is TsTypeVal.TypeDef -> this.typeName
    else -> TsName.Name(this.toString())
  }
}

fun TsTypeVal.getUsedNames(): List<TsName> {
  return when (this) {
    is TsTypeVal.Array -> usedGeneric.getUsedNames()
    is TsTypeVal.Object -> elements.map { it.defined.getUsedNames() }.flatten()
    is TsTypeVal.Tuple -> elements.map { it.defined.getUsedNames() }.flatten()
    is TsTypeVal.AnonymousFunction -> returnType.getUsedNames() + params.map { it.defined.getUsedNames() }.flatten()
    is TsTypeVal.Union -> joinTypes.map { it.getUsedNames() }.flatten()
    is TsTypeVal.TypeConstant -> element.getUsedNames()
    is TsTypeVal.Record -> keyUsedGeneric.getUsedNames() + valueUsedGeneric.getUsedNames()
    is TsTypeVal.Promise -> usedGeneric.getUsedNames()
    is TsTypeVal.TypeDef -> listOf(typeName) + usedGenerics.map { it.getUsedNames() }.flatten()
    else -> emptyList()
  }
}

fun TsName.toTsImport(useType: Boolean = true): TsImport? {
  return when (this) {
    is TsName.PathName -> {
      TsImport(
        useType = useType,
        fromPath = this.path,
        usingNames = listOf(this)
      )
    }

    else -> null
  }
}

fun TsTypeVal.asImports(useType: Boolean = true): List<TsImport> {
  return getUsedNames().mapNotNull {
    it.toTsImport(useType)
  }
}

fun TsScope.collectImports(): List<TsImport> {
  val imports = when (this) {
    is TsScope.TypeAlias -> aliasFor.asImports(true)
    is TsScope.Interface -> {
      val imports = superTypes.map { it.asImports(true) }.flatten() +
        properties.map { it.defined.getUsedNames() }.flatten().mapNotNull { it.toTsImport(true) } +
        generics.mapNotNull { it.name.toTsImport(true) }
      imports.distinct()
    }

    is TsScope.Class -> {
      // TODO 略有难度
      emptyList()
    }

    is TsScope.Enum,
      -> emptyList()

    else -> emptyList()
  }

  return imports.groupBy { it.fromPath to it.useType }
    .map { (_, tsImport) ->
      TsImport(
        useType = tsImport.first().useType,
        fromPath = tsImport.first().fromPath,
        usingNames = tsImport.map { it.usingNames }.flatten()
      )
    }
}

@JvmName("list_TsImport_toRenderCode")
fun List<TsImport>.toRenderCode(): String {
  return mapNotNull { tsImport ->
    if (tsImport.usingNames.isEmpty()) return@mapNotNull null
    val prefix = "import" + if (tsImport.useType) " type" else ""
    val suffix = "from '${tsImport.fromPath.unwrapGenericName()}'"
    val usingNames = tsImport.usingNames.joinToString(separator = ", ", prefix = "{", postfix = "}") { name ->
      when (name) {
        is TsName.Name -> name.name
        is TsName.PathName -> name.name
        is TsName.As -> "${name.name} as ${name.asName}"
        else -> error("import name $name is not supported")
      }
    }
    "$prefix $usingNames $suffix"
  }.joinToString("\n")
}


internal fun TsName.toVariableName(): String {
  return when (this) {
    is TsName.Name -> name
    is TsName.PathName -> name
    is TsName.Generic -> name.unwrapGenericName()
    else -> error("variable name $this is not supported")
  }
}

fun TsGeneric.Defined.toRenderCode(): String {
  return name.toVariableName().unwrapGenericName()
}

@JvmName("list_TsGeneric_Defined_toRenderCode")
fun List<TsGeneric.Defined>.toRenderCode(): String = joinToString(separator = ", ", prefix = "<", postfix = ">") {
  it.toRenderCode()
}

