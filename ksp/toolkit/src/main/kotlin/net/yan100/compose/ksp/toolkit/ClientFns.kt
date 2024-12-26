package net.yan100.compose.ksp.toolkit

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import net.yan100.compose.ksp.toolkit.kotlinpoet.Libs
import net.yan100.compose.meta.client.ClientDoc
import net.yan100.compose.meta.client.ClientInputGenericType
import net.yan100.compose.meta.client.ClientProp
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.types.Doc
import net.yan100.compose.meta.types.TypeKind


fun KSDeclaration.toClientType(log: KSPLogger? = null): ClientType {
  val kind = when (this) {
    is KSClassDeclaration -> {
      if (getKsAnnotationsByAnnotationClassQualifiedName(Libs.org.babyfish.jimmer.sql.Entity.qualifiedName).firstOrNull() != null) {
        TypeKind.IMMUTABLE
      } else if (getKsAnnotationsByAnnotationClassQualifiedName(Libs.org.babyfish.jimmer.sql.Embeddable.qualifiedName).firstOrNull() != null) {
        TypeKind.EMBEDDABLE
      } else TypeKind.valueOf(classKind.toString())
    }

    is KSTypeAlias -> TypeKind.TYPEALIAS
    else -> error("$this is not a KSClassDeclaration")
  }
  val dec = when (this@toClientType) {
    is KSClassDeclaration -> this
    is KSTypeAlias -> realDeclaration as KSClassDeclaration
    else -> error("$this is not a KSClassDeclaration")
  }
  val superTypes = dec.superTypes.map { e -> e.fastResolve() }.map {
    val superParameter = it.arguments.toInputGenericTypeList()
    it.declaration.toClientType(log)
      .copy(usedGenerics = superParameter)
      .clipToSuperType()
  }

  val isAlias = this is KSTypeAlias
  val aliasForTypeName = if (isAlias) realDeclaration.qualifiedNameAsString else null
  val typeParameters = this@toClientType.typeParameters.map { it.qualifiedNameAsString!! }
  return ClientType(
    typeName = qualifiedNameAsString!!,
    typeKind = kind,
    isAlias = if (isAlias) true else null,
    aliasForTypeName = aliasForTypeName,
    argumentLocations = typeParameters,
    doc = docString.toDoc(),
    enumConstants = let {
      if (this is KSClassDeclaration && this.classKind == ClassKind.ENUM_CLASS) {
        declarations.filterIsInstance<KSClassDeclaration>().filter { it.classKind == ClassKind.ENUM_ENTRY }.mapIndexed { i, ir -> ir.simpleNameAsString to i }
          .toMap().toMutableMap()
      } else mutableMapOf()
    },
    superTypes = superTypes.toList()
  )
}


fun KSPropertyDeclaration.toClientProp(): ClientProp {
  val typeName = this.type.fastResolve().declaration.qualifiedNameAsString!!
  val nullable = this.type.fastResolve().isMarkedNullable
  val doc = docString
  val generics = this.type.fastResolve().arguments.toInputGenericTypeList()

  return ClientProp(
    name = simpleNameAsString,
    typeName = typeName,
    nullable = if (nullable) true else null,
    inputGenerics = generics,
    doc = doc.toDoc()// TODO 支持过期注释
  )
}

/**
 * 转换为 注释对象
 */
fun String?.toDoc(): Doc? {
  if (this.isNullOrBlank()) return null
  val annotations = mutableListOf<String>()
  val lines = split("\n").map { it.trim() }.filterNot { it.isBlank() }.filterNot {
    if (it.startsWith("@")) {
      annotations.add(it)
      true
    } else false
  }
  val paramMap: MutableMap<String, String> = mutableMapOf()
  annotations.forEach {
    if (it.startsWith("@param")) {
      val p = it.substringAfter("@param").trim()
      val name = p.substringBefore(" ").trim()
      val desc = p.substringAfter(" ").trim()
      if (name.isNotBlank()) {
        paramMap[name] = desc
      }
    }
  }
  return ClientDoc(value = lines.joinToString("\n"), parameters = paramMap)
}

/**
 * 将泛型参数转换为可填写泛型参数
 */
@JvmName("ksTypeParameterListToInputGenericTypeList")
fun List<KSTypeParameter>.toInputGenericTypeList(): List<ClientInputGenericType> {
  return mapIndexed { i, it ->
    val args = it.typeParameters.toInputGenericTypeList()
    val argName = it.qualifiedNameAsString!!
    ClientInputGenericType(
      typeName = argName,
      index = i,
      inputGenerics = args
    )
  }
}

@JvmName("kSTypeArgumentListToInputGenericTypeList")
fun List<KSTypeArgument>.toInputGenericTypeList(): List<ClientInputGenericType> {
  return mapIndexed { i, it ->
    val args = it.type?.fastResolve()?.arguments
    val declaration = it.type!!.fastResolve()
    val argName = declaration.declaration.qualifiedNameAsString!!
    val nullable = declaration.isMarkedNullable
    ClientInputGenericType(
      typeName = argName,
      index = i,
      nullable = if (nullable) true else null,
      inputGenerics = args?.toInputGenericTypeList() ?: emptyList()
    )
  }
}

/**
 * 将 type 裁剪成 superType 类型
 */
fun ClientType.clipToSuperType(): ClientType {
  return copy(
    superTypes = superTypes.map { it.clipToSuperType() },
    properties = emptyList(),
    argumentLocations = emptyList(),
    typeKind = null
  )
}
