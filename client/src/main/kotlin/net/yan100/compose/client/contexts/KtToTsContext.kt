package net.yan100.compose.client.contexts

import net.yan100.compose.client.domain.TsGeneric
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.TsTypeProperty
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.interceptors.Interceptor
import net.yan100.compose.client.interceptors.TsPreReferenceInterceptor
import net.yan100.compose.client.interceptors.TsScopeInterceptor
import net.yan100.compose.client.isGenericName
import net.yan100.compose.client.toTsStyleName
import net.yan100.compose.meta.client.ClientApiStubs
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.client.ClientUsedGeneric

open class KtToTsContext(
  stub: ClientApiStubs,
  vararg tsInterceptorChains: Interceptor<*, *, *> = arrayOf()
) : KtToKtContext(stub, *tsInterceptorChains) {
  override var currentStage: ExecuteStage = ExecuteStage.RESOLVE_OPERATIONS

  private var internalTsScopes: MutableList<TsScope> = mutableListOf()
  private var internalTsScopesMap: MutableMap<String, TsScope> = mutableMapOf()
  private var internalTsScopesCircularCount = 0
  val tsScopes: List<TsScope>
    get() {
      if (internalTsScopesCircularCount > 63) {
        internalTsScopesCircularCount = 0
        error("Circular dependency detected")
      }
      return if (internalTsScopes.isNotEmpty()) {
        internalTsScopesCircularCount = 0
        internalTsScopes
      } else {
        internalTsScopesCircularCount += 1
        updateTsScopes()
        internalTsScopes
      }
    }
  private var internalTsScopesMapCircularCount = 0
  val tsScopesMap: Map<String, TsScope>
    get() {
      if (internalTsScopesMapCircularCount > 16) {
        internalTsScopesMapCircularCount = 0
        error("Circular dependency detected")
      }
      return if (internalTsScopesMap.isNotEmpty()) {
        internalTsScopesMapCircularCount = 0
        internalTsScopesMap
      } else {
        internalTsScopesMapCircularCount += 1
        updateTsScopes()
        internalTsScopesMap
      }
    }


  private fun updateTsScopes() {
    if (definitions.isEmpty()) error("context definitions is not init")
    val tsPreReferenceInterceptors = interceptorChain.filterIsInstance<TsPreReferenceInterceptor>()
    val tsPostReferenceInterceptors = interceptorChain.filterIsInstance<TsPreReferenceInterceptor>()
    val allPreReferences = definitions.map { type ->
      val process = tsPreReferenceInterceptors.firstOrNull { it.supported(this, type) }
      type to process?.run {
        TsScope.TypeVal(
          definition = process(this@KtToTsContext, type),
          meta = type
        )
      }
    }

    val (supportedPreReferences, unsupportedPreReferences) = allPreReferences.partition { it.second != null }
    if (unsupportedPreReferences.isNotEmpty()) {
      error("unsupported pre references, pre reference stage requires processing all references: ${unsupportedPreReferences.map { it.first }}")
    }
    internalTsScopesMap = supportedPreReferences.associateBy { (_, it) -> it!!.meta!!.typeName }.mapValues { (_, v) -> v.second!! }.toMutableMap()
    internalTsScopes = internalTsScopesMap.values.toMutableList()

    val allPostReferences = definitions.map { type ->
      val process = tsPostReferenceInterceptors.firstOrNull { it.supported(this, type) }
      type to process?.run {
        TsScope.TypeVal(
          definition = process(this@KtToTsContext, type),
          meta = type
        )
      }
    }
    val (supportedPostReferences, unsupportedPostReferences) = allPostReferences.partition { it.second != null }
    if (unsupportedPostReferences.isNotEmpty()) {
      error("unsupported post references, post reference stage requires processing all references: ${unsupportedPostReferences.map { it.first }}")
    }
    internalTsScopesMap += supportedPostReferences.associateBy { (_, it) -> it!!.meta!!.typeName }.mapValues { (_, v) -> v.second!! }.toMutableMap()
    internalTsScopes = internalTsScopesMap.values.toMutableList()
    updateCustomScopes(internalTsScopesMap.mapKeys { (k) -> getTypeByName(k)!! })
  }

  private fun updateCustomScopes(supportedPostReferences: Map<ClientType, TsScope?>) {
    val toTsInterceptors = interceptorChain.filterIsInstance<TsScopeInterceptor>()
    val (notHandledTypeDefinitions, basicScopes) = supportedPostReferences.entries.partition { (_, def) ->
      when (def) {
        is TsScope.Enum, null
          -> false

        is TsScope.TypeVal -> when (def.definition) {
          is TsTypeVal.TypeDef -> true
          else -> false
        }

        else -> false
      }
    }
    val linkedScopes = notHandledTypeDefinitions.associate { (type, prev) ->
      val process = toTsInterceptors.firstOrNull { it.supported(this, type) }
      type to (process?.process(this, type) ?: prev!!)
    }

    val processedResult = (linkedScopes + basicScopes.associate { (a, it) -> a to it!! })
    internalTsScopesMap += processedResult.mapKeys { it.key.typeName }
    internalTsScopes = internalTsScopesMap.values.toMutableList()
  }

  fun getTsGenericByGenerics(
    clientGenerics: List<ClientUsedGeneric>
  ): List<TsGeneric> {
    if (clientGenerics.isEmpty()) return emptyList()
    return clientGenerics.map { usedGeneric ->
      if (usedGeneric.typeName.isGenericName()) {
        return@map TsGeneric.Used(
          used = TsTypeVal.Generic(usedGeneric.typeName),
          index = usedGeneric.index
        )
      }
      val usedResult = resolveTsTypeValByClientTypeTypeName(usedGeneric.typeName).let { g ->
        when (g) {
          is TsTypeVal.TypeDef -> {
            g.copy(
              typeName = g.typeName,
              usedGenerics = getTsGenericByGenerics(usedGeneric.usedGenerics)
            )
          }

          else -> g
        }
      }
      TsGeneric.Used(
        used = usedResult,
        index = usedGeneric.index
      )
    }
  }

  fun getClientPropsByClientType(
    clientType: ClientType
  ): List<TsTypeProperty> {
    if (clientType.properties.isEmpty()) return emptyList()
    val r = getTypeByType(clientType) ?: return emptyList()
    return r.properties.map { clientProp ->
      if (clientProp.typeName.isGenericName()) {
        return@map TsTypeProperty(
          name = clientProp.name.toTsStyleName(),
          defined = TsTypeVal.Generic(clientProp.typeName),
          partial = clientProp.nullable == true
        )
      }
      val thatPropType = this.getTypeByName(clientProp.typeName)!!
      val def = getTsTypeValByType(thatPropType)
      if (def !is TsTypeVal.TypeDef) {
        return@map TsTypeProperty(
          name = clientProp.name.toTsStyleName(),
          defined = def,
          partial = clientProp.nullable == true
        )
      }
      TsTypeProperty(
        name = clientProp.name.toTsStyleName(),
        defined = def.copy(usedGenerics = getTsGenericByGenerics(clientProp.inputGenerics))
      )
    }
  }


  fun resolveTsScopeByClientTypeTypeName(
    typeName: String
  ): TsScope {
    val clientType = this.getTypeByName(typeName) ?: return TsScope.TypeVal(TsTypeVal.Never)
    return resolveTsScopeByClientType(clientType)
  }

  fun resolveTsTypeValByClientTypeTypeName(
    typeName: String
  ): TsTypeVal {
    val clientType = this.getTypeByName(typeName) ?: error("$typeName is not found")
    return getTsTypeValByType(clientType)
  }

  fun getTsTypeValByType(
    clientType: ClientType,
  ): TsTypeVal {
    val name = getTypeNameByName(clientType.typeName)
    return tsScopesMap[name]?.toTsTypeVal() ?: error("$name is not found")
  }

  fun resolveTsScopeByClientType(
    clientType: ClientType
  ): TsScope {
    val tsScope = resolveTsScopesByClientTypes(listOf(clientType)).first()
    return tsScope
  }

  fun resolveTsScopesByClientTypes(clientTypes: List<ClientType>): List<TsScope> {
    return clientTypes.map {
      val name = getTypeNameByName(it.typeName)
      tsScopesMap[name] ?: error("$name is not found")
    }
  }
}
