package net.yan100.compose.client.interceptors

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToKtContext
import net.yan100.compose.meta.client.ClientProp

abstract class ClientPropToClientPropInterceptor : Interceptor<ClientProp, ClientProp, KtToKtContext> {
  override val executeStage: ExecuteStage = ExecuteStage.LOOP_RESOLVE_CLASS
  override fun defaultProcess(ctx: KtToKtContext, source: ClientProp): ClientProp = source
}
