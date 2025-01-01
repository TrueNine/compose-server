package net.yan100.compose.client.contexts

import net.yan100.compose.client.interceptors.Interceptor
import net.yan100.compose.client.interceptors.QualifierNameInterceptor
import net.yan100.compose.client.isGenericName
import net.yan100.compose.meta.client.ClientApiStubs
import net.yan100.compose.meta.client.ClientProp
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.client.ClientUsedGeneric


open class KtToKtContext(
  stub: ClientApiStubs,
  vararg interceptorChain: Interceptor<*, *, *> = arrayOf()
) : StubContext<KtToKtContext>(interceptorChain.toMutableList()) {
  private val stub = stub.copy()
  private var internalDefinitions: MutableList<ClientType> = mutableListOf()
  private var internalDefinitionsMap: MutableMap<String, ClientType> = mutableMapOf()
  private var getDefinitionCircularCount = 0
  val definitions: List<ClientType>
    get() {
      if (getDefinitionCircularCount > 16) {
        getDefinitionCircularCount = 0
        error("Circular reference detected in definitions")
      }
      return if (internalDefinitions.isNotEmpty()) {
        getDefinitionCircularCount = 0
        internalDefinitions
      } else {
        getDefinitionCircularCount += 1
        updateDefinitions()
        internalDefinitions
      }
    }
  private var internalDefinitionsMapCircularCount = 0
  val definitionsMap: Map<String, ClientType>
    get() {
      if (internalDefinitionsMapCircularCount > 16) {
        internalDefinitionsMapCircularCount = 0
        error("Circular reference detected in definitionsMap")
      }
      return if (internalDefinitionsMap.isNotEmpty()) {
        internalDefinitionsMapCircularCount = 0
        internalDefinitionsMap
      } else {
        internalDefinitionsMapCircularCount += 1
        updateDefinitions()
        internalDefinitionsMap
      }
    }

  private fun convertAllUsedGenerics(clientInputGenerics: List<ClientUsedGeneric>): List<ClientUsedGeneric> {
    return clientInputGenerics.map {
      if (it.typeName.isGenericName()) return@map it
      it.copy(
        typeName = getTypeNameByName(it.typeName),
        usedGenerics = convertAllUsedGenerics(it.usedGenerics)
      )
    }
  }

  private fun convertAllPropertyName(clientProps: List<ClientProp>): List<ClientProp> {
    return clientProps.map {
      if (it.typeName.isGenericName()) return@map it
      it.copy(
        typeName = getTypeNameByName(it.typeName)
      )
    }
  }

  private fun updateDefinitions(clientTypes: List<ClientType> = stub.definitions, deepCount: Int = 0) {
    if (deepCount > 63) error("Circular reference detected in updateDefinitions")
    internalDefinitions = mutableListOf()
    internalDefinitionsMap = mutableMapOf()
    val convertedName = clientTypes.map {
      it.copy(
        typeName = getTypeNameByName(it.typeName),
        superTypes = convertSuperTypes(it),
        aliasForTypeName = it.aliasForTypeName?.let { n -> getTypeNameByName(n) },
        usedGenerics = convertAllUsedGenerics(it.usedGenerics),
        properties = convertAllPropertyName(it.properties)
      )
    }
    if (!clientTypes.containsAll(convertedName)) {
      updateDefinitions(convertedName, deepCount + 1)
    }
    internalDefinitions = convertedName.toMutableList()
    internalDefinitionsMap = internalDefinitions.associateBy { it.typeName }.toMutableMap()
  }

  private fun convertSuperTypes(clientType: ClientType): List<ClientType> {
    return if (clientType.superTypes.isEmpty()) emptyList()
    else {
      clientType.superTypes.map {
        it.copy(
          typeName = getTypeNameByName(it.typeName),
          superTypes = convertSuperTypes(it)
        )
      }
    }
  }

  private val typeNameToNameCache = mutableMapOf<String, String>()
  fun getTypeNameByName(typeName: String): String {
    if (typeNameToNameCache.containsKey(typeName)) return typeNameToNameCache[typeName]!!
    if (internalDefinitionsMap.containsKey(typeName) && internalDefinitionsMap.isNotEmpty()) return internalDefinitionsMap[typeName]!!.typeName
    val chains = interceptorChain.filterIsInstance<QualifierNameInterceptor>()
    if (chains.isEmpty()) error("Not a qualifier name interceptor was found, which may not have been expected")
    val processedName = chains.processOrNull(this, typeName)
    return if (null != processedName) {
      typeNameToNameCache[typeName] = processedName
      processedName
    } else typeName
  }

  fun getTypeByType(
    clientType: ClientType
  ): ClientType? {
    if (clientType.typeName.isBlank()) return null
    if (clientType.typeName.isGenericName()) return null
    return this.getTypeByName(clientType.typeName)
  }

  override fun getAllTypes(): List<ClientType> = internalDefinitions
  override fun getTypeByName(typeName: String): ClientType? {
    if (typeName.isBlank()) return null
    if (typeName.isGenericName()) return null
    val usedTypeName = getTypeNameByName(typeName)
    return definitionsMap[usedTypeName]
  }

  override fun addType(type: ClientType): KtToKtContext {
    updateDefinitions(
      stub.definitions + listOf(type)
    )
    return this
  }

  override var currentStage: ExecuteStage = ExecuteStage.LOOP_RESOLVE_CLASS
}
