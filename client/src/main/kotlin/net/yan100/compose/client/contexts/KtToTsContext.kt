package net.yan100.compose.client.contexts

import net.yan100.compose.client.domain.TsGeneric
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.TsTypeProperty
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.interceptors.Interceptor
import net.yan100.compose.client.interceptors.KotlinToTypescriptInterceptor
import net.yan100.compose.client.interceptors.TsPreReferenceInterceptor
import net.yan100.compose.client.isGenericName
import net.yan100.compose.client.toTsStyleName
import net.yan100.compose.meta.client.ClientApiStubs
import net.yan100.compose.meta.client.ClientInputGenericType
import net.yan100.compose.meta.client.ClientType

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
    val tsTypeValInterceptors = interceptorChain.filterIsInstance<TsPreReferenceInterceptor>()
    val toTsInterceptors = interceptorChain.filterIsInstance<KotlinToTypescriptInterceptor>()
    if (definitions.isEmpty()) {
      error("context definitions is not init")
    }

    val allTypeVal = definitions.map { type ->
      val process = tsTypeValInterceptors.firstOrNull { it.supported(this, type) }
      type to process?.run {
        TsScope.TypeVal(
          definition = process(this@KtToTsContext, type),
          meta = type
        )
      }
    }

    val (supportedTypeVals, unsupportedTypeVals) = allTypeVal.partition { it.second != null }
    internalTsScopes = supportedTypeVals.map { (_, typeVal) ->
      typeVal!!
    }.toMutableList()

    internalTsScopesMap = internalTsScopes.associateBy { it.meta!!.typeName }.toMutableMap()
    val (notHandled, h) =
      (supportedTypeVals + unsupportedTypeVals)
        .partition { (_, def) ->
          if (def == null) return@partition true
          if (def.definition !is TsTypeVal.TypeDef) return@partition false
          true
        }

    val all = notHandled.map { (type, prev) ->
      val process = toTsInterceptors.firstOrNull { it.supported(this, type) }
      if (prev == null) {
        type to process?.process(this, type)!!
      } else type to (process?.process(this, type) ?: prev)
    }

    val r = (all.map {
      it.second
    } + h.map { it.second!! })
    internalTsScopes = r.toMutableList()
    internalTsScopesMap = internalTsScopes.associateBy { it.meta!!.typeName }.toMutableMap()
  }

  fun getTsGenericByClientGenerics(
    clientGenerics: List<ClientInputGenericType>
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
              usedGenerics = getTsGenericByClientGenerics(usedGeneric.inputGenerics)
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
      val def = resolveTsTypeValByClientType(thatPropType)
      if (def !is TsTypeVal.TypeDef) {
        return@map TsTypeProperty(
          name = clientProp.name.toTsStyleName(),
          defined = def,
          partial = clientProp.nullable == true
        )
      }
      TsTypeProperty(
        name = clientProp.name.toTsStyleName(),
        defined = def.copy(usedGenerics = getTsGenericByClientGenerics(clientProp.inputGenerics))
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
    return resolveTsTypeValByClientType(clientType)
  }

  fun resolveTsTypeValByClientType(
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
