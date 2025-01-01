package net.yan100.compose.client.interceptors.ts

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsGeneric
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.domain.entries.TsName
import net.yan100.compose.client.interceptors.TsScopeInterceptor
import net.yan100.compose.client.toTsName
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.types.TypeKind

open class TsStaticInterceptor : TsScopeInterceptor() {
  override val executeStage: ExecuteStage = ExecuteStage.RESOLVE_TS_SCOPE

  private val supportedKinds = listOf(
    TypeKind.INTERFACE,
    TypeKind.CLASS,
    TypeKind.EMBEDDABLE,
    TypeKind.IMMUTABLE
  )

  override fun supported(ctx: KtToTsContext, source: ClientType): Boolean = source.typeKind in supportedKinds

  override fun process(ctx: KtToTsContext, source: ClientType): TsScope<*> {
    val prevScope = ctx.getTsScopeByType(source)
    val name = if (prevScope is TsScope.TypeVal) {
      prevScope.definition.toTsName()
    } else prevScope.name

    val properties = ctx.getTsTypePropertyByType(source)
    // 如果 没有属性，则处理为 type xxx = object
    if (properties.isEmpty()) {
      return TsScope.TypeVal(
        definition = TsTypeVal.EmptyObject,
        meta = source
      )
    }

    val generics = source.arguments.mapIndexed { i, it ->
      TsGeneric.Defined(
        name = TsName.Generic(it),
        index = i
      )
    }

    val superTypes = ctx.getSuperTypeRefs(source)

    return TsScope.Interface(
      meta = source,
      name = name,
      superTypes = superTypes,
      generics = generics,
      properties = properties
    )
  }
}
