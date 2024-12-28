package net.yan100.compose.client.interceptors

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.meta.client.ClientType

/**
 * 将 Kotlin 类型描述转换为 Typescript 类型
 */
abstract class TsScopeInterceptor : Interceptor<ClientType, TsScope, KtToTsContext> {
  override val executeStage: ExecuteStage = ExecuteStage.RESOLVE_TS_SCOPE
  override fun supported(ctx: KtToTsContext, source: ClientType) = false
  override fun defaultProcess(ctx: KtToTsContext, source: ClientType) = TsScope.TypeVal(
    meta = source,
    definition = TsTypeVal.Never
  )
}
