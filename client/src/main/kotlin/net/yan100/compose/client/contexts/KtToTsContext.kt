package net.yan100.compose.client.contexts

import net.yan100.compose.client.*
import net.yan100.compose.client.domain.TsGeneric
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.domain.TsUseVal
import net.yan100.compose.client.domain.entries.TsName
import net.yan100.compose.client.interceptors.*
import net.yan100.compose.meta.client.ClientApiStubs
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.client.ClientUsedGeneric
import net.yan100.compose.meta.types.TypeKind

open class KtToTsContext(
  stub: ClientApiStubs,
  vararg tsInterceptorChains: Interceptor<*, *, *> = arrayOf()
) : KtToKtContext(stub, *tsInterceptorChains) {
  override var currentStage: ExecuteStage = ExecuteStage.RESOLVE_OPERATIONS
  private val internalTsScopes: MutableList<TsScope<*>> = mutableListOf()
  private val internalTsScopesMap: MutableMap<String, TsScope<*>> = mutableMapOf()
  private var internalTsScopesCircularCount = 0
  val tsScopes: List<TsScope<*>>
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
        dispatch()
        internalTsScopes
      }
    }
  private var internalTsScopesMapCircularCount = 0
  val tsScopesMap: Map<String, TsScope<*>>
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
        dispatch()
        internalTsScopesMap
      }
    }

  private fun clearAllTsScope() {
    internalTsScopes.clear()
    internalTsScopesMap.clear()
  }

  private fun addAllTsScopeByType(
    tsScopes: Map<ClientType, TsScope<*>>
  ): Map<ClientType, TsScope<*>> {
    addAllTsScope(tsScopes.mapKeys { it.key.typeName })
    return tsScopes
  }

  private fun addAllTsScope(
    tsScopes: Map<String, TsScope<*>>
  ): MutableMap<String, TsScope<*>> {
    tsScopes.forEach { (k, v) ->
      if (internalTsScopesMap.containsKey(k)) {
        internalTsScopesMap[k] = v
      } else internalTsScopesMap[k] = v
    }
    internalTsScopes.clear()
    internalTsScopes.addAll(internalTsScopesMap.values)
    return internalTsScopesMap
  }


  /**
   * 更新入口
   */
  private fun dispatch() {
    if (definitions.isEmpty()) error("context definitions is not init")
    clearAllTsScope()
    val tsPreReferenceInterceptors = interceptorChain.filterIsInstance<TsPreReferenceInterceptor>()
    check(tsPreReferenceInterceptors.isNotEmpty()) { "context interceptor chain is not init" }
    val allPreReferences = definitions.map { type ->
      val process = tsPreReferenceInterceptors.firstOrNull { it.supported(this, type) }
      type to process?.run {
        TsScope.TypeVal(
          definition = process(this@KtToTsContext, type),
          meta = type
        )
      }
    }
    val preReferenceMap = allPreReferences.partition { it.second != null }.let { (s, u) ->
      if (u.isNotEmpty()) {
        error("unsupported pre references, pre reference stage requires processing all references: ${u.map { it.first }}")
      } else s.associate { it.first to it.second!! }
    }
    addAllTsScope(preReferenceMap.mapKeys { it.key.typeName })
    val postReferenceMap = updatePostReference(preReferenceMap)
    val redirected = redirectAllScopes(postReferenceMap)
    val customScopeMap = updateCustomScopes(redirected)
    updatePostScopes(customScopeMap)
    processService()
  }

  /**
   * 处理 services
   */
  fun processService() {
    val r = clientServiceMap.mapKeys { (type) ->
      val typeName = type.typeName.toTsPathName()
      TsScope.Class(
        name = typeName.copy(path = "service/${typeName.path}"),
        meta = type
      )
    }.mapValues { (_, operations) ->
      operations.map { operation ->
        operation
      }
    }
    println(r)
  }

  private fun redirectAllScopes(supportedMap: Map<ClientType, TsScope<*>>, deep: Int = 0): Map<ClientType, TsScope<*>> {
    if (deep > 16) error("Circular dependency detected")
    if (supportedMap.isEmpty()) return supportedMap
    val processedMap = supportedMap.mapValues { (t, s) ->
      val kind = when (t.typeKind) {
        TypeKind.CLASS,
        TypeKind.OBJECT,
        TypeKind.INTERFACE -> "static"

        TypeKind.ENUM_CLASS -> "enums"

        TypeKind.IMMUTABLE -> "dynamic"
        TypeKind.EMBEDDABLE -> "embeddable"
        TypeKind.TYPEALIAS -> "typealias"

        TypeKind.ENUM_ENTRY,
        TypeKind.ANNOTATION_CLASS,
        TypeKind.TRANSIENT,
        null -> error("unsupported type kind: ${t.typeKind}")
      }
      if (s is TsScope.TypeVal && s.definition is TsTypeVal.Ref && s.definition.typeName is TsName.PathName) {
        val name = s.definition.typeName.copy(
          name = s.definition.typeName.name,
          path = "${kind}/${s.definition.typeName.path}"
        )
        s.copy(definition = s.definition.copy(typeName = name))
      } else if (s.name is TsName.PathName) {
        val name = (s.name as TsName.PathName)
        val asName = name.copy(
          name = name.name,
          path = "${kind}/${name.path}"
        )
        when (s) {
          is TsScope.Class -> s.copy(name = asName)
          is TsScope.Enum -> s.copy(name = asName)
          is TsScope.Interface -> s.copy(name = asName)
          is TsScope.TypeAlias -> s.copy(name = asName)
          is TsScope.TypeVal -> error("unsupported scope kind: ${s.definition}")
        }
      } else s
    }
    return addAllTsScopeByType(processedMap)
  }

  fun getSuperTypeRefs(source: ClientType): List<TsTypeVal.Ref> {
    return source.superTypes.mapNotNull { superType ->
      val r = getTsTypeValByType(superType)
      when {
        r is TsTypeVal.Ref -> {
          if (r.isBasic) r
          else r.copy(
            typeName = r.typeName,
            usedGenerics = superType.toTsGenericUsed { er ->
              if (er.typeName.isGenericName()) er.typeName.unwrapGenericName().toTsName()
              else getTsTypeValByName(er.typeName).toTsName()
            }
          )
        }

        r.isBasic -> null
        else -> null
      }
    }
  }

  /**
   * 后置处理 scope
   */
  private fun updatePostScopes(supportedMap: Map<ClientType, TsScope<*>>, deep: Int = 0): Map<ClientType, TsScope<*>> {
    if (deep > 16) error("Circular dependency detected")
    if (supportedMap.isEmpty()) return supportedMap
    val tsGenericsInterceptors = interceptorChain.filterIsInstance<TsPostScopeInterceptor>()
    if (tsGenericsInterceptors.isEmpty()) return supportedMap
    val (preparedProcessMap, basicMap) = supportedMap.entries.partition { (_, def) ->
      !def.isBasic
    }.let {
      it.first.associate { (k, v) -> k to v } to it.second.associate { (k, v) -> k to v }
    }
    val processedMap = preparedProcessMap.map { (type, def) ->
      val process = tsGenericsInterceptors.firstOrNull { it.supported(this, type to def) }
      if (process == null) return@map type to def
      type to process.run {
        process(this@KtToTsContext, type to def)
      }
    }.run { toMap() }
    val resultMap = addAllTsScopeByType((processedMap + basicMap))
    if (processedMap != preparedProcessMap) return updatePostScopes(resultMap, deep + 1)
    return resultMap
  }

  /**
   * 后置处理 reference
   */
  private fun updatePostReference(supportedMap: Map<ClientType, TsScope<*>>, deep: Int = 0): Map<ClientType, TsScope<*>> {
    if (deep > 16) error("Circular dependency detected")
    if (supportedMap.isEmpty()) return supportedMap
    val tsPostInterceptors = interceptorChain.filterIsInstance<TsPostReferenceInterceptor>()
    if (tsPostInterceptors.isEmpty()) return supportedMap
    val (basicMap, nextProcessMap) = supportedMap.entries.partition { (_, def) ->
      def is TsScope.TypeVal && def.definition.isBasic
    }.let {
      it.first.associate { (k, v) -> k to v } to it.second.associate { (k, v) -> k to v }
    }
    val processedPostRefs = nextProcessMap.map { (type, def) ->
      val process = tsPostInterceptors.firstOrNull { it.supported(this, type) }
      if (process == null) return@map type to def
      type to process.run {
        TsScope.TypeVal(
          definition = process(this@KtToTsContext, type),
          meta = type
        )
      }
    }.run { toMap().toMutableMap() }
    val all = (processedPostRefs + basicMap)
    addAllTsScopeByType(all)
    if (processedPostRefs != nextProcessMap) return updatePostReference(all, deep + 1)
    return all
  }

  /**
   * 环绕处理 scope
   */
  private fun updateCustomScopes(supportedPostReferences: Map<ClientType, TsScope<*>>): Map<ClientType, TsScope<*>> {
    val toTsInterceptors = interceptorChain.filterIsInstance<TsScopeInterceptor>()
    val (notHandledTypeDefinitions, basicScopes) = supportedPostReferences.entries.partition { (_, def) ->
      when (def) {
        is TsScope.Enum
          -> false

        is TsScope.TypeVal -> when (def.definition) {
          is TsTypeVal.Ref -> true
          else -> false
        }

        else -> false
      }
    }
    val linkedScopes = notHandledTypeDefinitions.associate { (type, prev) ->
      val process = toTsInterceptors.firstOrNull { it.supported(this, type) }
      type to (process?.process(this, type) ?: prev)
    }

    val processedResult = (linkedScopes + basicScopes.associate { (a, it) -> a to it })
    addAllTsScope(processedResult.mapKeys { it.key.typeName })
    return processedResult
  }

  fun getTsGenericByGenerics(
    clientGenerics: List<ClientUsedGeneric>
  ): List<TsGeneric> {
    if (clientGenerics.isEmpty()) return emptyList()
    return clientGenerics.map { usedGeneric ->
      if (usedGeneric.typeName.isGenericName()) {
        return@map TsGeneric.Used(
          used = TsTypeVal.Generic(TsGeneric.Defined(name = usedGeneric.typeName.toTsName())),
          index = usedGeneric.index
        )
      }
      val usedResult = getTsTypeValByName(usedGeneric.typeName).let { g ->
        when (g) {
          is TsTypeVal.Ref -> {
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

  fun getTsTypePropertyByType(
    clientType: ClientType
  ): List<TsUseVal.Prop> {
    if (clientType.properties.isEmpty()) return emptyList()
    val r = getTypeByType(clientType) ?: return emptyList()
    return r.properties.map { clientProp ->
      if (clientProp.typeName.isGenericName()) {
        return@map TsUseVal.Prop(
          name = clientProp.name.toTsName(),
          typeVal = TsTypeVal.Generic(TsGeneric.Defined(name = clientProp.typeName.toTsName())),
          partial = clientProp.nullable == true
        )
      }
      val thatPropType = this.getTypeByName(clientProp.typeName)!!
      val def = getTsTypeValByType(thatPropType)
      if (def !is TsTypeVal.Ref) {
        return@map TsUseVal.Prop(
          name = clientProp.name.toTsName(),
          typeVal = def,
          partial = clientProp.nullable == true
        )
      }
      TsUseVal.Prop(
        name = clientProp.name.toTsName(),
        typeVal = def.copy(usedGenerics = getTsGenericByGenerics(clientProp.usedGenerics))
      )
    }
  }

  fun getTsScopeByName(
    typeName: String
  ): TsScope<*> {
    val clientType = getTypeByName(typeName) ?: error("$typeName is not found")
    return getTsScopeByType(clientType)
  }

  fun getTsTypeValByName(
    typeName: String
  ): TsTypeVal<*> {
    val clientType = this.getTypeByName(typeName) ?: error("$typeName is not found")
    return getTsTypeValByType(clientType)
  }

  fun getTsTypeValByType(
    clientType: ClientType,
  ): TsTypeVal<*> {
    val name = getTypeNameByName(clientType.typeName)
    return tsScopesMap[name]?.toTsTypeVal() ?: error("$name is not found")
  }

  fun getTsScopeByType(
    clientType: ClientType
  ): TsScope<*> {
    val tsScope = getTsScopesByTypes(listOf(clientType)).first()
    return tsScope
  }

  fun getTsScopesByTypes(clientTypes: List<ClientType>): List<TsScope<*>> {
    return clientTypes.map {
      val name = getTypeNameByName(it.typeName)
      internalTsScopesMap[name] ?: error("$name is not found")
    }
  }
}
