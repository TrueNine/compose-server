package net.yan100.compose.client

import net.yan100.compose.client.domain.TsGeneric
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.domain.TsUseVal
import net.yan100.compose.client.domain.entries.TsImport
import net.yan100.compose.client.domain.entries.TsName

fun TsGeneric.getUsedNames(): List<TsName> {
  return when (this) {
    is TsGeneric.Used -> used.getUsedNames()
    else -> emptyList()
  }
}

fun TsTypeVal<*>.toTsName(): TsName {
  return when (this) {
    is TsTypeVal.TypeReference -> typeName
    else -> TsName.Name(toString())
  }
}

fun TsScope<*>.getUsedNames(): List<TsName> {
  return when (this) {
    is TsScope.Class -> TODO()
    is TsScope.Enum -> listOf(name) + constants.keys.map(TsName::Name)
    is TsScope.Interface -> {
      val superUsedNames = superTypes.map { it.getUsedNames() }.flatten()
      val propertiesNames = properties.map { it.defined.getUsedNames() }.flatten()
      ((superUsedNames + propertiesNames) + name).distinct()
    }

    is TsScope.TypeAlias -> listOf(this.name) +
      aliasFor.getUsedNames() +
      usedGenerics.filterIsInstance<TsGeneric.Used>()
        .map { it.used.getUsedNames() }
        .flatten().distinct()

    is TsScope.TypeVal -> definition.getUsedNames()
  }
}

fun TsUseVal<*>.getUsedNames(): List<TsName> {
  return when (this) {
    is TsUseVal.Prop -> typeVal.getUsedNames()
    is TsUseVal.Parameter -> typeVal.getUsedNames()
    is TsUseVal.ReturnType -> typeVal.getUsedNames()
  }
}

fun TsTypeVal<*>.getUsedNames(): List<TsName> {
  return when (this) {
    is TsTypeVal.Array -> usedGeneric.getUsedNames()
    is TsTypeVal.Object -> elements.map { it.getUsedNames() }.flatten()
    is TsTypeVal.Tuple -> elements.map { it.getUsedNames() }.flatten()
    is TsTypeVal.AnonymousFunction -> returnType.getUsedNames() + params.map { it.getUsedNames() }.flatten()
    is TsTypeVal.Union -> joinTypes.map { it.getUsedNames() }.flatten()
    is TsTypeVal.TypeConstant -> element.getUsedNames()
    is TsTypeVal.Record -> keyUsedGeneric.getUsedNames() + valueUsedGeneric.getUsedNames()
    is TsTypeVal.Promise -> usedGeneric.getUsedNames()
    is TsTypeVal.TypeReference -> listOf(typeName) + usedGenerics.map { it.getUsedNames() }.flatten()
    else -> emptyList()
  }
}

fun TsName.toTsImport(useType: Boolean = true): TsImport? {
  return when (this) {
    is TsName.PathName -> {
      TsImport(
        useType = useType,
        fromPath = path,
        usingNames = listOf(this)
      )
    }

    else -> null
  }
}

fun TsTypeVal<*>.asImports(useType: Boolean = true): List<TsImport> {
  return getUsedNames().mapNotNull {
    it.toTsImport(useType)
  }
}

fun TsScope<*>.collectImports(): List<TsImport> {
  val imports = when (this) {
    is TsScope.Enum -> emptyList()
    is TsScope.TypeAlias -> aliasFor.asImports(true)
    is TsScope.Interface -> {
      val imps = superTypes.flatMap { it.asImports(true) } +
        properties.flatMap { it.defined.getUsedNames().mapNotNull { e -> e.toTsImport(true) } } +
        generics.mapNotNull { it.name.toTsImport(true) }
      imps.distinct()
    }

    is TsScope.Class -> TODO("略有难度")
    is TsScope.TypeVal -> TODO("略有难度")
  }
  val thisImport = name.toTsImport()
  return imports.mapNotNull { tsImport ->
    if (tsImport.fromPath == thisImport?.fromPath
      && thisImport.usingNames == tsImport.usingNames
    ) return@mapNotNull null
    if (tsImport.usingNames.isEmpty()) return@mapNotNull null
    val firstName = tsImport.usingNames.first().toVariableName()
    TsImport(
      useType = tsImport.useType,
      fromPath = tsImport.fromPath.let { metaPath ->
        if (thisImport == null) {
          "../$metaPath"
        } else {
          val thisPaths = thisImport.fromPath.split("/")
          val importPaths = tsImport.fromPath.split("/")
          check(thisPaths.size == 2) { "thisImport is not supported $thisPaths" }
          check(importPaths.size == 2) { "importPaths is not supported $importPaths" }
          val t1 = thisPaths[0]
          val i1 = importPaths[0]
          val i2 = importPaths[1]
          if (t1 == i1) {
            "../${i2}/${firstName}"
          } else {
            "../../${i1}/${i2}/${firstName}"
          }
        }
      },
      usingNames = tsImport.usingNames
    )
  }.groupBy { it.fromPath to it.useType }.map { (key, value) ->
    val (fromPath, useType) = key
    TsImport(
      fromPath = fromPath,
      useType = useType,
      usingNames = value.flatMap { it.usingNames }.distinct()
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
    else -> error("variable name [$this] is not supported")
  }
}

fun TsGeneric.Defined.toRenderCode(): String {
  return name.toVariableName().unwrapGenericName()
}

@JvmName("list_TsGeneric_Defined_toRenderCode")
fun List<TsGeneric.Defined>.toRenderCode(): String = joinToString(separator = ", ", prefix = "<", postfix = ">") {
  it.toRenderCode()
}
