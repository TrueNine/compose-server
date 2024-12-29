package net.yan100.compose.client.interceptors.ts

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsGeneric
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.entries.TsName
import net.yan100.compose.client.interceptors.TsScopeInterceptor
import net.yan100.compose.client.toTsStylePathName
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.types.TypeKind

class TsTypeAliasInterceptor : TsScopeInterceptor() {
  override val executeStage: ExecuteStage = ExecuteStage.RESOLVE_TS_SCOPE
  override fun supported(ctx: KtToTsContext, source: ClientType): Boolean = source.typeKind == TypeKind.TYPEALIAS
  override fun process(ctx: KtToTsContext, source: ClientType): TsScope<*> {
    val generics = source.arguments.mapIndexed { i, it ->
      TsGeneric.Defined(
        name = TsName.Name(it),
        index = i
      )
    }
    val name = ctx.getTypeNameByName(source.typeName)
    val useGenerics = ctx.getTsGenericByGenerics(source.usedGenerics)
    val aliasFor = ctx.getTsTypeValByName(source.aliasForTypeName!!)
    return TsScope.TypeAlias(
      name = name.toTsStylePathName(),
      aliasFor = aliasFor,
      meta = source,
      generics = generics,
      usedGenerics = useGenerics
    )
  }
}
