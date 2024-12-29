package net.yan100.compose.client.interceptors

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.meta.client.ClientType

/**
 * 该拦截器内可以
 * 获取到预处理好的 TypeVal
 */
abstract class TsPostReferenceInterceptor : Interceptor<ClientType, TsTypeVal<*>, KtToTsContext> {
  final override val executeStage: ExecuteStage = ExecuteStage.LOOP_RESOLVE_TS_REFERENCES
  override fun defaultProcess(ctx: KtToTsContext, source: ClientType): TsTypeVal<*> = TsTypeVal.Never
}
