package net.yan100.compose.ksp.client

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import net.yan100.compose.ksp.toolkit.*
import net.yan100.compose.meta.client.ClientProp
import net.yan100.compose.meta.client.ClientType

class PropertyHandler(
  private val resolver: Resolver,
  private val classDeclarations: List<KSType>,
  private val log: KSPLogger? = null
) {
  private val results: MutableMap<String, ClientType> = mutableMapOf()
  private var superTypeIgnoreInterceptor = { it: String ->
    when (it) {
      "kotlin.Any", "kotlin.io.Serializable", // 1
      "kotlin.Number",// 1
      "kotlin.Enum", // 其枚举类型必然是本身
        -> true

      else -> false
    }
  }

  private var ignoreInterceptor = { it: String ->
    when (it) {
      "kotlin.Any", "kotlin.io.Serializable", // 1
      "kotlin.collections.Iterable", "kotlin.collections.Collection", // 1
      "kotlin.collections.List", "kotlin.collections.Set",// 1
      "kotlin.collections.Map", "kotlin.Number",// 1
      "kotlin.Int", "kotlin.Long",// 1
      "kotlin.Unit", "kotlin.Boolean",// 1
      "kotlin.String", "kotlin.Comparable",// 1
      "kotlin.Enum", "kotlin.CharSequence" // 1
        -> true

      else -> false
    }
  }
  private var typeNameInterceptor = { it: String ->
    when (it) {
      "java.lang.Enum" -> "kotlin.Enum"
      "java.io.Serializable" -> "kotlin.io.Serializable"
      "java.lang.Object" -> "kotlin.Any"
      "java.lang.String" -> "kotlin.String"
      else -> it
    }
  }

  fun getCopyClientTypeToReturnType(typeName: String): ClientType? {
    val name = typeNameInterceptor(typeName)
    return results[name]?.copy(
      superTypes = mutableListOf(),
      isAlias = null,
      aliasForTypeName = null,
      typeKind = null,
      properties = mutableListOf(),
      enumConstants = mutableMapOf(),
      argumentLocations = mutableListOf()
    )
  }

  fun handleTypeNameInterceptor(interceptor: (String) -> String) {
    typeNameInterceptor = interceptor
  }

  fun ignoreNameInterceptor(interceptor: (String) -> Boolean) {
    ignoreInterceptor = interceptor
  }

  fun getAllClientTypes(): List<ClientType> {
    results.clear()
    classDeclarations.forEach {
      when (it.declaration) {
        is KSClassDeclaration -> handleClassDeclaration(it.declaration)
        is KSTypeAlias -> handleClassDeclaration(it.declaration)
      }
    }
    return results.values
      .map {
        it.copy(
          superTypes = it.superTypes.mapNotNull { r ->
            results[typeNameInterceptor(r.typeName)]?.clipToSuperType()
          }.filterNot { r -> superTypeIgnoreInterceptor(r.typeName) }
        )
      }
      .filterNot { ignoreInterceptor(it.typeName) }
  }

  private fun handlePropertyDeclaration(propertyDeclaration: KSPropertyDeclaration): ClientProp {
    val type = propertyDeclaration.type.fastResolve().declaration
    val name = typeNameInterceptor(type.qualifiedNameAsString!!)
    if (!results.containsKey(name)) {
      when (type) {
        is KSClassDeclaration,
        is KSTypeAlias -> handleClassDeclaration(type)
      }
    }
    return propertyDeclaration.toClientProp()
  }

  private fun handleClassDeclaration(declaration: KSDeclaration) {
    val typeName = typeNameInterceptor(declaration.qualifiedNameAsString!!)
    if (!results.containsKey(typeName)) {
      if (declaration is KSTypeAlias) {
        val type = declaration.toClientType()
        handleClassDeclaration(declaration.realDeclaration)
        results += typeName to type.copy(
          aliasForTypeName = typeNameInterceptor(type.aliasForTypeName!!),
          inputGenerics = declaration.type.fastResolve().arguments.toInputGenericTypeList()
        )
        return
      }

      if (declaration !is KSClassDeclaration) error("$declaration  not class")
      val e = declaration.getAllProperties().map {
        handlePropertyDeclaration(it)
      }.map {
        it.copy(typeName = typeNameInterceptor(it.typeName))
      }

      val clientType = declaration.toClientType(log).run {
        if (declaration.classKind != ClassKind.ENUM_CLASS) {
          superTypes.forEach {
            if (!results.containsKey(it.typeName)) {
              handleClassDeclaration(resolver.getClassDeclarationByRuntimeName(it.typeName)!!)
            }
          }
          copy(properties = properties + e)
        } else this
      }

      results += typeName to clientType.copy(
        typeName = typeNameInterceptor(clientType.typeName),
        superTypes = clientType.superTypes
      )
    } else {
      results[typeName]!!
    }
  }
}
