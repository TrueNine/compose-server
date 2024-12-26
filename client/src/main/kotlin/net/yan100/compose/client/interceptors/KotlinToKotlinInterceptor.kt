package net.yan100.compose.client.interceptors

import net.yan100.compose.client.contexts.KtToKtContext
import net.yan100.compose.meta.client.ClientType

/**
 * kotlin 转换为 kotlin
 */
abstract class KotlinToKotlinInterceptor : Interceptor<ClientType, ClientType, KtToKtContext> {
  override val executeStage: Interceptor.ExecuteStage = Interceptor.ExecuteStage.BEFORE_ALWAYS
  override fun defaultProcess(ctx: KtToKtContext, source: ClientType): ClientType = source
}
