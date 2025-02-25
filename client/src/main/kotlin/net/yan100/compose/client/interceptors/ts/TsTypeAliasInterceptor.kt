package net.yan100.compose.client.interceptors.ts

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsGeneric
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.entries.TsName
import net.yan100.compose.client.interceptors.TsScopeInterceptor
import net.yan100.compose.client.toTsName
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.types.TypeKind

class TsTypeAliasInterceptor : TsScopeInterceptor() {
  override val executeStage: ExecuteStage = ExecuteStage.RESOLVE_TS_SCOPE

  override fun supported(ctx: KtToTsContext, source: ClientType): Boolean =
    source.typeKind == TypeKind.TYPEALIAS

  override fun process(ctx: KtToTsContext, source: ClientType): TsScope<*> {
    val prevScope = ctx.getTsScopeByType(source)
    val name =
      if (prevScope is TsScope.TypeVal) {
        prevScope.definition.toTsName()
      } else prevScope.name

    val generics =
      source.arguments.mapIndexed { i, it ->
        TsGeneric.Defined(name = TsName.Name(it), index = i)
      }

    val aliasFor = ctx.getTsTypeValByName(source.aliasForTypeName!!)
    val aliasUses =
      if (source.usedGenerics.isEmpty()) emptyList()
      else ctx.getTsGenericByGenerics(source.usedGenerics)

    return TsScope.TypeAlias(
      name = name,
      aliasFor = aliasFor.fillGenerics(aliasUses),
      meta = source,
      generics = generics,
      usedGenerics = aliasUses,
    )
  }
}
