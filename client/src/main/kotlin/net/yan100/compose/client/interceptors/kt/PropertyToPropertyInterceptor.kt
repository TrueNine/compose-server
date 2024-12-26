package net.yan100.compose.client.interceptors.kt

import net.yan100.compose.client.contexts.KtToKtContext
import net.yan100.compose.client.interceptors.Interceptor
import net.yan100.compose.meta.client.ClientProp

abstract class PropertyToPropertyInterceptor : Interceptor<ClientProp, ClientProp, KtToKtContext> {
  override val executeStage: Interceptor.ExecuteStage = Interceptor.ExecuteStage.BEFORE_ALWAYS

  override fun defaultProcess(ctx: KtToKtContext, source: ClientProp): ClientProp = source
}
