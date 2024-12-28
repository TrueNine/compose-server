package net.yan100.compose.client.interceptors

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToKtContext
import net.yan100.compose.meta.client.ClientType

/**
 * kotlin 转换为 kotlin
 */
abstract class KotlinToKotlinInterceptor : Interceptor<ClientType, ClientType, KtToKtContext> {
  override val executeStage: ExecuteStage = ExecuteStage.LOOP_RESOLVE_CLASS
  override fun defaultProcess(ctx: KtToKtContext, source: ClientType): ClientType = source
}
