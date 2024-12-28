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

  fun getCopyClientTypeToReturnType(typeName: String): ClientType? {
    return results[typeName]?.copy(
      superTypes = mutableListOf(),
      isAlias = null,
      aliasForTypeName = null,
      typeKind = null,
      properties = mutableListOf(),
      enumConstants = mutableMapOf(),
      arguments = mutableListOf()
    )
  }

  fun getAllClientTypes(): List<ClientType> {
    results.clear()
    classDeclarations.forEach {
      when (it.declaration) {
        is KSClassDeclaration -> handleClassDeclaration(it.declaration)
        is KSTypeAlias -> handleClassDeclaration(it.declaration)
      }
    }
    return results.values.map { it.copy(superTypes = cleanSuperTypes(it.superTypes)) }
  }

  private fun cleanSuperTypes(superTypes: List<ClientType>): List<ClientType> {
    return superTypes.mapNotNull { r ->
      val handle = results[r.typeName]?.clipToSuperType()
      if (handle == null) null
      else r to handle
    }
      .map { (i, r) ->
        r.copy(
          superTypes = cleanSuperTypes(r.superTypes),
          usedGenerics = i.usedGenerics
        )
      }
  }

  private fun handlePropertyDeclaration(propertyDeclaration: KSPropertyDeclaration): ClientProp {
    val type = propertyDeclaration.type.fastResolve()
    val name = type.declaration.qualifiedNameAsString!!
    if (!results.containsKey(name)) {
      when (val d = type.declaration) {
        is KSClassDeclaration,
        is KSTypeAlias -> {
          handleClassDeclaration(d)
        }
      }
    }
    type.arguments.toDeclarations().forEach {
      when (it) {
        is KSClassDeclaration,
        is KSTypeAlias -> {
          handleClassDeclaration(it)
        }
      }
    }
    return propertyDeclaration.toClientProp()
  }

  private fun handleClassDeclaration(declaration: KSDeclaration) {
    val typeName = declaration.qualifiedNameAsString!!
    if (!results.containsKey(typeName)) {
      if (declaration is KSTypeAlias) {
        val type = declaration.toClientType()
        handleClassDeclaration(declaration.realDeclaration)
        results += typeName to type.copy(
          aliasForTypeName = type.aliasForTypeName!!,
          usedGenerics = declaration.type.fastResolve().arguments.toInputGenericTypeList(),
        )
        return
      }

      if (declaration !is KSClassDeclaration) error("$declaration  not class")
      val e = declaration.getAllProperties().map {
        handlePropertyDeclaration(it)
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
        typeName = clientType.typeName,
        superTypes = clientType.superTypes
      )
    } else {
      results[typeName]!!
    }
  }
}
