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
  /**
   * 已装载的存根
   */
  private val stub = stub.copy()

  /**
   * 已装载的服务端点
   */
  val clientServiceMap = stub.services.groupBy({ it.toClientType() }) { it.operations }
    .mapValues { (_, v) -> v.flatten() }
    .mapValues { (clientType, operations) ->
      operations.map { operation ->
        val requestInfo = operation.requestInfo ?: error("RequestInfo is null class: ${clientType.typeName}#${operation.name}")
        if (requestInfo.supportedMethods.size > 1) {
          requestInfo.supportedMethods.map { method ->
            operation.copy(
              requestInfo = requestInfo.copy(supportedMethods = listOf(method)),
              name = operation.name + "For${method}"
            )
          }
        } else listOf(operation)
      }.flatten()
    }

  private val internalDefinitionsMap: MutableMap<String, ClientType> = mutableMapOf()
  private val internalDefinitions: MutableList<ClientType> = mutableListOf()
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
        dispatch()
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
        dispatch()
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

  private fun convertAllPropertyName(clientProps: List<ClientProp>): List<ClientProp> = clientProps.map {
    if (it.typeName.isGenericName()) return@map it
    it.copy(
      typeName = getTypeNameByName(it.typeName)
    )
  }


  private fun dispatch(clientTypes: List<ClientType> = stub.definitions, deepCount: Int = 0): Map<String, ClientType> {
    if (deepCount > 16) error("Circular reference detected in updateDefinitions")
    if (clientTypes.isEmpty()) return emptyMap()
    val nameConvertedTypes = clientTypes.map { clientType ->
      clientType.copy(
        typeName = getTypeNameByName(clientType.typeName),
        superTypes = convertSuperTypes(clientType),
        aliasForTypeName = clientType.aliasForTypeName?.let { n -> getTypeNameByName(n) },
        usedGenerics = convertAllUsedGenerics(clientType.usedGenerics),
        properties = convertAllPropertyName(clientType.properties)
      )
    }
    if (!clientTypes.containsAll(nameConvertedTypes)) internalDefinitionsMap.putAll(dispatch(nameConvertedTypes, deepCount + 1))
    internalDefinitionsMap.putAll(nameConvertedTypes.associateBy { it.typeName })
    internalDefinitions.clear()
    internalDefinitions.addAll(internalDefinitionsMap.values)
    return internalDefinitionsMap
  }

  private fun convertSuperTypes(clientType: ClientType): List<ClientType> {
    return if (clientType.superTypes.isEmpty()) emptyList()
    else {
      clientType.superTypes.map { superType ->
        superType.copy(
          typeName = getTypeNameByName(superType.typeName),
          superTypes = convertSuperTypes(superType)
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
    dispatch(stub.definitions + listOf(type))
    return this
  }

  override var currentStage: ExecuteStage = ExecuteStage.LOOP_RESOLVE_CLASS
}
