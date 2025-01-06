package net.yan100.compose.client

import net.yan100.compose.client.domain.*
import net.yan100.compose.client.domain.entries.TsImport
import net.yan100.compose.client.domain.entries.TsName

fun TsVal.getUsedNames(): List<TsName> {
  return when (this) {
    is TsVal.Constructor -> params.flatMap { it.getUsedNames() }
    is TsVal.Function -> params.flatMap { it.getUsedNames() } + returnType.getUsedNames()
  }
}

fun TsGeneric.getUsedNames(): List<TsName> {
  return when (this) {
    is TsGeneric.Used -> used.getUsedNames()
    else -> emptyList()
  }
}

fun TsTypeVal<*>.toTsName(): TsName {
  return when (this) {
    is TsTypeVal.Ref -> typeName
    else -> TsName.Name(toString())
  }
}

fun TsScope<*>.getUsedNames(): List<TsName> {
  return when (this) {


    is TsScope.Enum -> listOf(name)
    is TsScope.Interface -> {
      val superUsedNames = superTypes.flatMap { it.getUsedNames() }
      val propertiesNames = properties.flatMap { it.getUsedNames() }
      ((superUsedNames + propertiesNames) + name).distinct()
    }

    is TsScope.Class -> {
      val generics = generics.flatMap { it.getUsedNames() }
      val superTypes = superTypes.flatMap { it.getUsedNames() }
      val functions = functions.flatMap { it.getUsedNames() }
      (generics + superTypes + functions + name).distinct()
    }

    is TsScope.TypeAlias ->
      aliasFor.getUsedNames() +
        name +
        usedGenerics.filterIsInstance<TsGeneric.Used>().flatMap { it.used.getUsedNames() }.distinct()

    is TsScope.TypeVal -> definition.getUsedNames()
  }
}

fun TsUseVal<*>.getUsedNames(): List<TsName> {
  return when (this) {
    is TsUseVal.Prop -> typeVal.getUsedNames()
    is TsUseVal.Parameter -> typeVal.getUsedNames()
    is TsUseVal.Return -> typeVal.getUsedNames()
  }
}

fun TsTypeVal<*>.getUsedNames(): List<TsName> {
  return when (this) {
    is TsTypeVal.Array -> usedGeneric.getUsedNames()
    is TsTypeVal.Object -> elements.flatMap { it.getUsedNames() }
    is TsTypeVal.Tuple -> elements.flatMap { it.getUsedNames() }
    is TsTypeVal.Function -> returns.getUsedNames() + params.flatMap { it.getUsedNames() }
    is TsTypeVal.Union -> joinTypes.flatMap { it.getUsedNames() }
    is TsTypeVal.TypeConstant -> element.getUsedNames()
    is TsTypeVal.Record -> keyUsedGeneric.getUsedNames() + valueUsedGeneric.getUsedNames()
    is TsTypeVal.Promise -> usedGeneric.getUsedNames()
    is TsTypeVal.Ref -> listOf(typeName) + usedGenerics.flatMap { it.getUsedNames() }
    is TsTypeVal.Generic -> generic.getUsedNames()
    TsTypeVal.Any,
    TsTypeVal.Bigint,
    TsTypeVal.Boolean,
    TsTypeVal.EmptyObject,
    TsTypeVal.Never,
    TsTypeVal.Null,
    TsTypeVal.Number,
    TsTypeVal.String,
    TsTypeVal.Symbol,
    TsTypeVal.Undefined,
    TsTypeVal.Unknown,
    TsTypeVal.Void -> emptyList()
  }
}

fun TsName.asTsImport(useType: Boolean = true): TsImport? {
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
    it.asTsImport(useType)
  }
}

fun TsScope<*>.collectImports(): List<TsImport> {
  val imports = when (this) {
    is TsScope.Enum -> emptyList()
    is TsScope.TypeAlias -> aliasFor.asImports(true)
    is TsScope.Interface -> {
      val superTypes = superTypes.flatMap { it.asImports(true) }
      val properties = properties.flatMap { it.getUsedNames().mapNotNull { e -> e.asTsImport(true) } }
      (properties + superTypes).distinct()
    }

    is TsScope.Class -> {
      val functions = functions.flatMap { it.getUsedNames() }.mapNotNull { it.asTsImport(true) }
      val superType = superTypes.mapNotNull { it.typeName.asTsImport(true) }
      (superType + functions).distinct()
    }

    is TsScope.TypeVal -> error("调用错误")
  }
  val thisImport = name.asTsImport()
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
  if (isEmpty()) return ""
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
