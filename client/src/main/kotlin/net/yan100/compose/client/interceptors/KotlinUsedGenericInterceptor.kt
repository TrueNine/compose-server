package net.yan100.compose.client.interceptors

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToKtContext
import net.yan100.compose.meta.client.ClientUsedGeneric

class KotlinUsedGenericInterceptor : Interceptor<ClientUsedGeneric, ClientUsedGeneric, KtToKtContext> {
  override val executeStage: ExecuteStage = ExecuteStage.CONVERT_QUALIFIER_NAME

  override fun supported(ctx: KtToKtContext, source: ClientUsedGeneric): Boolean {
    TODO("Not yet implemented")
  }

  override fun process(ctx: KtToKtContext, source: ClientUsedGeneric): ClientUsedGeneric {
    TODO("Not yet implemented")
  }

  override fun defaultProcess(ctx: KtToKtContext, source: ClientUsedGeneric): ClientUsedGeneric {
    TODO("Not yet implemented")
  }
}
