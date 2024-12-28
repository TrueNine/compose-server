package net.yan100.compose.client.interceptors.ts

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.interceptors.TsScopeInterceptor
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.types.TypeKind

class TsJimmerInterceptor : TsScopeInterceptor() {
  override val executeStage: ExecuteStage = ExecuteStage.LOOP_RESOLVE_TS_REFERENCES

  private val jimmerTypeKinds = setOf(
    TypeKind.EMBEDDABLE,
    TypeKind.IMMUTABLE
  )

  override fun supported(ctx: KtToTsContext, source: ClientType): Boolean {
    return source.typeKind in jimmerTypeKinds
  }

  override fun process(ctx: KtToTsContext, source: ClientType): TsScope {
    return TsScope.TypeVal(TsTypeVal.Unknown, meta = source)
  }
}
