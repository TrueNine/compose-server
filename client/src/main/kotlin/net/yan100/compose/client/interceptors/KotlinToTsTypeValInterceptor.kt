package net.yan100.compose.client.interceptors

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.meta.client.ClientType

abstract class KotlinToTsTypeValInterceptor : Interceptor<ClientType, TsTypeVal, KtToTsContext> {
  override val executeStage: ExecuteStage = ExecuteStage.RESOLVE_SERVICE
  override fun defaultProcess(ctx: KtToTsContext, source: ClientType): TsTypeVal = TsTypeVal.Never
}