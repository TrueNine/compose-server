package net.yan100.compose.client.interceptors

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.meta.client.ClientType

abstract class TsPostScopeInterceptor :
  Interceptor<Pair<ClientType, TsScope<*>>, TsScope<*>, KtToTsContext> {
  override val executeStage: ExecuteStage = ExecuteStage.RESOLVE_TS_SCOPE_ALWAYS

  override fun defaultProcess(
    ctx: KtToTsContext,
    source: Pair<ClientType, TsScope<*>>,
  ) = TsScope.TypeVal(meta = source.first, definition = TsTypeVal.Never)
}
