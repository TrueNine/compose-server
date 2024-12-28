package net.yan100.compose.client.interceptors.ts

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.interceptors.KotlinToTypescriptInterceptor
import net.yan100.compose.client.toTypescriptEnum
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.types.TypeKind

class TsEnumInterceptor : KotlinToTypescriptInterceptor() {
  override val executeStage: ExecuteStage = ExecuteStage.LOOP_RESOLVE_TS_REFERENCES
  override fun supported(ctx: KtToTsContext, source: ClientType): Boolean {
    return source.typeKind == TypeKind.ENUM_CLASS
  }

  override fun process(ctx: KtToTsContext, source: ClientType): TsScope {
    return source.toTypescriptEnum()
  }
}
