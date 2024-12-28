package net.yan100.compose.client.interceptors.ts

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.interceptors.KotlinToTypescriptInterceptor
import net.yan100.compose.meta.client.ClientType

class TsBuiltinInterceptor : KotlinToTypescriptInterceptor() {
  override val executeStage: ExecuteStage = ExecuteStage.RESOLVE_TS_SCOPE
  private val nameTable = mapOf<String, TsScope>()

  override fun supported(ctx: KtToTsContext, source: ClientType): Boolean = source.typeName in nameTable
  override fun process(ctx: KtToTsContext, source: ClientType): TsScope = nameTable[source.typeName]!!
}
